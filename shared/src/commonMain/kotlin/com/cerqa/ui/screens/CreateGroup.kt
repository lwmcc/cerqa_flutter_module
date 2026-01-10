package com.cerqa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cerqa.ui.components.MembersBottomSheet
import com.cerqa.ui.utils.KeyboardManager
import com.cerqa.utils.NameValidator
import com.cerqa.viewmodels.CreateGroupViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateGroup(
    createGroupViewModel: CreateGroupViewModel = koinInject(),
    onGroupCreated: () -> Unit = {}
) {
    val uiState by createGroupViewModel.uiState.collectAsState()
    val keyboardManager = remember { KeyboardManager() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Platform-specific keyboard handling
    keyboardManager.setupKeyboardHandling(
        onShow = { /* Keyboard shown */ },
        onHide = { /* Keyboard hidden */ }
    )

    // Navigate when group is created successfully
    LaunchedEffect(uiState.groupCreatedSuccessfully) {
        if (uiState.groupCreatedSuccessfully) {
            onGroupCreated()
        }
    }

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

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding() // Prevents UI from being squished by keyboard on iOS
            .verticalScroll(scrollState) // Allow scrolling when keyboard is visible
            .pointerInput(Unit) {
                // Dismiss keyboard when tapping outside text fields
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                })
            }
            .padding(24.dp)
    ) {

        OutlinedTextField(
            value = uiState.groupName,
            onValueChange = { groupName ->
                if (NameValidator.isValidFinal(groupName)) {
                    createGroupViewModel.onGroupNameChanged(groupName.trim())
                }
            },
            label = { Text("Group Name") },
            placeholder = { Text("Enter group name (min 3 characters)") },
            modifier = keyboardManager.addFocusModifier(
                Modifier.fillMaxWidth()
            ),
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

        if (uiState.selectedMembers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
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

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.error != null) {
            Text(
                text = uiState.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // To push button to bottom
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { createGroupViewModel.createGroup() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isCreatingGroup &&
                    uiState.groupName.length >= 3 &&
                    uiState.groupNameError == null &&
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
