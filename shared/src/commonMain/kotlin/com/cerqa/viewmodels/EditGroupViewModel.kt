package com.cerqa.viewmodels

import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.ListUserGroupsQuery
import com.cerqa.models.CurrentContact
import com.cerqa.repository.ContactsRepository
import com.cerqa.repository.GroupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for EditGroup screen
 */
data class EditGroupUiState(
    val groupId: String = "",
    val originalGroupName: String = "",
    val groupName: String = "",
    val groupNameError: String? = null,
    val originalMemberIds: Set<String> = emptySet(),
    val members: List<GroupMember> = emptyList(),
    val availableContacts: List<CurrentContact> = emptyList(),
    val isLoadingGroup: Boolean = true,  // Start with loading true
    val isLoadingContacts: Boolean = false,
    val isSaving: Boolean = false,
    val showMembersBottomSheet: Boolean = false,
    val showRemoveConfirmation: Boolean = false,
    val memberToRemove: GroupMember? = null,
    val error: String? = null,
    val saveSuccessful: Boolean = false
) {
    val hasUnsavedChanges: Boolean
        get() {
            val nameChanged = groupName != originalGroupName
            val currentMemberIds = members.map { it.userId }.toSet()
            val membersChanged = currentMemberIds != originalMemberIds
            return nameChanged || membersChanged
        }

    val isValid: Boolean
        get() = groupName.length >= 3 && groupNameError == null && members.isNotEmpty()
}

/**
 * Represents a group member with their UserGroup ID for removal
 */
data class GroupMember(
    val userGroupId: String,
    val userId: String,
    val userName: String?,
    val name: String?
)

/**
 * ViewModel for editing an existing group
 */
class EditGroupViewModel(
    private val groupRepository: GroupRepository,
    private val contactsRepository: ContactsRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val mainDispatcher: CoroutineDispatcher
) {
    private val viewModelScope = CoroutineScope(mainDispatcher)

    private val _uiState = MutableStateFlow(EditGroupUiState())
    val uiState: StateFlow<EditGroupUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    /**
     * Initialize the screen with group data
     */
    fun loadGroup(groupId: String) {
        println("EditGroupViewModel: Loading group with id: $groupId")

        viewModelScope.launch {
            currentUserId = authTokenProvider.getCurrentUserId()
            println("EditGroupViewModel: Current user ID: $currentUserId")

            _uiState.value = _uiState.value.copy(
                groupId = groupId,
                isLoadingGroup = true,
                error = null
            )

            // Load group members
            groupRepository.getGroupMembers(groupId)
                .onSuccess { userGroups ->
                    println("EditGroupViewModel: Fetched ${userGroups.size} UserGroup entries")
                    userGroups.forEach { ug ->
                        println("EditGroupViewModel: UserGroup - id=${ug.id}, userId=${ug.userId}, userName=${ug.user?.userName}")
                    }

                    // Get the group name from first member's group data
                    val groupName = userGroups.firstOrNull()?.group?.name ?: ""

                    // Convert to GroupMember list, excluding current user
                    val members = userGroups
                        .filter { it.userId != currentUserId }
                        .map { userGroup ->
                            GroupMember(
                                userGroupId = userGroup.id,
                                userId = userGroup.userId ?: "",
                                userName = userGroup.user?.userName,
                                name = userGroup.user?.name
                            )
                        }
                    println("EditGroupViewModel: After filtering, ${members.size} members (excluding current user)")

                    _uiState.value = _uiState.value.copy(
                        originalGroupName = groupName,
                        groupName = groupName,
                        originalMemberIds = members.map { it.userId }.toSet(),
                        members = members,
                        isLoadingGroup = false
                    )

                    // Load contacts after we have members
                    loadContacts()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingGroup = false,
                        error = "Failed to load group: ${error.message}"
                    )
                }
        }
    }

    /**
     * Load contacts, filtering out existing members
     */
    private fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingContacts = true)

            contactsRepository.fetchAllContactsWithInvites()
                .onSuccess { contactsList ->
                    val currentContacts = contactsList.filterIsInstance<CurrentContact>()

                    // Filter out users already in the group
                    val existingMemberIds = _uiState.value.members.map { it.userId }.toSet()
                    val availableContacts = currentContacts.filter { contact ->
                        contact.userId !in existingMemberIds && contact.userId != currentUserId
                    }

                    _uiState.value = _uiState.value.copy(
                        availableContacts = availableContacts,
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
     * Update group name
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
     * Show remove confirmation dialog
     */
    fun showRemoveConfirmation(member: GroupMember) {
        _uiState.value = _uiState.value.copy(
            showRemoveConfirmation = true,
            memberToRemove = member
        )
    }

    /**
     * Hide remove confirmation dialog
     */
    fun hideRemoveConfirmation() {
        _uiState.value = _uiState.value.copy(
            showRemoveConfirmation = false,
            memberToRemove = null
        )
    }

    /**
     * Remove a member from the group
     */
    fun removeMember() {
        val member = _uiState.value.memberToRemove ?: return

        viewModelScope.launch {
            groupRepository.removeGroupMember(member.userGroupId)
                .onSuccess {
                    // Update UI state
                    val updatedMembers = _uiState.value.members.filter {
                        it.userGroupId != member.userGroupId
                    }

                    // Add removed member back to available contacts
                    val removedContact = CurrentContact(
                        contactId = "", // Not needed for this use case
                        userId = member.userId,
                        userName = member.userName,
                        name = member.name,
                        avatarUri = null,
                        phoneNumber = null
                    )
                    val updatedContacts = _uiState.value.availableContacts + removedContact

                    _uiState.value = _uiState.value.copy(
                        members = updatedMembers,
                        availableContacts = updatedContacts,
                        showRemoveConfirmation = false,
                        memberToRemove = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to remove member: ${error.message}",
                        showRemoveConfirmation = false,
                        memberToRemove = null
                    )
                }
        }
    }

    /**
     * Show members bottom sheet
     */
    fun showMembersSheet() {
        _uiState.value = _uiState.value.copy(showMembersBottomSheet = true)
    }

    /**
     * Hide members bottom sheet
     */
    fun hideMembersSheet() {
        _uiState.value = _uiState.value.copy(showMembersBottomSheet = false)
    }

    /**
     * Add a member to the group
     */
    fun addMember(contact: CurrentContact) {
        viewModelScope.launch {
            val groupId = _uiState.value.groupId
            val userId = contact.userId

            groupRepository.addGroupMember(groupId, userId)
                .onSuccess { userGroupId ->
                    // Add to members list
                    val newMember = GroupMember(
                        userGroupId = userGroupId,
                        userId = userId,
                        userName = contact.userName,
                        name = contact.name
                    )
                    val updatedMembers = _uiState.value.members + newMember

                    // Remove from available contacts
                    val updatedContacts = _uiState.value.availableContacts.filter {
                        it.userId != userId
                    }

                    _uiState.value = _uiState.value.copy(
                        members = updatedMembers,
                        availableContacts = updatedContacts
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to add member: ${error.message}"
                    )
                }
        }
    }

    /**
     * Save all changes
     */
    fun saveChanges() {
        val state = _uiState.value

        // Validate
        if (state.groupName.length < 3) {
            _uiState.value = state.copy(error = "Group name must be at least 3 characters")
            return
        }

        if (state.members.isEmpty()) {
            _uiState.value = state.copy(error = "Group must have at least one member")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            // Only update name if it changed
            if (state.groupName != state.originalGroupName) {
                groupRepository.updateGroupName(state.groupId, state.groupName)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            saveSuccessful = true
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = "Failed to save changes: ${error.message}"
                        )
                    }
            } else {
                // No name change, just mark as successful
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccessful = true
                )
            }
        }
    }
}