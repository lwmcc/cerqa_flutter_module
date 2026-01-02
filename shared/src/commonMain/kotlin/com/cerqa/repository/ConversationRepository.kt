package com.cerqa.repository

interface ConversationRepository {
    suspend fun sendChatMessage(message: String)
}