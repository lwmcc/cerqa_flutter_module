package com.mccartycarclub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.domain.model.LocalContact
import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.domain.usecases.user.GetUser
import com.mccartycarclub.repository.AmplifyDbRepo
import com.mccartycarclub.repository.NetResult
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.ui.callbacks.connectionclicks.ConnectionEvent
import com.mccartycarclub.utils.fetchUserId
import com.mccartycarclub.utils.fetchUserIdv2
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val user: GetUser,  // TODO: edit these names to use usecase
    private val userContacts: GetContacts,
    private val dbRepo: AmplifyDbRepo,
    private val repo: RemoteRepo,
) : ViewModel() {

    private val _hasPendingInvite = MutableStateFlow(false)
    val hasPendingInvite = _hasPendingInvite.asStateFlow()

    private val _hasConnection = MutableStateFlow(false)
    val hasConnection = _hasConnection.asStateFlow()

    private val _isSendingInvite = MutableStateFlow(false)
    val isSendingInvite = _isSendingInvite.asStateFlow()

    private val _isCancellingInvite = MutableStateFlow(false)
    val isCancellingInvite = _isCancellingInvite.asStateFlow()

    private val _receiverQueryPending = MutableStateFlow(true)
    val receiverQueryPending = _receiverQueryPending.asStateFlow()

    private val _localContacts = MutableStateFlow(emptyList<LocalContact>())
    val localContacts = _localContacts.asStateFlow()

    private val _query = MutableStateFlow("")
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
                            println("MainViewModel ***** ${data.exception.message}")
                        }

                        is NetResult.Success -> {

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

    fun getDeviceContacts() = userContacts.getDeviceContacts(localContacts = { contacts ->
        _localContacts.update { contacts }
    })

    fun inviteContact(userId: String, rowId: (String) -> Unit) {
        userContacts.addNewContact(userId, rowId = {
            it?.let {
                rowId(it)
            }
        })
    }

    fun fetchUserIdFromSentInvite(rowId: String) {
        user.fetchUserIdFromSentInvite(rowId, userId = {
            println("MainViewModel ***** USER ID $it")
        })
    }

    fun acceptContactInvite() {
        userContacts.acceptContactInvite()
    }

    fun fetchUserContacts(inviteReceiverUserId: String) {
        userContacts.getUserContacts(inviteReceiverUserId)
    }

    fun onQueryChange(searchQuery: String) {
        _query.value = searchQuery
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

    fun fetchContacts(inviteReceiverUserId: String) {
        viewModelScope.launch {
            repo.fetchContacts(inviteReceiverUserId)
        }
    }

    fun userConnectionEvent(connectionEvent: ConnectionEvent) {
        when (connectionEvent) {
            is ConnectionEvent.CancelEvent -> {
                viewModelScope.launch {
                    val userID = fetchUserId()
                    _isCancellingInvite.value = true
                    resetButtonToConnect(
                        repo.cancelInviteToConnect(
                            senderUserId = userID,
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