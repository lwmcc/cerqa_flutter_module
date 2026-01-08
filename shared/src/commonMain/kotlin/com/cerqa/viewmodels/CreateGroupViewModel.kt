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
    val groupNameError: String? = null,
    val contacts: List<CurrentContact> = emptyList(),
    val isLoadingContacts: Boolean = false,
    val selectedMembers: Set<String> = emptySet(), // Set of user IDs
    val showMembersBottomSheet: Boolean = false,
    val isCreatingGroup: Boolean = false,
    val error: String? = null,
    val groupCreatedSuccessfully: Boolean = false
)

/**
 * ViewModel for creating a new group with member selection
 * Duplicate validation is handled by backend Lambda function
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

    init {
        // Load user's contacts when ViewModel is created
        loadContacts()
    }

    /**
     * Update the group name
     */
    fun onGroupNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(
            groupName = name,
            groupNameError = if (name.isNotEmpty() && name.length < 3) {
                "Group name must be at least 3 characters"
            } else {
                null
            }
        )
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
     * Backend will validate for duplicate names
     */
    fun createGroup() {
        val state = _uiState.value

        // Validation
        if (state.groupName.length < 3) {
            _uiState.value = state.copy(error = "Group name must be at least 3 characters")
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
                        isCreatingGroup = false,
                        groupCreatedSuccessfully = true
                    )
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
