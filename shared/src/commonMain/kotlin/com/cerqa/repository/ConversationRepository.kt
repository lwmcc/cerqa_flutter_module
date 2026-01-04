package com.cerqa.repository

import com.cerqa.graphql.ListChannelMessagesQuery
import com.cerqa.graphql.ListUserChannelsQuery

interface ConversationRepository {
    suspend fun sendChatMessage(
        channelId: String,
        senderUserId: String,
        content: String
    )

    suspend fun getUserChannels(userId: String): Result<List<ListUserChannelsQuery.Item>>

    suspend fun getChannelMessages(channelId: String): Result<List<ListChannelMessagesQuery.Item>>
}