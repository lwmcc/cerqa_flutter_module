package com.cerqa.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cerqa.ui.components.MembersBottomSheet
import com.cerqa.viewmodels.EditGroupViewModel
import com.cerqa.viewmodels.GroupMember
import org.koin.compose.koinInject

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditGroup(
    groupId: String,
    editGroupViewModel: EditGroupViewModel = koinInject(),
    onDismiss: () -> Unit = {}
) {
    val uiState by editGroupViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Load group data on first composition
    LaunchedEffect(groupId) {
        editGroupViewModel.loadGroup(groupId)
    }

    // Navigate back when save is successful
    LaunchedEffect(uiState.saveSuccessful) {
        if (uiState.saveSuccessful) {
            onDismiss()
        }
    }

    // Show members bottom sheet
    if (uiState.showMembersBottomSheet) {
        MembersBottomSheet(
            onDismiss = { editGroupViewModel.hideMembersSheet() },
            contacts = uiState.availableContacts,
            selectedMemberIds = emptySet(),
            isLoading = uiState.isLoadingContacts,
            onToggleMember = { userId ->
                val contact = uiState.availableContacts.find { it.userId == userId }
                if (contact != null) {
                    editGroupViewModel.addMember(contact)
                    editGroupViewModel.hideMembersSheet()
                }
            }
        )
    }

    // Remove member confirmation dialog
    if (uiState.showRemoveConfirmation && uiState.memberToRemove != null) {
        AlertDialog(
            onDismissRequest = { editGroupViewModel.hideRemoveConfirmation() },
            title = { Text("Remove Member?") },
            text = {
                val memberName = uiState.memberToRemove?.name
                    ?: uiState.memberToRemove?.userName
                    ?: "this member"
                Text("Are you sure you want to remove $memberName from the group?")
            },
            confirmButton = {
                Button(
                    onClick = { editGroupViewModel.removeMember() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { editGroupViewModel.hideRemoveConfirmation() }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = if (uiState.originalGroupName.isNotEmpty())
                            uiState.originalGroupName
                        else
                            "Edit Group"
                    )
                }
            )
        },
        bottomBar = {
            // Only show button when content is loaded (not loading and has data)
            if (!uiState.isLoadingGroup && uiState.groupName.isNotEmpty()) {
                Button(
                    onClick = { editGroupViewModel.saveChanges() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(56.dp),
                    enabled = !uiState.isSaving &&
                            uiState.isValid &&
                            uiState.hasUnsavedChanges
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    }
                    Text(
                        text = if (uiState.isSaving) "Saving..." else "Save Changes",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoadingGroup -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null && uiState.groupName.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = uiState.error ?: "Failed to load group",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = { editGroupViewModel.loadGroup(groupId) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                keyboardController?.hide()
                            })
                        }
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Group Name Field (pre-filled with original name)
                    OutlinedTextField(
                        value = uiState.groupName,
                        onValueChange = { editGroupViewModel.onGroupNameChanged(it.trim()) },
                        label = { Text("Group Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.groupNameError != null,
                        supportingText = {
                            if (uiState.groupNameError != null) {
                                Text(
                                    text = uiState.groupNameError.orEmpty(),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        trailingIcon = {
                            if (uiState.groupNameError != null) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    // Members Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Members (${uiState.members.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Add Members Button
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { editGroupViewModel.showMembersSheet() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add members",
                            modifier = Modifier.size(18.dp)
                        )
                        Text("Add Members")
                    }

                    // Member Chips
                    if (uiState.members.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.members.forEach { member ->
                                MemberChip(
                                    member = member,
                                    onRemove = { editGroupViewModel.showRemoveConfirmation(member) }
                                )
                            }
                        }
                    }

                    // Error message
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberChip(
    member: GroupMember,
    onRemove: () -> Unit
) {
    AssistChip(
        onClick = { },
        label = {
            Text(member.name ?: member.userName ?: "Unknown")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove ${member.name}",
                    modifier = Modifier.size(14.dp)
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    )
}
