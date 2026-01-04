package com.cerqa.viewmodels

import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.ListChannelMessagesQuery
import com.cerqa.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConversationUiState(
    val isLoading: Boolean = false,
    val messages: List<ListChannelMessagesQuery.Item> = emptyList(),
    val error: String? = null
)

class ConversationViewModel(
    private val authTokenProvider: AuthTokenProvider,
    private val mainDispatcher: CoroutineDispatcher,
    private val conversationRepository: ConversationRepository,
) {
    private val viewModelScope = CoroutineScope(mainDispatcher)

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    fun loadMessages(receiverId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val senderId = authTokenProvider.getCurrentUserId()
            if (senderId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No authenticated user found"
                )
                return@launch
            }

            val channelId = createConversationChannelName(receiverId, senderId)
            conversationRepository.getChannelMessages(channelId)
                .onSuccess { messages ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        messages = messages,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load messages"
                    )
                }
        }
    }

    fun sendChatMessage(channelId: String, senderUserId: String, receiverId: String, message: String) {
        viewModelScope.launch {
            val senderId = authTokenProvider.getCurrentUserId()
            if (senderId != null) {
                conversationRepository.sendChatMessage(
                    channelId = createConversationChannelName(
                        receiverId,
                        senderId = senderId
                    ),
                    senderUserId,
                    content = message,
                )
                // Reload messages after sending
                loadMessages(receiverId)
            } else {
                println("ConversationViewModel ***** Cannot send message - user not authenticated")
            }
        }
    }

    fun createConversationChannelName(receiverId: String, senderId: String): String {
        return if (receiverId > senderId) {
            "chat:$senderId:$receiverId"
        } else {
            "chat:$receiverId:$senderId"
        }
    }

}