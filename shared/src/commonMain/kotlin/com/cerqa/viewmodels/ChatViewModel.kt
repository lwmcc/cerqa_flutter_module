package com.cerqa.viewmodels

import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.ListUserChannelsQuery
import com.cerqa.graphql.ListUserGroupsQuery
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
    val groupsError: String? = null
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

            // Load both channels and groups in parallel
            val channelsResult = conversationRepository.getUserChannels(userId)
            val groupsResult = groupRepository.getUserGroups(userId)

            val combinedList = mutableListOf<ChatListItem>()
            var errorMessage: String? = null

            // Process channels
            channelsResult.onSuccess { channels ->
                channels.forEach { channel ->
                    combinedList.add(
                        ChatListItem.DirectChat(
                            id = channel.id,
                            timestamp = channel.createdAt,
                            channel = channel
                        )
                    )
                }
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to load channels"
            }

            // Process groups
            groupsResult.onSuccess { groups ->
                groups.forEach { group ->
                    combinedList.add(
                        ChatListItem.GroupChat(
                            id = group.id,
                            timestamp = group.createdAt,
                            group = group
                        )
                    )
                }
            }.onFailure { error ->
                if (errorMessage == null) {
                    errorMessage = error.message ?: "Failed to load groups"
                }
            }

            // Sort by timestamp (most recent first)
            val sortedList = combinedList.sortedByDescending { it.timestamp }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                combinedChats = sortedList,
                channels = channelsResult.getOrElse { emptyList() },
                groups = groupsResult.getOrElse { emptyList() },
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
                    _uiState.value = _uiState.value.copy(
                        isLoadingGroups = false,
                        groups = groups,
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
}
