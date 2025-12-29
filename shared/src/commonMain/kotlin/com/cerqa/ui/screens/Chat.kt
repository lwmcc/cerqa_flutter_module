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
    onNavigateToContacts: () -> Unit = {}
) {
    val tabs = listOf("Chats", "Groups")
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
            0 -> ChatsTab()
            1 -> GroupsTab()
        }
    }
}

@Composable
private fun ChatsTab() {
    // Dummy chat data
    val chats = remember {
        listOf(
            ChatItem(
                id = "1",
                name = "Larry McCarty",
                lastMessage = "Hey, how are you?",
                timestamp = "2:30 PM",
                unreadCount = 2
            ),
            ChatItem(
                id = "2",
                name = "LeBron James",
                lastMessage = "See you at the game!",
                timestamp = "1:15 PM",
                unreadCount = 0
            ),
            ChatItem(
                id = "3",
                name = "Sarah Johnson",
                lastMessage = "Thanks for your help!",
                timestamp = "Yesterday",
                unreadCount = 1
            ),
            ChatItem(
                id = "4",
                name = "Mike Chen",
                lastMessage = "The meeting is at 3 PM",
                timestamp = "Yesterday",
                unreadCount = 0
            ),
            ChatItem(
                id = "5",
                name = "Emma Wilson",
                lastMessage = "Can you send me the files?",
                timestamp = "Monday",
                unreadCount = 3
            )
        )
    }

    if (chats.isEmpty()) {
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
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(chats, key = { it.id }) { chat ->
                ChatListItem(chat = chat, onClick = { /* TODO: Open chat */ })
                HorizontalDivider()
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
