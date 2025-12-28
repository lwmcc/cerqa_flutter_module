package com.cerqa.viewmodels

import androidx.lifecycle.viewModelScope
import com.cerqa.models.*
import com.cerqa.notifications.Notifications
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
    private val contactsRepository: ContactsRepository,
    private val notifications: Notifications,
    private val deviceContactsProvider: DeviceContactsProvider? = null,
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
        println("ContactsViewModel: fetchAllContacts called")
        _uiState.value = _uiState.value.copy(pending = true)
        scope.launch {
            contactsRepository.fetchAllContactsWithInvites()
                .onSuccess { contactsList ->
                    _uiState.value = _uiState.value.copy(
                        pending = false,
                        contacts = contactsList
                    )
                    _contacts.value = contactsList
                }
                .onFailure { exception ->
                    println("ContactsViewModel: fetchAllContacts FAILURE - ${exception.message}")
                    exception.printStackTrace()
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
        println("ContactsViewModel: userConnectionEvent - event: $connectionEvent")
        _uiState.value = _uiState.value.copy(pending = true)

        when (connectionEvent) {
            is ContactCardEvent.DeleteReceivedInvite -> {
                println("ContactsViewModel: userConnectionEvent - DeleteReceivedInvite for userId: ${connectionEvent.userId}")
                scope.launch {
                    deleteReceivedInviteToConnect(connectionEvent.userId)
                }
            }

            is ContactCardEvent.AcceptConnection -> {
                println("ContactsViewModel: userConnectionEvent - AcceptConnection for senderUserId: ${connectionEvent.senderUserId}")
                scope.launch {
                    acceptConnection(listIndex, connectionEvent.senderUserId)
                }
            }

            is ContactCardEvent.DeleteContact -> {
                println("ContactsViewModel: userConnectionEvent - DeleteContact for contactId: ${connectionEvent.contactId}")
                scope.launch {
                    deleteContact(connectionEvent.contactId)
                }
            }

            is ContactCardEvent.CancelSentInvite -> {
                println("ContactsViewModel: userConnectionEvent - CancelSentInvite for receiverUserId: ${connectionEvent.receiverUserId}")
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
        println("ContactsViewModel: deleteReceivedInviteToConnect - userId: $userId")

        // Optimistically remove the invite from UI immediately
        val updatedContacts = _uiState.value.contacts.filter {
            !(it is ReceivedContactInvite && it.userId == userId)
        }
        _uiState.value = _uiState.value.copy(contacts = updatedContacts, pending = true)
        _contacts.value = updatedContacts

        contactsRepository.deleteReceivedInvite(userId)
            .onSuccess {
                println("ContactsViewModel: deleteReceivedInviteToConnect - SUCCESS, keeping optimistic update")
                // Just update pending state - the invite is already removed from UI
                _uiState.value = _uiState.value.copy(pending = false)
            }
            .onFailure { error ->
                println("ContactsViewModel: deleteReceivedInviteToConnect - FAILURE: ${error.message}")
                error.printStackTrace()
                // Revert the optimistic update on failure by fetching fresh data
                fetchAllContacts()
                _uiState.value = _uiState.value.copy(message = MessageType.ERROR)
            }
    }

    /**
     * Accept a connection invite
     */
    private suspend fun acceptConnection(listIndex: Int, senderUserId: String) {
        println("ContactsViewModel: acceptConnection - senderUserId: $senderUserId")

        // Optimistically convert the received invite to a current contact
        val invite = _uiState.value.contacts.find {
            it is ReceivedContactInvite && it.userId == senderUserId
        } as? ReceivedContactInvite

        if (invite == null) {
            _uiState.value = _uiState.value.copy(message = MessageType.ERROR)
            return
        }

        val updatedContacts = _uiState.value.contacts.map { contact ->
            if (contact is ReceivedContactInvite && contact.userId == senderUserId) {
                CurrentContact(
                    contactId = contact.contactId,
                    userId = contact.userId,
                    userName = contact.userName,
                    name = contact.name,
                    avatarUri = contact.avatarUri,
                    phoneNumber = contact.phoneNumber
                )
            } else {
                contact
            }
        }
        _uiState.value = _uiState.value.copy(contacts = updatedContacts, pending = true)
        _contacts.value = updatedContacts

        contactsRepository.acceptInvite(senderUserId)
            .onSuccess { userContact ->
                println("ContactsViewModel: acceptConnection - SUCCESS, updating contact with backend ID")
                // Update the contact with the real contactId from backend
                val finalContacts = _uiState.value.contacts.map { contact ->
                    if (contact is CurrentContact && contact.userId == senderUserId) {
                        contact.copy(contactId = userContact.id ?: contact.contactId)
                    } else {
                        contact
                    }
                }
                _uiState.value = _uiState.value.copy(contacts = finalContacts, pending = false)
                _contacts.value = finalContacts
            }
            .onFailure { error ->
                println("ContactsViewModel: acceptConnection - FAILURE: ${error.message}")
                error.printStackTrace()
                // Revert the optimistic update on failure by fetching fresh data
                fetchAllContacts()
                _uiState.value = _uiState.value.copy(message = MessageType.ERROR)
            }
    }

    /**
     * Delete contact
     */
    private suspend fun deleteContact(contactId: String) {
        println("ContactsViewModel: deleteContact - contactId: $contactId")

        // Optimistically remove the contact from UI immediately
        val updatedContacts = _uiState.value.contacts.filter { it.contactId != contactId }
        _uiState.value = _uiState.value.copy(contacts = updatedContacts, pending = true)
        _contacts.value = updatedContacts

        contactsRepository.deleteContact(contactId)
            .onSuccess {
                println("ContactsViewModel: deleteContact - SUCCESS, keeping optimistic update")
                // Just update pending state - the contact is already removed from UI
                _uiState.value = _uiState.value.copy(pending = false)
            }
            .onFailure { error ->
                println("ContactsViewModel: deleteContact - FAILURE: ${error.message}")
                error.printStackTrace()
                // Revert the optimistic update on failure by fetching fresh data
                fetchAllContacts()
                _uiState.value = _uiState.value.copy(message = MessageType.ERROR)
            }
    }

    private suspend fun cancelInviteToConnect(receiverUserId: String) {
        println("ContactsViewModel: cancelInviteToConnect - receiverUserId: $receiverUserId")

        // Optimistically remove the sent invite from UI immediately
        val updatedContacts = _uiState.value.contacts.filter {
            !(it is SentInviteContactInvite && it.userId == receiverUserId)
        }
        _uiState.value = _uiState.value.copy(contacts = updatedContacts, pending = true)
        _contacts.value = updatedContacts

        contactsRepository.cancelInviteToConnect(receiverUserId)
            .onSuccess {
                println("ContactsViewModel: cancelInviteToConnect - SUCCESS, keeping optimistic update")
                // Just update pending state - the invite is already removed from UI
                _uiState.value = _uiState.value.copy(pending = false)
            }
            .onFailure { error ->
                println("ContactsViewModel: cancelInviteToConnect - FAILURE: ${error.message}")
                error.printStackTrace()
                // Revert the optimistic update on failure by fetching fresh data
                fetchAllContacts()
                _uiState.value = _uiState.value.copy(message = MessageType.ERROR)
            }
    }

    /**
     * Add a sent invite directly to the contacts list with full user details
     * This is called when an invite is sent from search to avoid re-fetching
     */
    fun addSentInvite(
        inviteId: String,
        receiverUserId: String,
        receiverUserName: String?,
        receiverName: String?,
        senderUserId: String
    ) {
        scope.launch {
            println("ContactsViewModel: addSentInvite - inviteId: $inviteId, receiver: $receiverUserName")

            // Get current user ID if not provided
            val actualSenderUserId = if (senderUserId.isBlank()) {
                contactsRepository.getCurrentUserId() ?: ""
            } else {
                senderUserId
            }

            val newInvite = SentInviteContactInvite(
                senderUserId = actualSenderUserId,
                contactId = inviteId,
                userId = receiverUserId,
                userName = receiverUserName,
                name = receiverName,
                avatarUri = null,
                phoneNumber = null
            )

            val updatedContacts = _uiState.value.contacts + newInvite
            _uiState.value = _uiState.value.copy(contacts = updatedContacts)
            _contacts.value = updatedContacts

            println("ContactsViewModel: addSentInvite - SUCCESS, contacts list now has ${updatedContacts.size} items")
        }
    }

    fun sendInviteToConnect(
        receiverUserId: String,
        receiverUserName: String? = null,
        receiverName: String? = null,
        senderUserId: String? = null,
        senderName: String,
        senderUserName: String
    ) {
        _isSendingInvite.value = true
        scope.launch {
            contactsRepository.sendInviteToConnect(receiverUserId)
                .onSuccess { inviteId ->
                    println("ContactsViewModel: sendInviteToConnect - SUCCESS, adding to contacts list")
                    // Add the new sent invite to the contacts list
                    addSentInvite(
                        inviteId = inviteId,
                        receiverUserId = receiverUserId,
                        receiverUserName = receiverUserName,
                        receiverName = receiverName,
                        senderUserId = senderUserId ?: ""
                    )

                    // Send push notification to recipient via FCM
/*                    notifications.sendConnectionInviteNotification(
                        recipientUserId = receiverUserId,
                        senderName = senderName,
                        senderUserName = senderUserName,
                        inviteId = inviteId
                    ).onSuccess {
                        println("ContactsViewModel: FCM push notification sent to $receiverUserId")
                    }.onFailure { error ->
                        println("ContactsViewModel: Failed to send push notification: ${error.message}")
                        // Don't fail the whole operation if notification fails
                    }*/

                    _inviteSentSuccess.value = true
                    _isSendingInvite.value = false
                    _uiState.value = _uiState.value.copy(message = MessageType.INVITE_SENT)
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

            contactsRepository.addContact(contactUserId)
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
    fun addContactByPhone(phone: String, senderName: String, senderUserName: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            // First find the user by phone
            contactsRepository.findUserByPhone(phone)
                .onSuccess { user ->
                    if (user != null) {
                        // Then send them a connection invite
                        sendInviteToConnect(
                            receiverUserId = user.userId ?: "",
                            receiverUserName = user.userName,
                            receiverName = user.name,
                            senderName = senderName,
                            senderUserName = senderUserName
                        )
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

    fun loadDeviceContacts() {
        scope.launch {
/*            uiState = uiState.copy(pending = true)
            val contactsWrapper = contactsRepository.getDeviceContacts()

            uiState = uiState.copy(
                pending = false,
                appUsers = contactsWrapper.appUsers,
                nonAppUsers = contactsWrapper.nonAppUsers,
            )*/
        }
    }

    companion object {
        const val SEARCH_DELAY: Int = 1_000
    }
}