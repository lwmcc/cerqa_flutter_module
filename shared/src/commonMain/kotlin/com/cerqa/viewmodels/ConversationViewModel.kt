package com.cerqa.viewmodels

import com.cerqa.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val mainDispatcher: CoroutineDispatcher,
    private val conversationRepository: ConversationRepository,
) {
    private val viewModelScope = CoroutineScope(mainDispatcher)

    fun sendChatMessage(message: String) {
        viewModelScope.launch {
            conversationRepository.sendChatMessage(message)
        }
    }

}