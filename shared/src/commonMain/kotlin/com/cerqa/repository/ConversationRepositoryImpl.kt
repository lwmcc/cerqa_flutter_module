package com.cerqa.repository

import com.apollographql.apollo.ApolloClient

class ConversationRepositoryImpl(
    private val apolloClient: c
) : ConversationRepository {
    override suspend fun sendChatMessage(message: String) {
        println("ConversationRepositoryImpl ***** SEND CHAT MESSAGE -- $message")
    }
}