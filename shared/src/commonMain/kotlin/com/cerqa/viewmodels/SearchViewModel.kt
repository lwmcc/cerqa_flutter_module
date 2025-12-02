package com.cerqa.viewmodels

import com.cerqa.models.*
import com.cerqa.repository.ContactsRepository
import com.cerqa.platform.DeviceContactsProvider
import com.cerqa.platform.SmsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class SearchUiState(
    val pending: Boolean = false,
    val idle: Boolean = true,
    val isSendingInvite: Boolean = false,
    val inviteSent: Boolean = false,
    val message: MessageType? = null,
    val results: List<SearchUser> = emptyList(),
    val appUsers: List<DeviceContact> = emptyList(),
    val nonAppUsers: List<DeviceContact> = emptyList(),
)

/**
 * Connection event from search/contact cards
 */
sealed class ContactCardConnectionEvent {
    data class InviteConnectEvent(val receiverUserId: String, val rowId: String) : ContactCardConnectionEvent()
    data class InvitePhoneNumberConnectEvent(val receiverPhoneNumber: String) : ContactCardConnectionEvent()
}

class SearchViewModel(
    private val repository: ContactsRepository,
    private val deviceContactsProvider: DeviceContactsProvider? = null,
    private val smsProvider: SmsProvider? = null
) {
    private val viewModelJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _query = MutableStateFlow<String?>(null)
    val query: StateFlow<String?> = _query.asStateFlow()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var deviceContactsLoaded = false

    init {
        // Start observing search query
        scope.launch {
            observeSearchQuery()
        }
    }

    /**
     * Load device contacts on demand (call this when user navigates to search/contacts screen)
     */
    fun loadDeviceContacts() {
        if (deviceContactsLoaded) return // Already loaded

        scope.launch {
            deviceContactsProvider?.let { provider ->
                _uiState.value = _uiState.value.copy(pending = true)

                val deviceContacts = provider.getDeviceContacts()

                // TODO: Categorize into app users vs non-app users
                // For now, just showing all as non-app users
                _uiState.value = _uiState.value.copy(
                    nonAppUsers = deviceContacts,
                    pending = false
                )

                deviceContactsLoaded = true
            }
        }
    }

    /**
     * Update the search query
     */
    fun onQueryChange(searchQuery: String) {
        _query.value = searchQuery
        _uiState.value = _uiState.value.copy(results = emptyList(), idle = searchQuery.isEmpty())
    }

    /**
     * Observe search query with debouncing
     */
    private suspend fun observeSearchQuery() {
        query
            .debounce(ContactsViewModel.SEARCH_DELAY.milliseconds)
            .distinctUntilChanged()
            .collectLatest { userName ->
                if (!userName.isNullOrEmpty()) {
                    performSearch(userName)
                } else {
                    _uiState.value = _uiState.value.copy(idle = true, results = emptyList())
                }
            }
    }

    /**
     * Perform user search
     */
    private suspend fun performSearch(userName: String) {
        _uiState.value = _uiState.value.copy(pending = true, idle = false)

        // Search for users by username
        val searchUsersResult = repository.searchUsersByUserName(userName)

        // Get all contacts to determine connection status
        val contactsResult = repository.fetchAllContactsWithInvites()

        val contacts = contactsResult.getOrNull() ?: emptyList()
        val searchUsers = searchUsersResult.getOrNull() ?: emptyList()

        // Map search results with their connection state
        val userResults = searchUsers.map { user ->
            val contact = contacts.find { it.userId == user.userId }
            val contactType = when (contact) {
                is ReceivedContactInvite -> ContactType.RECEIVED
                is SentInviteContactInvite -> ContactType.SENT
                is CurrentContact -> ContactType.CURRENT
                else -> null
            }

            SearchUser(
                id = user.id,
                userName = user.userName,
                avatarUri = user.avatarUri,
                userId = user.userId ?: "",
                phone = user.phone,
                connectButtonEnabled = true,
                contactType = contactType
            )
        }

        _uiState.value = _uiState.value.copy(
            results = userResults,
            pending = false
        )
    }

    /**
     * Handle invite sent event
     */
    fun inviteSentEvent(connectionEvent: ContactCardConnectionEvent) {
        when (connectionEvent) {
            is ContactCardConnectionEvent.InviteConnectEvent -> {
                // Disable the button for this user
                val updatedResults = _uiState.value.results.map { user ->
                    if (user.userId == connectionEvent.receiverUserId) {
                        user.copy(connectButtonEnabled = false)
                    } else {
                        user
                    }
                }
                _uiState.value = _uiState.value.copy(results = updatedResults)

                // Send the invite
                scope.launch {
                    _uiState.value = _uiState.value.copy(pending = true, isSendingInvite = true)

                    repository.sendInviteToConnect(connectionEvent.receiverUserId)
                        .onSuccess {
                            _uiState.value = _uiState.value.copy(
                                pending = false,
                                isSendingInvite = false,
                                inviteSent = true,
                                message = MessageType.INVITE_SENT
                            )
                            enableButtons()
                        }
                        .onFailure {
                            _uiState.value = _uiState.value.copy(
                                pending = false,
                                isSendingInvite = false,
                                message = MessageType.ERROR
                            )
                        }
                }
            }

            is ContactCardConnectionEvent.InvitePhoneNumberConnectEvent -> {
                // Send SMS invitation
                scope.launch {
                    smsProvider?.sendSms(
                        phoneNumber = connectionEvent.receiverPhoneNumber,
                        message = "Join me on the app!" // TODO: Make this configurable
                    )

                    _uiState.value = _uiState.value.copy(
                        message = MessageType.INVITE_SENT
                    )
                }
            }
        }
    }

    /**
     * Re-enable all connect buttons
     */
    private fun enableButtons() {
        val buttonsEnabled = _uiState.value.results.map {
            it.copy(connectButtonEnabled = true)
        }
        _uiState.value = _uiState.value.copy(results = buttonsEnabled)
    }
}