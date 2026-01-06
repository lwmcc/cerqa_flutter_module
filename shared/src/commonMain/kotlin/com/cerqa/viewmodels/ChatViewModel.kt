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

data class ChatUiState(
    val isLoading: Boolean = false,
    val channels: List<ListUserChannelsQuery.Item> = emptyList(),
    val groups: List<ListUserGroupsQuery.Item> = emptyList(),
    val isLoadingGroups: Boolean = false,
    val error: String? = null,
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
}
