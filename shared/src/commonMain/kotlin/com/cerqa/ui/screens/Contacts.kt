package com.cerqa.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.cerqa.models.ContactType
import com.cerqa.models.SearchUser
import com.cerqa.viewmodels.MessageType
import com.cerqa.viewmodels.SearchViewModel

@Composable
fun Contacts(searchViewModel: SearchViewModel) {
    val uiState by searchViewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showInviteDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<SearchUser?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newValue ->
                searchQuery = newValue
                searchViewModel.onQueryChange(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = {
                Text(text = "Search users by username")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search icon"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear search",
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            searchViewModel.onQueryChange("")
                        }
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // Search is triggered automatically via onQueryChange
                }
            )
        )

        if (uiState.pending) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        when (uiState.message) {
            MessageType.SUCCESS -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "User added to backend successfully!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            MessageType.INVITE_SENT -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Invite sent successfully!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            MessageType.ERROR -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "An error occurred",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            else -> {} // TODO:  what else?
        }

        // Idle state - show when no search query
        if (uiState.idle && searchQuery.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Search for users to connect",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // TEMPORARY: Test user creation buttons
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Test User Creation (TEMPORARY)",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Click to add current logged-in user to backend",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { searchViewModel.createTestUser1() },
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Text("Add as Larry")
                            }
                            Button(
                                onClick = { searchViewModel.createTestUser2() },
                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                            ) {
                                Text("Add as LeBron")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "TODO: Display backend contacts list here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "TODO: Display device contacts list here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search results - show when user is searching
        if (!uiState.idle && searchQuery.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search results header
                if (uiState.results.isNotEmpty()) {
                    item {
                        Text(
                            text = "Search Results",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // User search results from backend
                items(uiState.results, key = { it.id }) { user ->
                    SearchUserCard(
                        user = user,
                        onConnectClick = {
                            selectedUser = user
                            showInviteDialog = true
                        }
                    )
                }

                // Empty state for search results
                if (uiState.results.isEmpty() && !uiState.pending) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No users found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Invite confirmation dialog
    if (showInviteDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = { Text("Send Connection Invite") },
            text = {
                Text("Send a connection invite to ${selectedUser?.userName ?: "this user"}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedUser?.let { user ->
                            searchViewModel.inviteSentEvent(
                                com.cerqa.viewmodels.ContactCardConnectionEvent.InviteConnectEvent(
                                    receiverUserId = user.userId,
                                    rowId = user.id
                                )
                            )
                        }
                        showInviteDialog = false
                    }
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInviteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SearchUserCard(
    user: SearchUser,
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.userName ?: "Unknown User",
                    style = MaterialTheme.typography.titleMedium
                )
                user.phone?.let { phone ->
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Connection status or action button
            when (user.contactType) {
                ContactType.RECEIVED -> {
                    Text(
                        text = "Invite Received",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                ContactType.SENT -> {
                    Text(
                        text = "Invite Sent",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                ContactType.CURRENT -> {
                    Text(
                        text = "Connected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                null -> {
                    Button(
                        onClick = onConnectClick,
                        enabled = user.connectButtonEnabled
                    ) {
                        Text("Connect")
                    }
                }
            }
        }
    }
}