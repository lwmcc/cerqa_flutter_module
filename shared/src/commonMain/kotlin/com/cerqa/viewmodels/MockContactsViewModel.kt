package com.cerqa.viewmodels

import com.cerqa.models.Contact
import com.cerqa.repository.MockContactsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for testing contacts UI with mock data.
 */
class MockContactsViewModel(
    private val repository: MockContactsRepository
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
     * Fetch mock contacts.
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
