package com.cerqa.viewmodels

import com.cerqa.models.*
import com.cerqa.repository.ContactsRepository
import com.cerqa.platform.DeviceContactsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for Contacts screen
 */
data class ContactsUiState(
    val pending: Boolean = false,
    val contacts: List<Contact> = emptyList(),
    val message: MessageType? = null,
)

/**
 * Message types for user feedback
 */
enum class MessageType {
    ERROR, NO_INTERNET, SUCCESS, INVITE_SENT
}

/**
 * Contact card events
 */
sealed class ContactCardEvent {
    data class CancelSentInvite(val receiverUserId: String) : ContactCardEvent()
    data class AcceptConnection(val senderUserId: String) : ContactCardEvent()
    data class DeleteContact(val contactId: String) : ContactCardEvent()
    data class DeleteReceivedInvite(val userId: String) : ContactCardEvent()
}

/**
 * Shared ViewModel for managing contacts across iOS and Android
 * Uses JWT-authenticated API calls via the ContactsRepository
 */
class ContactsViewModel(
    private val repository: ContactsRepository,
    private val deviceContactsProvider: DeviceContactsProvider? = null
) {
    private val viewModelJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSendingInvite = MutableStateFlow(false)
    val isSendingInvite: StateFlow<Boolean> = _isSendingInvite.asStateFlow()

    private val _inviteSentSuccess = MutableStateFlow(false)
    val inviteSentSuccess: StateFlow<Boolean> = _inviteSentSuccess.asStateFlow()

    /**
     * Fetch all contacts, current contacts, and invites
     */
    fun fetchAllContacts() {
        _uiState.value = _uiState.value.copy(pending = true)
        scope.launch {
            repository.fetchAllContactsWithInvites()
                .onSuccess { contactsList ->
                    _uiState.value = _uiState.value.copy(
                        pending = false,
                        contacts = contactsList
                    )
                    _contacts.value = contactsList
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        pending = false,
                        message = MessageType.ERROR
                    )
                    _error.value = exception.message ?: "Failed to fetch contacts"
                }
        }
    }

    /**
     * Handle user connection events, accept, reject, cancel, delete
     */
    fun userConnectionEvent(listIndex: Int = 0, connectionEvent: ContactCardEvent) {
        _uiState.value = _uiState.value.copy(pending = true)

        when (connectionEvent) {
            is ContactCardEvent.DeleteReceivedInvite -> {
                scope.launch {
                    deleteReceivedInviteToConnect(connectionEvent.userId)
                }
            }

            is ContactCardEvent.AcceptConnection -> {
                scope.launch {
                    acceptConnection(listIndex, connectionEvent.senderUserId)
                }
            }

            is ContactCardEvent.DeleteContact -> {
                scope.launch {
                    deleteContact(connectionEvent.contactId)
                }
            }

            is ContactCardEvent.CancelSentInvite -> {
                scope.launch {
                    cancelInviteToConnect(connectionEvent.receiverUserId)
                }
            }
        }
    }

    /**
     * Delete a received connection invite
     */
    private suspend fun deleteReceivedInviteToConnect(userId: String) {
        repository.deleteReceivedInvite(userId)
            .onSuccess {
                val contacts = _uiState.value.contacts.filterNot { it.userId == userId }
                _uiState.value = _uiState.value.copy(contacts = contacts, pending = false)
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(pending = false, message = MessageType.ERROR)
            }
    }

    /**
     * Accept a connection invite
     */
    private suspend fun acceptConnection(listIndex: Int, senderUserId: String) {
        repository.acceptInvite(senderUserId)
            .onSuccess { userContact ->
                // Refresh contacts to show the new contact
                fetchAllContacts()
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(pending = false, message = MessageType.ERROR)
            }
    }

    /**
     * Delete contact
     */
    private suspend fun deleteContact(contactId: String) {
        repository.deleteContact(contactId)
            .onSuccess {
                val contacts = _uiState.value.contacts.filterNot {
                    it.contactId == contactId || (it as? CurrentContact)?.contactId == contactId
                }
                _uiState.value = _uiState.value.copy(contacts = contacts, pending = false)
                _contacts.value = contacts
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(pending = false, message = MessageType.ERROR)
            }
    }

    private suspend fun cancelInviteToConnect(receiverUserId: String) {
        repository.cancelInviteToConnect(receiverUserId)
            .onSuccess {
                val contacts = _uiState.value.contacts.filterNot { it.userId == receiverUserId }
                _uiState.value = _uiState.value.copy(contacts = contacts, pending = false)
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(pending = false, message = MessageType.ERROR)
            }
    }

    fun sendInviteToConnect(receiverUserId: String) {
        _isSendingInvite.value = true
        scope.launch {
            repository.sendInviteToConnect(receiverUserId)
                .onSuccess {
                    _inviteSentSuccess.value = true
                    _isSendingInvite.value = false
                    _uiState.value = _uiState.value.copy(message = MessageType.INVITE_SENT)
                    // Refresh contacts to show the new sent invite
                    fetchAllContacts()
                }
                .onFailure {
                    _isSendingInvite.value = false
                    _uiState.value = _uiState.value.copy(message = MessageType.ERROR)
                }
        }
    }

    /**
     * Add new contact by user ID
     */
    fun addContact(contactUserId: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            repository.addContact(contactUserId)
                .onSuccess {
                    // Refresh the contacts list
                    fetchAllContacts()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to add contact"
                }

            _isLoading.value = false
        }
    }

    /**
     * Add contact by phone number
     */
    fun addContactByPhone(phone: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            // First find the user by phone
            repository.findUserByPhone(phone)
                .onSuccess { user ->
                    if (user != null) {
                        // Then send them a connection invite
                        sendInviteToConnect(user.userId ?: "")
                    } else {
                        _error.value = "User with phone number $phone not found"
                    }
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to find user"
                }

            _isLoading.value = false
        }
    }

    /**
     * Search contacts by query string this is a local search
     */
    fun searchContacts(query: String): List<Contact> {
        if (query.isBlank()) {
            return _contacts.value
        }

        return _contacts.value.filter { contact ->
            contact.name?.contains(query, ignoreCase = true) == true ||
                    contact.phoneNumber?.contains(query, ignoreCase = true) == true ||
                    contact.userName?.contains(query, ignoreCase = true) == true
        }
    }

    /**
     * Get device contacts
     */
    suspend fun getDeviceContacts(): ContactsWrapper {
        return deviceContactsProvider?.let { provider ->
            val deviceContacts = provider.getDeviceContacts()
            // Separate into app users and non-app users based on current contacts
            val currentContacts = _contacts.value.filterIsInstance<CurrentContact>()
            val contactPhoneNumbers = currentContacts.mapNotNull { it.phoneNumber }.toSet()

            val appUsers = deviceContacts.filter { deviceContact ->
                deviceContact.phoneNumbers.any { it in contactPhoneNumbers }
            }

            val nonAppUsers = deviceContacts.filter { deviceContact ->
                deviceContact.phoneNumbers.none { it in contactPhoneNumbers }
            }

            ContactsWrapper(appUsers = appUsers, nonAppUsers = nonAppUsers)
        } ?: ContactsWrapper(emptyList(), emptyList())
    }

    companion object {
        const val SEARCH_DELAY: Int = 1_000
    }
}