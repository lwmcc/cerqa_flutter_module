package com.cerqa.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    selectedTabIndex: Int = 0,
    onTabChange: (Int) -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToConversation: (contactId: String, userName: String) -> Unit = { _, _ -> },
    chatViewModel: ChatViewModel = koinInject()
) {
    val tabs = listOf("Chats", "Groups")

    // Load channels when composable is first displayed
    LaunchedEffect(Unit) {
        chatViewModel.loadUserChannels()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TabRow(selectedTabIndex = selectedTabIndex) {
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
                onNavigateToConversation = onNavigateToConversation
            )
            1 -> GroupsTab()
        }
    }
}

@Composable
private fun ChatsTab(
    chatViewModel: ChatViewModel,
    onNavigateToConversation: (contactId: String, userName: String) -> Unit,
    preferences: com.cerqa.data.Preferences = koinInject()
) {
    val uiState by chatViewModel.uiState.collectAsState()
    val currentUserId = preferences.getUserData()?.userId ?: ""

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
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.channels, key = { it.id }) { channel ->
                    ChatListItemFromChannel(
                        channel = channel,
                        currentUserId = currentUserId,
                        onNavigateToConversation = onNavigateToConversation
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun GroupsTab() {
    // Dummy group data
    val groups = remember {
        listOf(
            GroupItem(
                id = "1",
                name = "Car Club Members",
                lastMessage = "John: When is the next meetup?",
                timestamp = "3:45 PM",
                memberCount = 12,
                unreadCount = 5
            ),
            GroupItem(
                id = "2",
                name = "Weekend Warriors",
                lastMessage = "Mike: I'm in for Saturday!",
                timestamp = "11:20 AM",
                memberCount = 8,
                unreadCount = 0
            ),
            GroupItem(
                id = "3",
                name = "Classic Cars Enthusiasts",
                lastMessage = "Sarah: Check out my new ride!",
                timestamp = "Yesterday",
                memberCount = 24,
                unreadCount = 2
            ),
            GroupItem(
                id = "4",
                name = "Track Day Crew",
                lastMessage = "Alex: Next event is Aug 15th",
                timestamp = "Tuesday",
                memberCount = 6,
                unreadCount = 0
            )
        )
    }

    if (groups.isEmpty()) {
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
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(groups, key = { it.id }) { group ->
                GroupListItem(group = group, onClick = { /* TODO: Open group */ })
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ChatListItemFromChannel(
    channel: com.cerqa.graphql.ListUserChannelsQuery.Item,
    currentUserId: String,
    onNavigateToConversation: (contactId: String, userName: String) -> Unit
) {
    // Determine which user is the OTHER person (not the current user)
    val (otherUserId, displayName) = when {
        channel.creator?.userId == currentUserId -> {
            // Current user is the creator, so show receiver
            val userId = channel.receiver?.userId ?: ""
            val name = channel.receiver?.name ?: channel.receiver?.userName ?: "Unknown"
            userId to name
        }
        channel.receiver?.userId == currentUserId -> {
            // Current user is the receiver, so show creator
            val userId = channel.creator?.userId ?: ""
            val name = channel.creator?.name ?: channel.creator?.userName ?: "Unknown"
            userId to name
        }
        // Fallback: show creator if available, otherwise receiver
        channel.creator != null -> {
            val userId = channel.creator.userId ?: ""
            val name = channel.creator.name ?: channel.creator.userName ?: "Unknown"
            userId to name
        }
        else -> {
            val userId = channel.receiver?.userId ?: ""
            val name = channel.receiver?.name ?: channel.receiver?.userName ?: "Unknown"
            userId to name
        }
    }
    val lastMessage = channel.messages?.items?.filterNotNull()?.firstOrNull()
    val lastMessageText = lastMessage?.content ?: "No messages yet"

    // Format timestamp
    val timestamp = lastMessage?.createdAt ?: channel.createdAt

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onNavigateToConversation(otherUserId, displayName)
            }
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
