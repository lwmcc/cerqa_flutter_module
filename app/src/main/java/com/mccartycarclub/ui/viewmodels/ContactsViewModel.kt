package com.mccartycarclub.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.repository.Contact
import com.mccartycarclub.repository.NetDeleteResult
import com.mccartycarclub.repository.NetSearchResult
import com.mccartycarclub.repository.NetworkResponse
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.repository.UserMapper
import com.mccartycarclub.ui.components.ContactCardEvent
import com.mccartycarclub.utils.fetchUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val userContacts: GetContacts,
    private val repo: RemoteRepo,
) : ViewModel() {

    sealed class UserContacts {
        data object Idle : UserContacts()
        data class Error(val message: String) : UserContacts()
        data object NoInternet : UserContacts()
        data class Success(val data: List<Contact>) : UserContacts()
    }

    private val _receiverQueryPending = MutableStateFlow(true)
    val receiverQueryPending = _receiverQueryPending.asStateFlow()

    private val _hasPendingInvite = MutableStateFlow(false)
    val hasPendingInvite = _hasPendingInvite.asStateFlow()

    private val _hasConnection = MutableStateFlow(false)
    val hasConnection = _hasConnection.asStateFlow()

    private val _isCancellingInvite = MutableStateFlow(false)
    val isCancellingInvite = _isCancellingInvite.asStateFlow()

    private val _isSendingInvite = MutableStateFlow(false)
    val isSendingInvite = _isSendingInvite.asStateFlow()

    private val _contactsState = MutableStateFlow<UserContacts>(UserContacts.Idle)
    val contactsState = _contactsState.asStateFlow()

    private val _dataPending = MutableStateFlow(true)
    val dataPending = _dataPending.asStateFlow()

    private val _contacts = mutableStateListOf<Contact>()
    val contacts: SnapshotStateList<Contact> get() = _contacts

    private var _loggedInUserId: String? = null
    val loggedInUserId: String?
        get() = _loggedInUserId

    private val _query = MutableStateFlow<String?>(null)
    val searchResults: StateFlow<NetSearchResult<User?>> = _query
        .debounce(SEARCH_DELAY.milliseconds)
        .filter { !it.isNullOrBlank() }
        .distinctUntilChanged()
        .flatMapLatest { userName ->
            userName?.let { name ->
                callbackFlow {
                    repo.fetchUserByUserName(name).collect { data ->
                        when (data) {
                            NetSearchResult.Idle -> {

                            }

                            NetSearchResult.Pending -> {

                            }

                            is NetSearchResult.Error -> {
                                println("ContactsViewModel ***** ${data.exception.message}")
                            }

                            is NetSearchResult.Success -> {
                                trySend(data)
                                fetchUserId { loggedIn ->
                                    if (loggedIn.loggedIn) {
                                        loggedIn.userId?.let { userId ->
                                            _receiverQueryPending.value = true
                                            viewModelScope.launch {
                                                val hasConnection: Deferred<Boolean> = async {
                                                    repo.contactExists(
                                                        userId,
                                                        data.data?.userId.toString(),
                                                    ).firstOrNull() ?: false
                                                }

                                                val hasExistingInvite = async {
                                                    repo.hasExistingInvite(
                                                        userId,
                                                        data.data?.userId.toString()
                                                    ).firstOrNull() ?: false
                                                }

                                                val connection = hasConnection.await()
                                                val invite = hasExistingInvite.await()

                                                _receiverQueryPending.value = false
                                                _hasPendingInvite.value = invite
                                                _hasConnection.value = connection
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    awaitClose {
                        // TODO: do I need this?
                        // YES, what should i put here?
                    }
                }
            } ?: flow { emit(NetSearchResult.Pending) }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NetSearchResult.Idle
        )

    fun onQueryChange(searchQuery: String) {
        println("ContactsViewModel ***** ${searchQuery}")
        _query.value = searchQuery
    }

    fun inviteContact(userId: String, rowId: (String) -> Unit) {
        userContacts.addNewContact(userId, rowId = {
            it?.let {
                rowId(it)
            }
        })
    }

    fun acceptContactInvite() {
        userContacts.acceptContactInvite()
    }

    fun userConnectionEvent(listIndex: Int = 0, connectionEvent: ContactCardEvent) {
        _dataPending.value = true
        when (connectionEvent) {
            is ContactCardEvent.InviteConnectEvent -> {
                viewModelScope.launch {
                    val userID = fetchUserId()
                    _isSendingInvite.value = true

                    val inviteSuccess =
                        repo.sendInviteToConnect(_loggedInUserId!!, connectionEvent.receiverUserId)

                    if (inviteSuccess) {
                        fetchReceivedInvites(_loggedInUserId!!)
                    } else {
                        println("ContactsViewModel ***** INVITE ERROR")
                    }
                }
            }

            // TODO: will be moved to cache
            is ContactCardEvent.DeleteReceivedInvite -> {
                viewModelScope.launch {
                    when (val result =
                        repo.deleteReceivedInviteToContact(loggedInUserId!!, connectionEvent.userId)
                            .first()) {
                        is NetDeleteResult.Error -> {

                        }

                        NetDeleteResult.NoInternet -> {

                        }

                        NetDeleteResult.Success -> {
                            removeContact(connectionEvent.userId, false)
                        }
                    }
                }
            }

            is ContactCardEvent.AcceptConnection -> {
                viewModelScope.launch {
                    when (val result = repo.createContact(
                        connectionEvent.connectionAccepted.senderUserId,
                        loggedInUserId!!,
                    ).first()) {
                        is NetDeleteResult.Error -> {
                            println("ContactsViewModel ***** ERROR ${result.exception.message}")
                        }

                        NetDeleteResult.Success -> {
                            _contacts[listIndex] =
                                UserMapper.currentContactFrom(connectionEvent.connectionAccepted)
                        }

                        NetDeleteResult.NoInternet -> {
                            println("ContactsViewModel ***** No internet")
                        }
                    }
                    _dataPending.value = false
                }
            }

            is ContactCardEvent.DeleteContact -> {
                viewModelScope.launch {
                    when (val result = repo.deleteContact(
                        _loggedInUserId!!,
                        connectionEvent.contactId,
                    ).first()) {
                        is NetDeleteResult.Error -> {
                            // TODO: log this
                            result.exception.message ?: ""
                        }

                        NetDeleteResult.NoInternet -> {

                        }

                        NetDeleteResult.Success -> {
                            removeContact(connectionEvent.contactId, false)
                        }
                    }
                }
            }

            is ContactCardEvent.CancelSentInvite -> {
                viewModelScope.launch {
                    _isCancellingInvite.value = true

                    val resetButton = repo.cancelInviteToConnect(
                        _loggedInUserId!!,
                        connectionEvent.receiverUserId,
                    ).first()

                    when (resetButton) {
                        is NetDeleteResult.Error -> {
                            // TODO: show error message
                            //resetButtonToDeleteInvite(false)
                        }

                        NetDeleteResult.NoInternet -> {
                            // TODO: show no internet message
                            // resetButtonToDeleteInvite(false)
                        }

                        NetDeleteResult.Success -> {
                            removeContact(connectionEvent.receiverUserId, false)
                        }
                    }
                }
            }
        }
    }

    // TODO: rename this function
    fun fetchReceivedInvites(loggedInUserId: String) {
        _dataPending.value = true
        viewModelScope.launch {
            when (val data = repo.fetchAllContacts(loggedInUserId).first()) {
                is NetworkResponse.Error -> {
                    _contactsState.value =
                            // TODO: remove message will use string
                        UserContacts.Error(data.exception.message ?: "some message here")
                }

                is NetworkResponse.NoInternet -> {
                    // TODO: remove message will use string
                    _contactsState.value = UserContacts.NoInternet
                }

                is NetworkResponse.Success -> {
                    if (data.data != null) {
                        // TODO: don't need to pass data to Succes have it in _contacts
                        _contactsState.value = UserContacts.Success(data.data)

                        _contacts.clear()
                        _contacts.addAll(data.data)
                    }
                }
            }
        }
        _dataPending.value = false
    }

    suspend fun usersHaveExistingConnection(
        senderUserId: String,
        receiverUserId: String,
    ) {
        repo.contactExists(senderUserId, receiverUserId).collect { hasConnection ->
            hasConnection
        }
    }

    fun setLoggedInUserId(loggedInUserId: String) {
        _loggedInUserId = loggedInUserId
    }

    fun isDataPending(dataPending: Boolean) {
        _dataPending.value = dataPending
    }

    private fun removeContact(id: String, pending: Boolean) {
        _dataPending.value = pending
        _contacts.removeAll { it.contactId == id }
    }

    private fun resetButtonsToPendingOnSuccess(success: Boolean) {
        if (success) {
            _receiverQueryPending.value = false
            _hasConnection.value = false
            _hasPendingInvite.value = true

            // Re-enable cancel invite button
            _isCancellingInvite.value = false
            fetchReceivedInvites(_loggedInUserId!!)
        } else {
            _isSendingInvite.value = false
        }
    }

    private fun resetButtonToConnect(success: Boolean) {
        if (success) {
            _receiverQueryPending.value = false
            _hasConnection.value = false
            _hasPendingInvite.value = false

            // Re-enable Invite button
            _isSendingInvite.value = false
        } else {
            _isCancellingInvite.value = false
        }
    }

    private fun resetButtonToDeleteInvite(success: Boolean) {
        if (success) {

        } else {

        }
    }

    companion object {
        const val SEARCH_DELAY: Int = 1_000
    }
}