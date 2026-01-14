package com.cerqa.viewmodels

import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.ListUserChannelsQuery
import com.cerqa.graphql.ListUserGroupsQuery
import com.cerqa.graphql.ListUserSentMessagesQuery
import com.cerqa.repository.ConversationRepository
import com.cerqa.repository.GroupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed class to represent both direct chats and group chats
sealed class ChatListItem {
    abstract val id: String
    abstract val timestamp: String

    data class DirectChat(
        override val id: String,
        override val timestamp: String,
        val channel: ListUserChannelsQuery.Item
    ) : ChatListItem()

    // Direct chat from message data (when channel isn't in the user's channels query)
    data class MessageBasedChat(
        override val id: String,
        override val timestamp: String,
        val channelId: String,
        val channelName: String?,
        val otherUserId: String,
        val otherUserName: String,
        val lastMessageContent: String,
        val lastMessageTime: String
    ) : ChatListItem()

    data class GroupChat(
        override val id: String,
        override val timestamp: String,
        val group: ListUserGroupsQuery.Item
    ) : ChatListItem()
}

data class ChatUiState(
    val isLoading: Boolean = false,
    val combinedChats: List<ChatListItem> = emptyList(),
    val error: String? = null,
    // Keep these for backward compatibility during transition
    val channels: List<ListUserChannelsQuery.Item> = emptyList(),
    val groups: List<ListUserGroupsQuery.Item> = emptyList(),
    val isLoadingGroups: Boolean = false,
    val groupsError: String? = null,
    // Cleanup state
    val isCleaningUp: Boolean = false,
    val cleanupMessage: String? = null
)

class ChatViewModel(
    private val authTokenProvider: AuthTokenProvider,
    private val conversationRepository: ConversationRepository,
    private val groupRepository: GroupRepository,
    private val mainDispatcher: CoroutineDispatcher
) {
    private val viewModelScope = CoroutineScope(mainDispatcher)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Load both channels and groups together
    fun loadAllChats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val userId = authTokenProvider.getCurrentUserId()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No authenticated user found"
                )
                return@launch
            }

            println("ChatViewModel: Loading chats for userId: $userId")

            // Load channels, groups, and sent messages
            val channelsResult = conversationRepository.getUserChannels(userId)
            val groupsResult = groupRepository.getUserGroups(userId)
            val sentMessagesResult = conversationRepository.getUserSentMessages(userId)

            val combinedList = mutableListOf<ChatListItem>()
            var errorMessage: String? = null
            val existingChannelIds = mutableSetOf<String>()

            // Process channels - only include direct chats (not groups)
            channelsResult.onSuccess { channels ->
                println("ChatViewModel: Fetched ${channels.size} channels")
                channels.forEach { channel ->
                    println("ChatViewModel: Channel id=${channel.id}, name=${channel.name}, isGroup=${channel.isGroup}, creatorId=${channel.creatorId}, receiverId=${channel.receiverId}")

                    // Skip group channels - they're handled via UserGroups
                    if (channel.isGroup == true) {
                        println("ChatViewModel: Skipping group channel: ${channel.id}")
                        return@forEach
                    }

                    existingChannelIds.add(channel.id)
                    combinedList.add(
                        ChatListItem.DirectChat(
                            id = channel.id,
                            timestamp = channel.messages?.items?.firstOrNull()?.createdAt ?: channel.createdAt,
                            channel = channel
                        )
                    )
                }
                println("ChatViewModel: Added ${combinedList.size} direct chats from channels")
            }.onFailure { error ->
                println("ChatViewModel: Failed to load channels: ${error.message}")
                errorMessage = error.message ?: "Failed to load channels"
            }

            // Process sent messages to find additional conversations not in channels
            sentMessagesResult.onSuccess { messages ->
                println("ChatViewModel: Fetched ${messages.size} sent messages")

                // Group messages by channelId to get unique conversations
                val messagesByChannel = messages.groupBy { it.channelId }
                println("ChatViewModel: Found ${messagesByChannel.size} unique channels from messages")

                messagesByChannel.forEach { (channelId, channelMessages) ->
                    // Skip if this channel is already in our list
                    if (channelId in existingChannelIds) {
                        println("ChatViewModel: Channel $channelId already exists, skipping")
                        return@forEach
                    }

                    // Get the most recent message for this channel
                    val mostRecentMessage = channelMessages.maxByOrNull { it.createdAt }
                        ?: return@forEach

                    val channel = mostRecentMessage.channel

                    // Skip group channels (we handle those separately)
                    if (channel?.isGroup == true) {
                        println("ChatViewModel: Skipping group channel from messages: $channelId")
                        return@forEach
                    }

                    // Determine the other user in the conversation
                    // Check if current user is the creator (compare both userId and creatorId)
                    val isCurrentUserCreator = channel?.creator?.userId == userId ||
                                               channel?.creatorId == userId ||
                                               channel?.creator?.id == userId

                    val (otherUserId, otherUserName) = if (channel != null) {
                        when {
                            isCurrentUserCreator -> {
                                val receiver = channel.receiver
                                val name = receiver?.userName?.takeIf { it.isNotEmpty() }
                                    ?: receiver?.name?.takeIf { it.isNotEmpty() }
                                    ?: "${receiver?.firstName ?: ""} ${receiver?.lastName ?: ""}".trim()
                                        .takeIf { it.isNotEmpty() }
                                    ?: "Unknown"
                                Pair(receiver?.userId ?: channel.receiverId ?: "", name)
                            }
                            else -> {
                                val creator = channel.creator
                                val name = creator?.userName?.takeIf { it.isNotEmpty() }
                                    ?: creator?.name?.takeIf { it.isNotEmpty() }
                                    ?: "${creator?.firstName ?: ""} ${creator?.lastName ?: ""}".trim()
                                        .takeIf { it.isNotEmpty() }
                                    ?: "Unknown"
                                Pair(creator?.userId ?: channel.creatorId ?: "", name)
                            }
                        }
                    } else {
                        // Channel is null - try to extract user ID from channelId format "chat:userId1:userId2"
                        val parts = channelId.split(":")
                        if (parts.size == 3 && parts[0] == "chat") {
                            val otherUser = if (parts[1] == userId) parts[2] else parts[1]
                            Pair(otherUser, "Chat")
                        } else {
                            println("ChatViewModel: Could not parse channelId: $channelId")
                            return@forEach
                        }
                    }

                    existingChannelIds.add(channelId)
                    combinedList.add(
                        ChatListItem.MessageBasedChat(
                            id = channelId,
                            timestamp = mostRecentMessage.createdAt,
                            channelId = channelId,
                            channelName = channel?.name,
                            otherUserId = otherUserId,
                            otherUserName = otherUserName,
                            lastMessageContent = mostRecentMessage.content,
                            lastMessageTime = mostRecentMessage.createdAt
                        )
                    )
                    println("ChatViewModel: Added message-based chat: $channelId -> $otherUserName")
                }
            }.onFailure { error ->
                // Don't fail the whole load if sent messages fail
                println("ChatViewModel: Failed to load sent messages: ${error.message}")
            }

            // Process groups - filter out those without a valid group id or name
            groupsResult.onSuccess { groups ->
                println("ChatViewModel: Fetched ${groups.size} groups")
                groups
                    .filter {
                        it.group?.id?.isNotEmpty() == true &&
                        it.group?.name?.isNotEmpty() == true
                    }
                    .forEach { group ->
                        combinedList.add(
                            ChatListItem.GroupChat(
                                id = group.id,
                                timestamp = group.createdAt,
                                group = group
                            )
                        )
                    }
            }.onFailure { error ->
                println("ChatViewModel: Failed to load groups: ${error.message}")
                if (errorMessage == null) {
                    errorMessage = error.message ?: "Failed to load groups"
                }
            }

            // Collect userIds that need name lookup (those with "Chat" or "Unknown" as names)
            val userIdsToLookup = combinedList
                .filterIsInstance<ChatListItem.MessageBasedChat>()
                .filter { it.otherUserName == "Chat" || it.otherUserName == "Unknown" }
                .map { it.otherUserId }
                .filter { it.isNotEmpty() }
                .distinct()

            // Also check DirectChat items that might have "Unknown" names
            val directChatUserIdsToLookup = combinedList
                .filterIsInstance<ChatListItem.DirectChat>()
                .mapNotNull { item ->
                    val channel = item.channel
                    val isCreator = channel.creatorId == userId || channel.creator?.userId == userId
                    if (isCreator) {
                        // Get receiver info
                        val receiverName = channel.receiver?.userName
                            ?: channel.receiver?.name
                            ?: "${channel.receiver?.firstName ?: ""} ${channel.receiver?.lastName ?: ""}".trim()
                        if (receiverName.isEmpty() || receiverName == "Unknown") {
                            channel.receiverId ?: channel.receiver?.userId
                        } else null
                    } else {
                        // Get creator info
                        val creatorName = channel.creator?.userName
                            ?: channel.creator?.name
                            ?: "${channel.creator?.firstName ?: ""} ${channel.creator?.lastName ?: ""}".trim()
                        if (creatorName.isEmpty() || creatorName == "Unknown") {
                            channel.creatorId ?: channel.creator?.userId
                        } else null
                    }
                }
                .filter { it.isNotEmpty() }

            val allUserIdsToLookup = (userIdsToLookup + directChatUserIdsToLookup).distinct()

            // Fetch user info for missing usernames
            val userLookupMap = if (allUserIdsToLookup.isNotEmpty()) {
                println("ChatViewModel: Looking up ${allUserIdsToLookup.size} users for missing names")
                conversationRepository.getUsersByUserIds(allUserIdsToLookup).getOrElse { emptyMap() }
            } else {
                emptyMap()
            }

            // Update chat items with fetched usernames
            val updatedList = combinedList.map { item ->
                when (item) {
                    is ChatListItem.MessageBasedChat -> {
                        if ((item.otherUserName == "Chat" || item.otherUserName == "Unknown") && item.otherUserId.isNotEmpty()) {
                            val user = userLookupMap[item.otherUserId]
                            if (user != null) {
                                val displayName = user.userName?.takeIf { it.isNotEmpty() }
                                    ?: user.name?.takeIf { it.isNotEmpty() }
                                    ?: "${user.firstName} ${user.lastName}".trim().takeIf { it.isNotEmpty() }
                                    ?: "Unknown"
                                item.copy(otherUserName = displayName)
                            } else item
                        } else item
                    }
                    else -> item
                }
            }

            // Sort by timestamp (most recent first)
            val sortedList = updatedList.sortedByDescending { it.timestamp }

            println("ChatViewModel: Total combined chats: ${sortedList.size}")

            // Filter groups to only include those with valid group ids and names
            val validGroups = groupsResult.getOrElse { emptyList() }
                .filter {
                    it.group?.id?.isNotEmpty() == true &&
                    it.group?.name?.isNotEmpty() == true
                }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                combinedChats = sortedList,
                channels = channelsResult.getOrElse { emptyList() },
                groups = validGroups,
                error = errorMessage
            )
        }
    }

    // Keep old methods for backward compatibility
    fun loadUserChannels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val userId = authTokenProvider.getCurrentUserId()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No authenticated user found"
                )
                return@launch
            }

            conversationRepository.getUserChannels(userId)
                .onSuccess { channels ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        channels = channels,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load channels"
                    )
                }
        }
    }

    fun loadUserGroups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingGroups = true, groupsError = null)

            val userId = authTokenProvider.getCurrentUserId()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoadingGroups = false,
                    groupsError = "No authenticated user found"
                )
                return@launch
            }

            groupRepository.getUserGroups(userId)
                .onSuccess { groups ->
                    // Filter out groups without a valid group id or name
                    val validGroups = groups.filter {
                        it.group?.id?.isNotEmpty() == true &&
                        it.group?.name?.isNotEmpty() == true
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoadingGroups = false,
                        groups = validGroups,
                        groupsError = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingGroups = false,
                        groupsError = error.message ?: "Failed to load groups"
                    )
                }
        }
    }

    fun deleteGroup(groupId: String, channelId: String, onComplete: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = groupRepository.deleteGroup(groupId, channelId)
            onComplete(result)

            // Reload groups after deletion
            if (result.isSuccess) {
                loadAllChats()
            }
        }
    }

    fun leaveGroup(userGroupId: String, userChannelId: String, onComplete: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
/*            val result = groupRepository.leaveGroup(userGroupId, *//*userChannelId*//*)
            onComplete(result)

            // Reload groups after leaving
            if (result.isSuccess) {
                loadAllChats()
            }*/
        }
    }

    fun cleanupUnknownData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCleaningUp = true,
                cleanupMessage = null
            )

            groupRepository.cleanupUnknownData()
                .onSuccess { message ->
                    _uiState.value = _uiState.value.copy(
                        isCleaningUp = false,
                        cleanupMessage = message
                    )
                    // Reload data after cleanup
                    loadAllChats()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isCleaningUp = false,
                        cleanupMessage = "Cleanup failed: ${error.message}"
                    )
                }
        }
    }

    fun clearCleanupMessage() {
        _uiState.value = _uiState.value.copy(cleanupMessage = null)
    }
}
