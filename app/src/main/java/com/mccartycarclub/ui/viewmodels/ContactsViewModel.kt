package com.mccartycarclub.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mccartycarclub.domain.ChannelModel
import com.mccartycarclub.domain.model.UserSearchResult
import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.repository.Contact
import com.mccartycarclub.repository.LocalRepo
import com.mccartycarclub.repository.NetDeleteResult
import com.mccartycarclub.repository.NetworkResponse
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.repository.ResponseException
import com.mccartycarclub.repository.UiStateResult
import com.mccartycarclub.repository.UserMapper
import com.mccartycarclub.ui.components.ContactCardEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
    private val localRepo: LocalRepo,
) : ViewModel() {

    sealed class UserContacts {
        data object Idle : UserContacts() // TODO: do I need this?
        data class Error(val message: String) : UserContacts()
        data object NoInternet : UserContacts()
        data object Success : UserContacts()
    }

    private val _receiverQueryPending = MutableStateFlow(false)
    val receiverQueryPending = _receiverQueryPending.asStateFlow()

    private val _hasPendingInvite = MutableStateFlow(false)
    val hasPendingInvite = _hasPendingInvite.asStateFlow()

    private val _hasConnection = MutableStateFlow(false)
    val hasConnection = _hasConnection.asStateFlow()

    private val _isCancellingInvite = MutableStateFlow(false)
    val isCancellingInvite = _isCancellingInvite.asStateFlow()

    private val _isSendingInvite = MutableStateFlow(false)
    val isSendingInvite = _isSendingInvite.asStateFlow()

    private val _inviteSentSuccess = MutableStateFlow(false)
    val inviteSentSuccess = _inviteSentSuccess.asStateFlow()

    private val _dataPending = MutableStateFlow(true)
    val dataPending = _dataPending.asStateFlow()

    private val _contactsState = MutableStateFlow<UserContacts>(UserContacts.Idle)
    val contactsState = _contactsState.asStateFlow()
    private val _contacts = mutableStateListOf<Contact>()
    val contacts: SnapshotStateList<Contact> get() = _contacts

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private var _loggedInUserId: String? = null
    private val loggedInUserId: String?
        get() = _loggedInUserId

    private val _query = MutableStateFlow<String?>(null)
    val searchResults: StateFlow<UiStateResult<UserSearchResult>> = _query
        .debounce(SEARCH_DELAY.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { userName ->
            flow {
                if (userName.isNullOrBlank()) {
                    emit(UiStateResult.Success(null))
                } else {
                    repo.searchUsers(_userId.value, userName!!)
                        .collect { response ->
                            when (response) {
                                is NetworkResponse.Success -> {
                                    emit(UiStateResult.Success(response.data))
                                }

                                is NetworkResponse.Error -> {
                                    emit(UiStateResult.Error(ResponseException("")))
                                }

                                NetworkResponse.NoInternet -> {
                                    emit(UiStateResult.NoInternet)
                                }
                            }
                        }
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiStateResult.Idle
        )

    init {
        viewModelScope.launch {
            _userId.value = localRepo.getUserId().first()
        }
    }

    fun onQueryChange(searchQuery: String) {
        _query.value = searchQuery
    }

    // TODO: use id instead of listIndex
    fun userConnectionEvent(listIndex: Int = 0, connectionEvent: ContactCardEvent) {
        _dataPending.value = true
        when (connectionEvent) {
            is ContactCardEvent.InviteConnectEvent -> {
                viewModelScope.launch {
                    _isSendingInvite.value = true

                    _userId.value?.let { userId ->
                        val channelName =
                            ChannelModel.NotificationsDirect.getName(connectionEvent.receiverUserId)

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

            // TODO: will be moved to cache
            is ContactCardEvent.DeleteReceivedInvite -> {
                viewModelScope.launch {
                    when (repo.deleteReceivedInviteToContact(
                        _userId.value.toString(),
                        connectionEvent.userId,
                    ).first()) {
                        is NetDeleteResult.Error -> {
                            // TODO: handle this
                        }

                        NetDeleteResult.NoInternet -> {
                            // TODO: handle this
                        }

                        NetDeleteResult.Success -> {
                            resetContactsList(
                                userId = connectionEvent.userId,
                                contactsState = UserContacts.Success,
                            )
                        }
                    }
                    _dataPending.value = false
                }
            }

            is ContactCardEvent.AcceptConnection -> {
                viewModelScope.launch {
                    when (val result = repo.createContact(
                        senderUserId = connectionEvent.connectionAccepted.senderUserId,
                        loggedInUserId = _userId.value.toString(),
                    ).first()) {
                        is NetDeleteResult.Error -> {
                            println("ContactsViewModel ***** ERROR ${result.exception.message}")
                        }

                        NetDeleteResult.Success -> {
                            val index =
                                _contacts.indexOfFirst { it.userId == connectionEvent.connectionAccepted.senderUserId }
                            if (index != -1) {
                                _contactsState.value = UserContacts.Success
                                _contacts[listIndex] =
                                    UserMapper.currentContactFrom(connectionEvent.connectionAccepted)
                            }
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
                        _userId.value.toString(),
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
                    _dataPending.value = false
                }
            }

            is ContactCardEvent.CancelSentInvite -> {
                viewModelScope.launch {
                    _isCancellingInvite.value = true

                    val cancelSuccess = repo.cancelInviteToConnect(
                        _userId.value.toString(),
                        connectionEvent.receiverUserId,
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
                            resetContactsList(
                                userId = connectionEvent.receiverUserId,
                                contactsState = UserContacts.Success,
                            )
                        }
                    }
                    _dataPending.value = false
                }
            }
        }
    }

    fun fetchAllContacts(loggedInUserId: String?) {
        if (loggedInUserId != null) {
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
                            _contactsState.value = UserContacts.Success
                            _contacts.clear() // TODO: is this needed? test it
                            _contacts.addAll(data.data)

                        }
                    }
                }
                _dataPending.value = false
            }
        }
    }

    private fun removeContact(id: String, pending: Boolean) {
        _dataPending.value = pending
        _contacts.removeAll { it.contactId == id }
    }

    private fun resetContactsList(userId: String, contactsState: UserContacts) {
        _contactsState.value = contactsState
        _contacts.removeAll { it.userId == userId }
    }

    companion object {
        const val SEARCH_DELAY: Int = 1_000
    }
}