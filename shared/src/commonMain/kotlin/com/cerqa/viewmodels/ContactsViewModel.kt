package com.cerqa.viewmodels

import com.cerqa.models.Contact
import com.cerqa.repository.ContactsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Shared ViewModel for managing contacts across iOS and Android.
 * Uses JWT-authenticated API calls via the ContactsRepository.
 */
class ContactsViewModel(
    private val repository: ContactsRepository
) {
    private val viewModelJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // State
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Fetch all contacts from the backend.
     */
    fun fetchContacts() {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            repository.fetchContacts()
                .onSuccess { contactsList ->
                    _contacts.value = contactsList
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to fetch contacts"
                }

            _isLoading.value = false
        }
    }

    /**
     * Search contacts by query string.
     */
    fun searchContacts(query: String): List<Contact> {
        if (query.isBlank()) {
            return _contacts.value
        }

        return _contacts.value.filter { contact ->
            contact.name?.contains(query, ignoreCase = true) == true ||
            contact.phone?.contains(query, ignoreCase = true) == true ||
            contact.email?.contains(query, ignoreCase = true) == true ||
            contact.userName?.contains(query, ignoreCase = true) == true
        }
    }

    /**
     * Find a user by phone number.
     */
    suspend fun findUserByPhone(phone: String): Contact? {
        return repository.findUserByPhone(phone).getOrNull()
    }

    /**
     * Add a new contact by user ID.
     */
    fun addContact(contactUserId: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            repository.addContact(contactUserId)
                .onSuccess {
                    // Refresh the contacts list
                    fetchContacts()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to add contact"
                }

            _isLoading.value = false
        }
    }

    /**
     * Add a contact by phone number (finds user first, then adds).
     */
    fun addContactByPhone(phone: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            // First find the user by phone
            repository.findUserByPhone(phone)
                .onSuccess { user ->
                    if (user != null) {
                        // Then add them as a contact
                        repository.addContact(user.id)
                            .onSuccess {
                                fetchContacts()
                            }
                            .onFailure { exception ->
                                _error.value = exception.message ?: "Failed to add contact"
                            }
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
     * Delete a contact by ID.
     */
    fun deleteContact(contactId: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            repository.deleteContact(contactId)
                .onSuccess {
                    // Remove from local list
                    _contacts.value = _contacts.value.filter { it.id != contactId }
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to delete contact"
                }

            _isLoading.value = false
        }
    }

    /**
     * Clear any error messages.
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Clean up when ViewModel is no longer needed.
     */
    fun onCleared() {
        viewModelJob.cancel()
    }
}