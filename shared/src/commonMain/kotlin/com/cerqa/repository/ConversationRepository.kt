package com.cerqa.repository

import com.cerqa.graphql.ListChannelMessagesQuery
import com.cerqa.graphql.ListUserChannelsQuery
import com.cerqa.graphql.ListUserSentMessagesQuery
import com.cerqa.graphql.ListUsersQuery

interface ConversationRepository {
    suspend fun sendChatMessage(
        channelId: String,
        senderUserId: String,
        content: String
    )

    suspend fun getUserChannels(userId: String): Result<List<ListUserChannelsQuery.Item>>

    suspend fun getChannelMessages(channelId: String): Result<List<ListChannelMessagesQuery.Item>>

    suspend fun getUserSentMessages(userId: String): Result<List<ListUserSentMessagesQuery.Item>>

    suspend fun getUserByUserId(userId: String): Result<ListUsersQuery.Item?>

    suspend fun getUsersByUserIds(userIds: List<String>): Result<Map<String, ListUsersQuery.Item>>
}