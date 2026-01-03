package com.cerqa.repository

import com.cerqa.graphql.ListUserChannelsQuery

interface ConversationRepository {
    suspend fun sendChatMessage(
        channelId: String,
        senderUserId: String,
        content: String
    )

    suspend fun getUserChannels(userId: String): Result<List<ListUserChannelsQuery.Item>>
}