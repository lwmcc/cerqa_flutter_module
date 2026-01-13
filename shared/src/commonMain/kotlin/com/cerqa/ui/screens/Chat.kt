package com.cerqa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cerqa.graphql.ListUserChannelsQuery
import com.cerqa.graphql.ListUserGroupsQuery
import com.cerqa.ui.components.ChatBottomSheet
import com.cerqa.ui.components.ChatFilterRow
import com.cerqa.viewmodels.ChatViewModel
import com.cerqa.viewmodels.ChatListItem
import org.koin.compose.koinInject

// TODO: move out
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

// Sealed class to track which chat was long-clicked
sealed class LongClickedChat {
    data class DirectChat(val channelId: String, val userName: String) : LongClickedChat()
    data class GroupChat(val groupId: String, val groupName: String, val role: com.cerqa.graphql.type.GroupMemberRole?) : LongClickedChat()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(
    onNavigateToContacts: () -> Unit = {},
    onNavigateToEditGroup: (String) -> Unit,
    onNavigateToConversation: (contactId: String, userName: String) -> Unit = { _, _ -> },
    chatViewModel: ChatViewModel = koinInject(),
) {
    var longClickedChat by remember { mutableStateOf<LongClickedChat?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var groupToDelete by remember { mutableStateOf<Pair<String, String>?>(null) } // groupId, channelId
    var groupToLeave by remember { mutableStateOf<Pair<String, String>?>(null) } // userGroupId, userChannelId
    var actionInProgress by remember { mutableStateOf(false) }

    val uiState by chatViewModel.uiState.collectAsState()
    var currentUserId by remember { mutableStateOf("") }
    val authTokenProvider: com.cerqa.auth.AuthTokenProvider = koinInject()

    LaunchedEffect(Unit) {
        currentUserId = authTokenProvider.getCurrentUserId() ?: ""
        chatViewModel.loadAllChats()
    }

    // Show appropriate bottom sheet based on chat type
    longClickedChat?.let { chat ->
        ChatBottomSheet(
            showBottomSheet = { show ->
                if (!show) longClickedChat = null
            }
        ) {
            when (chat) {
                is LongClickedChat.DirectChat -> {
                    DirectChatBottomSheet(
                        userName = chat.userName,
                        onDeleteChat = {
                            // TODO: Delete direct chat
                            longClickedChat = null
                        }
                    )
                }
                is LongClickedChat.GroupChat -> {
                    // Find the full group data to get IDs we need
                    val groupData = uiState.groups.find { it.group?.id == chat.groupId }

                    GroupChatBottomSheet(
                        groupName = chat.groupName,
                        role = chat.role,
                        editGroup = {
                            groupData?.group?.id?.let { groupId ->
                                onNavigateToEditGroup(groupId)
                            }
                            longClickedChat = null
                        },
                        onDeleteGroup = {
                            // Show confirmation dialog
                            groupData?.let { group ->
                                // Find the channel for this group
                                val channel = uiState.channels.find {
                                    it.receiverId == group.group?.groupId
                                }
                                groupToDelete = Pair(group.group?.id ?: "", channel?.id ?: "")
                                showDeleteConfirmation = true
                            }
                            longClickedChat = null
                        },
                        onLeaveGroup = {
                            // Show confirmation dialog
                            groupData?.let { group ->
                                // Find the user channel for this group's channel
                                val channel = uiState.channels.find {
                                    it.receiverId == group.group?.groupId
                                }
                                groupToLeave = Pair(group.id, "" /* TODO: find userChannelId */)
                                showLeaveConfirmation = true
                            }
                            longClickedChat = null
                        }
                    )
                }
            }
        }
    }

    // Delete Group Confirmation Dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Group?") },
            text = { Text("Are you sure you want to delete this group? This action cannot be undone and will remove the group for all members.") },
            confirmButton = {
                Button(
                    onClick = {
                        groupToDelete?.let { (groupId, channelId) ->
                            actionInProgress = true
                            chatViewModel.deleteGroup(groupId, channelId) { result ->
                                actionInProgress = false
                                showDeleteConfirmation = false
                                groupToDelete = null
                                result.onFailure { error ->
                                    // TODO: Show error message to user
                                    println("Error deleting group: ${error.message}")
                                }
                            }
                        }
                    },
                    enabled = !actionInProgress,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    if (actionInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onError,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false },
                    enabled = !actionInProgress
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Leave Group Confirmation Dialog
    if (showLeaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmation = false },
            title = { Text("Leave Group?") },
            text = { Text("Are you sure you want to leave this group? You will no longer have access to group messages.") },
            confirmButton = {
                Button(
                    onClick = {
                        groupToLeave?.let { (userGroupId, userChannelId) ->
                            actionInProgress = true
                            chatViewModel.leaveGroup(userGroupId, userChannelId) { result ->
                                actionInProgress = false
                                showLeaveConfirmation = false
                                groupToLeave = null
                                result.onFailure { error ->
                                    // TODO: Show error message to user
                                    println("Error leaving group: ${error.message}")
                                }
                            }
                        }
                    },
                    enabled = !actionInProgress,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    if (actionInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onError,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Leave")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLeaveConfirmation = false },
                    enabled = !actionInProgress
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                        Button(onClick = { chatViewModel.loadAllChats() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.combinedChats.isEmpty() -> {
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

                    val chats = uiState.combinedChats

                    if (chats.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                ChatFilterRow()
                            }
                        }
                    }

                    items(chats, key = { it.id }) { chatItem ->
                        when (chatItem) {
                            is ChatListItem.DirectChat -> {
                                DirectChatListItem(
                                    chatItem = chatItem,
                                    currentUserId = currentUserId,
                                    onNavigateToConversation = onNavigateToConversation,
                                    onLongClick = { channelId, userName ->
                                        longClickedChat =
                                            LongClickedChat.DirectChat(channelId, userName)
                                    }
                                )
                            }
                            is ChatListItem.GroupChat -> {
                                GroupChatListItem(
                                    chatItem = chatItem,
                                    onClick = {
                                        // TODO: Navigate to group conversation
                                    },
                                    onLongClick = { groupId, groupName, role ->
                                        longClickedChat =
                                            LongClickedChat.GroupChat(groupId, groupName, role)
                                    }
                                )
                            }
                        }
                    }
                }
            }
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


// TODO: move
// Bottom sheet for Group Chats
@Composable
fun GroupChatBottomSheet(
    groupName: String,
    role: com.cerqa.graphql.type.GroupMemberRole?,
    editGroup: () -> Unit,
    onDeleteGroup: () -> Unit,
    onLeaveGroup: () -> Unit
) {
    // If role is null, we don't show creator-specific actions for safety
    // TODO: testing
    val isCreator = true // role == com.cerqa.graphql.type.GroupMemberRole.CREATOR

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Archive,
                contentDescription = "Archive",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Archive",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
            )
        }

        // Show "Delete Group" only for creators
        if (isCreator) {
            Row(
                modifier = Modifier
                    .clickable { editGroup() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Group",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Edit Group",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal,
                )
            }

            Row(
                modifier = Modifier
                    .clickable { onDeleteGroup() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Group",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Delete Group",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .clickable { onLeaveGroup() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Leave Group",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Leave Group",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

// TODO: move
// Bottom sheet for Direct Chats
@Composable
fun DirectChatBottomSheet(
    userName: String,
    onDeleteChat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Archive,
                contentDescription = "Archive",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Archive",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
            )
        }

        Row(
            modifier = Modifier
                .clickable { onDeleteChat() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Delete",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun DirectChatListItem(
    chatItem: ChatListItem.DirectChat,
    currentUserId: String,
    onNavigateToConversation: (contactId: String, userName: String) -> Unit,
    onLongClick: (channelId: String, userName: String) -> Unit
) {
    val channel = chatItem.channel

    fun getDisplayName(user: com.cerqa.graphql.ListUserChannelsQuery.Creator?): String {
        return user?.userName?.takeIf { it.isNotEmpty() }
            ?: user?.name?.takeIf { it.isNotEmpty() }
            ?: "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim()
                .takeIf { it.isNotEmpty() }
            ?: "Unknown"
    }

    fun getDisplayNameFromReceiver(user: com.cerqa.graphql.ListUserChannelsQuery.Receiver?): String {
        return user?.userName?.takeIf { it.isNotEmpty() }
            ?: user?.name?.takeIf { it.isNotEmpty() }
            ?: "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim()
                .takeIf { it.isNotEmpty() }
            ?: "Unknown"
    }

    val (otherUserId, displayName) = when {
        channel.creator?.userId == currentUserId -> {
            Pair(channel.receiver?.userId ?: "", getDisplayNameFromReceiver(channel.receiver))
        }

        else -> {
            Pair(channel.creator?.userId ?: "", getDisplayName(channel.creator))
        }
    }

    val lastMessage = channel.messages?.items?.filterNotNull()?.firstOrNull()
    val lastMessageText = lastMessage?.content ?: "No messages yet"
    val timestamp = lastMessage?.createdAt ?: channel.createdAt

    ListItem(
        headlineContent = {
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            Text(
                text = lastMessageText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User avatar",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        trailingContent = {
            Text(
                text = formatTimestamp(timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.combinedClickable(
            onClick = { onNavigateToConversation(otherUserId, displayName) },
            onLongClick = { onLongClick(channel.id, displayName) }
        )
    )
}

@Composable
private fun GroupChatListItem(
    chatItem: ChatListItem.GroupChat,
    onClick: () -> Unit,
    onLongClick: (groupId: String, groupName: String, role: com.cerqa.graphql.type.GroupMemberRole?) -> Unit
) {
    val userGroup = chatItem.group
    val groupName = userGroup.group?.name ?: "Unknown Group"
    val groupId = userGroup.group?.id ?: ""
    val createdAt = userGroup.group?.createdAt ?: ""
    val role: com.cerqa.graphql.type.GroupMemberRole? =
        null  // Role not yet in production backend

    ListItem(
        headlineContent = {
            Text(
                text = groupName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            Text(
                text = "Group chat",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Group avatar",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        trailingContent = {
            Text(
                text = formatTimestamp(createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.combinedClickable(
            onClick = { onClick() },
            onLongClick = { onLongClick(groupId, groupName, role) }
        )
    )
}

sealed class ChatItemModel {
    abstract val id: String

    data class DirectChat(
        override val id: String,
        val channel: ListUserChannelsQuery.Item
    ) : ChatItemModel()

    data class GroupChat(
        override val id: String,
        val group: ListUserGroupsQuery.Item
    ) : ChatItemModel()
}
