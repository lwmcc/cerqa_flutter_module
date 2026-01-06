package com.cerqa.viewmodels

import com.cerqa.auth.AuthTokenProvider
import com.cerqa.models.Contact
import com.cerqa.models.CurrentContact
import com.cerqa.repository.ContactsRepository
import com.cerqa.repository.GroupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * UI State for CreateGroup screen
 */
data class CreateGroupUiState(
    val groupName: String = "",
    val isCheckingName: Boolean = false,
    val groupNameExists: Boolean = false,
    val groupNameError: String? = null,
    val contacts: List<CurrentContact> = emptyList(),
    val isLoadingContacts: Boolean = false,
    val selectedMembers: Set<String> = emptySet(), // Set of user IDs
    val showMembersBottomSheet: Boolean = false,
    val isCreatingGroup: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for creating a new group with name validation and member selection
 */
@OptIn(FlowPreview::class)
class CreateGroupViewModel(
    private val groupRepository: GroupRepository,
    private val contactsRepository: ContactsRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val mainDispatcher: CoroutineDispatcher
) {
    private val viewModelScope = CoroutineScope(mainDispatcher)

    private val _uiState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = _uiState.asStateFlow()

    // Flow for debouncing group name input
    private val groupNameFlow = MutableStateFlow("")

    init {
        // Set up debounced group name validation
        viewModelScope.launch {
            groupNameFlow
                .debounce(500) // Wait 500ms after user stops typing
                .filter { it.length >= 3 } // Only search if 3+ characters
                .collect { groupName ->
                    checkGroupNameAvailability(groupName)
                }
        }

        // Load user's contacts when ViewModel is created
        loadContacts()
    }

    /**
     * Update the group name and trigger debounced search
     */
    fun onGroupNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(
            groupName = name,
            groupNameError = null
        )

        // If name is less than 3 characters, clear validation state
        if (name.length < 3) {
            _uiState.value = _uiState.value.copy(
                isCheckingName = false,
                groupNameExists = false,
                groupNameError = if (name.isNotEmpty()) "Group name must be at least 3 characters" else null
            )
        } else {
            // Start checking indicator
            _uiState.value = _uiState.value.copy(isCheckingName = true)
        }

        // Emit to debounced flow
        groupNameFlow.value = name
    }

    /**
     * Check if group name is available (debounced)
     */
    private suspend fun checkGroupNameAvailability(groupName: String) {
        groupRepository.checkGroupNameExists(groupName)
            .onSuccess { exists ->
                _uiState.value = _uiState.value.copy(
                    isCheckingName = false,
                    groupNameExists = exists,
                    groupNameError = if (exists) "Group name already exists" else null
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isCheckingName = false,
                    groupNameError = "Error checking name: ${error.message}"
                )
            }
    }

    /**
     * Load the user's contacts for member selection
     */
    private fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingContacts = true)

            contactsRepository.fetchAllContactsWithInvites()
                .onSuccess { contactsList ->
                    // Filter to only current contacts (not pending invites)
                    val currentContacts = contactsList.filterIsInstance<CurrentContact>()
                    _uiState.value = _uiState.value.copy(
                        contacts = currentContacts,
                        isLoadingContacts = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingContacts = false,
                        error = "Failed to load contacts: ${error.message}"
                    )
                }
        }
    }

    /**
     * Toggle member selection
     */
    fun toggleMemberSelection(userId: String) {
        val currentSelected = _uiState.value.selectedMembers
        val newSelected = if (currentSelected.contains(userId)) {
            currentSelected - userId
        } else {
            currentSelected + userId
        }
        _uiState.value = _uiState.value.copy(selectedMembers = newSelected)
    }

    /**
     * Remove a selected member (from chips)
     */
    fun removeMember(userId: String) {
        val newSelected = _uiState.value.selectedMembers - userId
        _uiState.value = _uiState.value.copy(selectedMembers = newSelected)
    }

    /**
     * Show the members bottom sheet
     */
    fun showMembersSheet() {
        _uiState.value = _uiState.value.copy(showMembersBottomSheet = true)
    }

    /**
     * Hide the members bottom sheet
     */
    fun hideMembersSheet() {
        _uiState.value = _uiState.value.copy(showMembersBottomSheet = false)
    }

    /**
     * Create the group with selected members
     */
    fun createGroup() {
        val state = _uiState.value

        // Validation
        if (state.groupName.length < 3) {
            _uiState.value = state.copy(error = "Group name must be at least 3 characters")
            return
        }

        if (state.groupNameExists) {
            _uiState.value = state.copy(error = "Group name already exists")
            return
        }

        if (state.selectedMembers.isEmpty()) {
            _uiState.value = state.copy(error = "Please select at least one member")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingGroup = true, error = null)

            // Get current user ID
            val currentUserId = authTokenProvider.getCurrentUserId()
            if (currentUserId == null) {
                _uiState.value = _uiState.value.copy(
                    isCreatingGroup = false,
                    error = "You must be logged in to create a group"
                )
                return@launch
            }

            groupRepository.createGroup(
                groupName = state.groupName,
                memberUserIds = state.selectedMembers.toList(),
                creatorUserId = currentUserId
            )
                .onSuccess { groupId ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingGroup = false
                    )
                    // TODO: Navigate to the created group or show success message
                    println("CreateGroupViewModel: Group created successfully with ID: $groupId")
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingGroup = false,
                        error = "Failed to create group: ${error.message}"
                    )
                }
        }
    }

    /**
     * Get selected contacts as a list
     */
    fun getSelectedContacts(): List<CurrentContact> {
        return _uiState.value.contacts.filter { contact ->
            _uiState.value.selectedMembers.contains(contact.userId)
        }
    }
}
