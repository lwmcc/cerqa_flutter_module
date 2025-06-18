package com.mccartycarclub.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mccartycarclub.domain.ChannelModel
import com.mccartycarclub.repository.Contact
import com.mccartycarclub.repository.LocalRepo
import com.mccartycarclub.repository.NetDeleteResult
import com.mccartycarclub.repository.NetworkResponse
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.repository.UserMapper
import com.mccartycarclub.ui.components.ConnectionAccepted
import com.mccartycarclub.ui.components.ContactCardEvent
import com.mccartycarclub.ui.shared.MessageTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val pending: Boolean = false,
    val contacts: List<Contact> = emptyList<Contact>(),
    val message: MessageTypes? = null,
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repo: RemoteRepo,
    private val localRepo: LocalRepo,
) : ViewModel() {

    private val _isSendingInvite = MutableStateFlow(false)
    val isSendingInvite = _isSendingInvite.asStateFlow()

    private val _inviteSentSuccess = MutableStateFlow(false)
    val inviteSentSuccess = _inviteSentSuccess.asStateFlow()

    var uiState by mutableStateOf(UiState())
        private set

    private val _contacts = mutableStateListOf<Contact>()
    val contacts: SnapshotStateList<Contact> get() = _contacts

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        viewModelScope.launch {
            _userId.value = localRepo.getUserId().first()
        }
    }

    fun userConnectionEvent(listIndex: Int = 0, connectionEvent: ContactCardEvent) {
        uiState = uiState.copy(pending = true)

        when (connectionEvent) {
            is ContactCardEvent.InviteConnectEvent -> {
                viewModelScope.launch {
                    _isSendingInvite.value = true

                    _userId.value?.let { userId ->
                        //val channelName =
                        //    ChannelModel.NotificationsDirect.getName(connectionEvent.receiverUserId)
                        // TODO: will uncomment when ready
                        //realtimePublishRepo.publish(channelName)
                        //realTime.createReceiverInviteSubscription(_userId.value.toString(), channel)

                        val data = repo.sendInviteToConnect(
                            senderUserId = _userId.value,
                            receiverUserId = connectionEvent.receiverUserId,
                            rowId = connectionEvent.rowId,
                        ).first()

                        when (data) {
                            is NetworkResponse.Error -> {
                                // TODO: log update ui
                            }

                            NetworkResponse.NoInternet -> {
                                // TODO: update ui
                            }

                            is NetworkResponse.Success -> {
                                _inviteSentSuccess.value = true
                            }
                        }
                    }
                }
            }

            is ContactCardEvent.DeleteReceivedInvite -> {
                viewModelScope.launch {
                    deleteReceivedInviteToConnect(connectionEvent.userId)
                }
            }

            is ContactCardEvent.AcceptConnection -> {
                viewModelScope.launch {
                    createContact(
                        listIndex,
                        connectionEvent.connectionAccepted.senderUserId,
                        connectionEvent.connectionAccepted,
                    )
                }
            }

            is ContactCardEvent.DeleteContact -> {
                viewModelScope.launch {
                    deleteContact(connectionEvent.contactId)
                }
            }

            is ContactCardEvent.CancelSentInvite -> {
                viewModelScope.launch {
                    cancelInviteToConnect(connectionEvent.receiverUserId)
                }
            }
        }
    }

    fun fetchAllContacts(loggedInUserId: String?) {
        if (loggedInUserId != null) {
            uiState = uiState.copy(pending = true)
            viewModelScope.launch {

                when (val data = repo.fetchAllContacts(loggedInUserId).first()) {
                    is NetworkResponse.Error -> {
                        uiState = uiState.copy(message = MessageTypes.Error)
                    }

                    is NetworkResponse.NoInternet -> {
                        uiState = uiState.copy(message = MessageTypes.NoInternet)
                    }

                    is NetworkResponse.Success -> {
                        uiState = uiState.copy(
                            pending = false,
                            contacts = data.data ?: emptyList(),
                        )
                    }
                }
            }
        }
    }

    private fun removeContact(id: String) {
        uiState = uiState.copy(pending = true)
        _contacts.removeAll { it.contactId == id }
    }

    private suspend fun deleteReceivedInviteToConnect(userId: String) {
        when (repo.deleteReceivedInviteToContact(
            _userId.value.toString(), userId,
        ).first()) {
            is NetDeleteResult.Error -> {
                // TODO: handle this
            }

            NetDeleteResult.NoInternet -> {
                // TODO: handle this
            }

            NetDeleteResult.Success -> {
                val contacts =
                    uiState.contacts.filterNot { it.userId == userId }
                uiState = uiState.copy(contacts = contacts)
            }
        }
        uiState = uiState.copy(pending = false)
    }

    private suspend fun createContact(
        listIndex: Int,
        senderUserId: String,
        connectionAccepted: ConnectionAccepted
    ) {
        when (val result = repo.createContact(
            senderUserId = senderUserId,
            loggedInUserId = _userId.value.toString(),
        ).first()) {
            is NetDeleteResult.Error -> {
                println("ContactsViewModel ***** ERROR ${result.exception.message}")
            }

            NetDeleteResult.Success -> {
                val contacts = uiState.contacts.toMutableList()
                contacts[listIndex] =
                    UserMapper.currentContactFrom(connectionAccepted)
                uiState = uiState.copy(contacts = contacts)
            }

            NetDeleteResult.NoInternet -> {
                println("ContactsViewModel ***** No internet")
            }
        }
        uiState = uiState.copy(pending = false)
    }

    private suspend fun deleteContact(contactId: String) {
        when (val result = repo.deleteContact(
            _userId.value.toString(),
            contactId,
        ).first()) {
            is NetDeleteResult.Error -> {
                // TODO: log this
                result.exception.message ?: ""
            }

            NetDeleteResult.NoInternet -> {

            }

            NetDeleteResult.Success -> {
                removeContact(contactId)
            }
        }
        uiState = uiState.copy(pending = false)
    }

    private suspend fun cancelInviteToConnect(receiverUserId: String) {
        uiState = uiState.copy(pending = true)
        val cancelSuccess = repo.cancelInviteToConnect(
            _userId.value.toString(),
            receiverUserId,
        ).first()

        when (cancelSuccess) { // TODO: change the name of this
            is NetDeleteResult.Error -> {
                // TODO: show error message
                //resetButtonToDeleteInvite(false)
            }

            NetDeleteResult.NoInternet -> {
                // TODO: show no internet message
                // resetButtonToDeleteInvite(false)
            }

            NetDeleteResult.Success -> {
                val contacts =
                    uiState.contacts.filterNot { it.userId == receiverUserId }
                uiState = uiState.copy(contacts = contacts)
            }
        }
        uiState = uiState.copy(pending = false)
    }

    companion object {
        const val SEARCH_DELAY: Int = 1_000
    }
}
