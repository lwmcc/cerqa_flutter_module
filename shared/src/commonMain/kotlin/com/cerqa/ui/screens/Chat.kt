package com.cerqa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cerqa.ui.components.ChatBottomSheet
import com.cerqa.viewmodels.ChatViewModel
import org.koin.compose.koinInject

// Dummy data models
data class ChatItem(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0
)

data class GroupItem(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val memberCount: Int,
    val unreadCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(
    // TODO: use actions to reduce params
    selectedTabIndex: Int = 0,
    onTabChange: (Int) -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToEditGroup: () -> Unit,
    onNavigateToConversation: (contactId: String, userName: String) -> Unit = { _, _ -> },
    chatViewModel: ChatViewModel = koinInject(),
) {
    val tabs = listOf("Chats", "Groups")
    // TODO: do something to reduce number of params, reduce sheets to one class
    var showBottomSheet by remember { mutableStateOf(false) }
    var showGroupChatBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        chatViewModel.loadUserChannels()
    }

    if (showBottomSheet) {
        ChatBottomSheet(
            showBottomSheet = { show ->
                showBottomSheet = show
            }
        ) {
            GroupChatBottomSheet() {
                onNavigateToEditGroup()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            divider = { } // Remove the divider line
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabChange(index) },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> ChatsTab(
                chatViewModel = chatViewModel,
                onNavigateToConversation = onNavigateToConversation,
                onLongClick = {
                    showBottomSheet = true
                },
            )

            1 -> GroupsTab(
                chatViewModel = chatViewModel,
                onLongClick = {
                    showBottomSheet = true
                },
            )
        }
    }
}

@Composable
private fun ChatsTab(
    chatViewModel: ChatViewModel,
    onNavigateToConversation: (contactId: String, userName: String) -> Unit,
    onLongClick: () -> Unit,
    authTokenProvider: com.cerqa.auth.AuthTokenProvider = koinInject(),
) {
    val uiState by chatViewModel.uiState.collectAsState()
    var currentUserId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        currentUserId = authTokenProvider.getCurrentUserId() ?: ""
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { chatViewModel.loadUserChannels() }) {
                        Text("Retry")
                    }
                }
            }
        }

        uiState.channels.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No chats yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(uiState.channels, key = { it.id }) { channel ->
                    ChatListItemFromChannel(
                        channel = channel,
                        currentUserId = currentUserId,
                        onNavigateToConversation = onNavigateToConversation,
                        onLongClick = onLongClick,
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun GroupsTab(
    chatViewModel: ChatViewModel,
    onLongClick: () -> Unit,
) {
    val uiState by chatViewModel.uiState.collectAsState()

    // Load groups when tab is first displayed
    LaunchedEffect(Unit) {
        chatViewModel.loadUserGroups()
    }

    when {
        uiState.isLoadingGroups -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.groupsError != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${uiState.groupsError}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { chatViewModel.loadUserGroups() }) {
                        Text("Retry")
                    }
                }
            }
        }

        uiState.groups.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No groups yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(uiState.groups, key = { it.id }) { userGroup ->
                    GroupListItemFromUserGroup(
                        userGroup = userGroup,
                        onClick = {
                            /* TODO: Open group chat */
                        },
                        onLongClick = onLongClick,
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ChatListItemFromChannel(
    channel: com.cerqa.graphql.ListUserChannelsQuery.Item,
    currentUserId: String,
    onNavigateToConversation: (contactId: String, userName: String) -> Unit,
    onLongClick: () -> Unit,
) {
    // Helper function to get display name with fallback logic
    fun getDisplayName(user: com.cerqa.graphql.ListUserChannelsQuery.Creator?): String {
        return user?.userName?.takeIf { it.isNotEmpty() }
            ?: user?.name?.takeIf { it.isNotEmpty() }
            ?: "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim().takeIf { it.isNotEmpty() }
            ?: "Unknown"
    }

    fun getDisplayNameFromReceiver(user: com.cerqa.graphql.ListUserChannelsQuery.Receiver?): String {
        return user?.userName?.takeIf { it.isNotEmpty() }
            ?: user?.name?.takeIf { it.isNotEmpty() }
            ?: "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim().takeIf { it.isNotEmpty() }
            ?: "Unknown"
    }

    // Determine which user is the OTHER person (not the current user)
    val (otherUserId, displayName) = when {
        channel.creator?.userId == currentUserId -> {
            // Current user is the creator, so show receiver
            (channel.receiver?.userId ?: "") to getDisplayNameFromReceiver(channel.receiver)
        }

        channel.receiver?.userId == currentUserId -> {
            // Current user is the receiver, so show creator
            (channel.creator?.userId ?: "") to getDisplayName(channel.creator)
        }
        // Fallback: show creator if available, otherwise receiver
        channel.creator != null -> {
            (channel.creator.userId ?: "") to getDisplayName(channel.creator)
        }

        else -> {
            (channel.receiver?.userId ?: "") to getDisplayNameFromReceiver(channel.receiver)
        }
    }
    val lastMessage = channel.messages?.items?.filterNotNull()?.firstOrNull()
    val lastMessageText = lastMessage?.content ?: "No messages yet"

    // Format timestamp
    val timestamp = lastMessage?.createdAt ?: channel.createdAt

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onNavigateToConversation(otherUserId, displayName)
                },
                onLongClick = {
                    onLongClick()
                }
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = lastMessageText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatTimestamp(timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChatListItem(chat: ChatItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chat.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = chat.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (chat.unreadCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Badge {
                    Text(chat.unreadCount.toString())
                }
            }
        }
    }
}

// Helper function to format timestamp
private fun formatTimestamp(timestamp: String): String {
    // Simple formatting - you can enhance this later
    return try {
        // Extract just the date part for now
        timestamp.substringBefore("T")
    } catch (e: Exception) {
        "Recently"
    }
}

@Composable
private fun GroupListItemFromUserGroup(
    userGroup: com.cerqa.graphql.ListUserGroupsQuery.Item,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val groupName = userGroup.group?.name ?: "Unknown Group"
    val createdAt = userGroup.group?.createdAt ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = {
                    onLongClick()
                }
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = groupName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Created ${formatTimestamp(createdAt)}",// TODO: dont format like this
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatTimestamp(createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GroupListItem(group: GroupItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${group.memberCount})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = group.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = group.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (group.unreadCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Badge {
                    Text(group.unreadCount.toString())
                }
            }
        }
    }
}



@Composable
fun GroupChatBottomSheet(editGroup: () -> Unit) {
    OutlinedButton(
        onClick = {
            editGroup()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Edit Group")
    }
    OutlinedButton(
        onClick = {
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Delete Chat")
    }
    OutlinedButton(
        onClick = {

        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Leave Chat")
    }
}

// TODO: move these
@Composable
fun ChatBottomSheet() {
    OutlinedButton(
        onClick = {
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Delete Chat")
    }
    OutlinedButton(
        onClick = {

        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Leave Chat")
    }
}
