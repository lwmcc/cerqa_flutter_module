package com.mccartycarclub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.repository.Contact
import com.mccartycarclub.repository.NetResult
import com.mccartycarclub.repository.NetWorkResult
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.ui.callbacks.connectionclicks.ConnectionEvent
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val userContacts: GetContacts,
    private val repo: RemoteRepo,
) : ViewModel() {

    sealed class UserContacts {
        data object Pending : UserContacts()
        data class Error(val message: String) : UserContacts()
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

    private val _contacts = MutableStateFlow<UserContacts>(UserContacts.Pending)
    val contacts = _contacts.asStateFlow()

    private val _query = MutableStateFlow("") // TODO: null?
    val searchResults: StateFlow<NetResult<User?>> = _query
        .debounce(1000)
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { userName ->
            callbackFlow {
                repo.fetchUserByUserName(userName).collect { data ->
                    when (data) {
                        NetResult.Pending -> {

                        }

                        is NetResult.Error -> {
                            println("ContactsViewModel ***** ${data.exception.message}")
                        }

                        is NetResult.Success -> {
                            println("ContactsViewModel ***** SEARCH")
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
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NetResult.Pending
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

    fun userConnectionEvent(connectionEvent: ConnectionEvent) {
        when (connectionEvent) {
            is ConnectionEvent.CancelEvent -> {
                viewModelScope.launch {
                    val userID = fetchUserId()
                    _isCancellingInvite.value = true
                    resetButtonToConnect(
                        repo.cancelInviteToConnect(
                            senderUserId = userID/*connectionEvent.senderUserId*/,
                            receiverUserId = connectionEvent.receiverUserId,
                        )
                    )
                }
            }

            is ConnectionEvent.ConnectEvent -> {
                viewModelScope.launch {
                    val userID = fetchUserId()
                    _isSendingInvite.value = true
                    resetButtonsToPendingOnSuccess(
                        repo.sendInviteToConnect(
                            senderUserId = userID,
                            receiverUserId = connectionEvent.receiverUserId,
                        )
                    )
                }
            }

            ConnectionEvent.DisconnectEvent -> {

            }
        }
    }

    // TODO: rename this function
    fun fetchReceivedInvites(loggedInUserId: String) {
        viewModelScope.launch {
            val contacts: Deferred<List<Contact>> = async {
                when (val items = repo.fetchReceivedInvites(loggedInUserId).catch {
                    println("ContactsViewModel ***** ${it.message}")
                    // TODO: log this
                }.first()) {
                    is NetWorkResult.Error -> {
                        emptyList()
                    }

                    NetWorkResult.Pending -> {
                        emptyList()
                    }

                    is NetWorkResult.Success -> {
                        items.data ?: emptyList()
                    }
                }
            }

            _contacts.value = UserContacts.Success(contacts.await())

            val sentInvites: Deferred<List<Contact>> = async {
                when (val items = repo.fetchSentInvites(loggedInUserId).catch {
                    // TODO: log this
                }.first()) {
                    is NetWorkResult.Error -> {
                        emptyList()
                    }

                    NetWorkResult.Pending -> {
                        emptyList()
                    }

                    is NetWorkResult.Success -> {
                        items.data ?: emptyList()
                    }
                }
            }

            println("ContactsViewModel ***** SEND SIZE ${sentInvites.await().size}")
            sentInvites.await().forEach { item ->
                println("ContactsViewModel ***** AWAIT ${item.userName}")
            }
/*
            repo.fetchContacts(loggedInUserId).collect { data ->
                println("ContactsViewModel ***** AWAIT CONTACTS ${data.data}")
            }
            }
 */

            repo.fetchContacts(loggedInUserId)

            // TODO: testing
        }
    }

    fun contactButtonClickAction(event: ContactCardEvent) {
        when (event) {
            is ContactCardEvent.DeleteReceivedInvite -> {
                println("ContactsViewModel ***** DeleteReceivedInvite")
            }

            is ContactCardEvent.Connect -> {
                viewModelScope.launch {
                    repo.createContact(event.connectionAccepted).collect { data ->
                        when (data) {
                            NetResult.Pending -> {
                                println("ContactsViewModel ***** PENDING")
                            }

                            is NetResult.Error -> {
                                println("ContactsViewModel ***** ERROR ")
                            }

                            is NetResult.Success -> {
                                println("ContactsViewModel ***** SUCCESS")
                            }
                        }
                    }
                }
            }

            is ContactCardEvent.CancelSentInvite -> {

            }

            is ContactCardEvent.DeleteContact -> {

            }
        }
    }

/*    fun fetchContacts(fetchContacts: String) {
        viewModelScope.launch {
            repo.fetchContacts(fetchContacts)
        }
    }*/

    fun fetchUserContacts(inviteReceiverUserId: String) {
        userContacts.getUserContacts(inviteReceiverUserId)
    }

    suspend fun recevierHasExistingInvite() {
        // repo.hasExistingInvite()
    }

    suspend fun usersHaveExistingConnection(
        senderUserId: String,
        receiverUserId: String,
    ) {
        repo.contactExists(senderUserId, receiverUserId).collect { hasConnection ->
            hasConnection
        }
    }

    private fun resetButtonsToPendingOnSuccess(success: Boolean) {
        if (success) {
            _receiverQueryPending.value = false
            _hasConnection.value = false
            _hasPendingInvite.value = true

            // Re-enable cancel invite button
            _isCancellingInvite.value = false
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
}