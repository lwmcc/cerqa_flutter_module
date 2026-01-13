package com.cerqa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cerqa.ui.components.MembersBottomSheet
import com.cerqa.viewmodels.EditGroupViewModel
import com.cerqa.viewmodels.GroupMember
import org.koin.compose.koinInject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditGroup(
    groupId: String,
    editGroupViewModel: EditGroupViewModel = koinInject(),
    onDismiss: () -> Unit = {}
) {
    val uiState by editGroupViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            selectedMemberIds = emptySet(), // Not using multi-select here
            isLoading = uiState.isLoadingContacts,
            onToggleMember = { userId ->
                // Find the contact and add them immediately
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
                Text("Are you sure you want to remove ${uiState.memberToRemove?.name ?: uiState.memberToRemove?.userName ?: "this member"} from the group?")
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoadingGroup -> {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null && uiState.groupName.isEmpty() -> {
                // Error state (group failed to load)
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp)
                        .padding(bottom = 96.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Update group details and manage members",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Group Name Section
                    Text(
                        text = "Group Name",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.groupName,
                        onValueChange = { editGroupViewModel.onGroupNameChanged(it.trim()) },
                        label = { Text("Enter group name") },
                        placeholder = { Text("e.g., Weekend Warriors") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.groupNameError != null,
                        supportingText = {
                            if (uiState.groupNameError != null) {
                                Text(
                                    text = uiState.groupNameError ?: "",
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // Members Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Members",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${uiState.members.size} ${if (uiState.members.size == 1) "member" else "members"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add Member Button
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { editGroupViewModel.showMembersSheet() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add member",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Member")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Member Chips
                    if (uiState.members.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "No members in this group",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Sticky bottom button
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .imePadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = { editGroupViewModel.saveChanges() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.isSaving &&
                                uiState.groupName.length >= 3 &&
                                uiState.groupNameError == null &&
                                uiState.members.isNotEmpty()
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saving...")
                        } else {
                            Text("Save Changes")
                        }
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