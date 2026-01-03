package com.cerqa.viewmodels

import com.cerqa.data.Preferences
import com.cerqa.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val preferences: Preferences,
    private val mainDispatcher: CoroutineDispatcher,
    private val conversationRepository: ConversationRepository,
) {
    private val viewModelScope = CoroutineScope(mainDispatcher)

    fun sendChatMessage(channelId: String, senderUserId: String, receiverId: String, message: String) {
        viewModelScope.launch {

            preferences.getUserData().let { user ->
                conversationRepository.sendChatMessage(
                    channelId = createConversationChannelName(
                        receiverId,
                        senderId = user.userId.toString()
                    ),
                    senderUserId,
                    content = message,
                )
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