package com.cerqa.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cerqa.ui.components.MembersBottomSheet
import com.cerqa.viewmodels.CreateGroupViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateGroup(createGroupViewModel: CreateGroupViewModel = koinInject()) {
    val uiState by createGroupViewModel.uiState.collectAsState()

    // Show members bottom sheet when requested
    if (uiState.showMembersBottomSheet) {
        MembersBottomSheet(
            onDismiss = { createGroupViewModel.hideMembersSheet() },
            contacts = uiState.contacts,
            selectedMemberIds = uiState.selectedMembers,
            isLoading = uiState.isLoadingContacts,
            onToggleMember = { userId -> createGroupViewModel.toggleMemberSelection(userId) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Create Group",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Group Name Input with Validation
        OutlinedTextField(
            value = uiState.groupName,
            onValueChange = { createGroupViewModel.onGroupNameChanged(it) },
            label = { Text("Group Name") },
            placeholder = { Text("Enter group name (min 3 characters)") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.groupNameError != null,
            supportingText = {
                when {
                    uiState.groupNameError != null -> {
                        Text(
                            text = uiState.groupNameError ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    uiState.groupName.length >= 3 && !uiState.groupNameExists && !uiState.isCheckingName -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Available",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Name available",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            trailingIcon = {
                when {
                    uiState.isCheckingName -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    uiState.groupNameError != null -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    uiState.groupName.length >= 3 && !uiState.groupNameExists -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Valid",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Members Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Members (${uiState.selectedMembers.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedButton(
                onClick = { createGroupViewModel.showMembersSheet() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add members",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Members")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Members as Chips
        if (uiState.selectedMembers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No members added yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                createGroupViewModel.getSelectedContacts().forEach { contact ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(contact.name ?: contact.userName ?: "Unknown")
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
                                onClick = { createGroupViewModel.removeMember(contact.userId ?: "") },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove ${contact.name}",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Error Message
        if (uiState.error != null) {
            Text(
                text = uiState.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Create Button
        Button(
            onClick = { createGroupViewModel.createGroup() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isCreatingGroup &&
                      uiState.groupName.length >= 3 &&
                      !uiState.groupNameExists &&
                      uiState.selectedMembers.isNotEmpty()
        ) {
            if (uiState.isCreatingGroup) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (uiState.isCreatingGroup) "Creating..." else "Create Group",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
