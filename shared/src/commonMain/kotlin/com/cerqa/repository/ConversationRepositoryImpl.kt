package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.cerqa.graphql.CreateChannelMutation
import com.cerqa.graphql.ListUserChannelsQuery
import com.cerqa.graphql.SendMessageMutation
import com.cerqa.graphql.type.CreateChannelInput
import com.cerqa.graphql.type.CreateMessageInput
import com.cerqa.realtime.AblyClient
import com.cerqa.realtime.RealtimeChannel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ChatMessage(
    val id: String? = null,
    val senderId: String,
    val content: String
)

class ConversationRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val ablyClient: AblyClient
) : ConversationRepository {

    override suspend fun sendChatMessage(
        channelId: String,
        senderUserId: String,
        content: String
    ) {
        try {
            // Ensure channel exists before sending message
            ensureChannelExists(channelId, senderUserId)

            // Create and send message
            val input = CreateMessageInput(
                channelId = channelId,
                senderId = senderUserId,
                content = content
            )

            val mutation = SendMessageMutation(input = input)

            // Send to backend (AppSync)
            val response = apolloClient
                .mutation(mutation)
                .execute()

            if (response.data != null) {
                val message = response.data?.createMessage
                val messageId = message?.id

                // Send to Ably realtime channel
                val chatChannel = RealtimeChannel.Chat(channelId)
                val chatMessage = ChatMessage(
                    id = messageId,
                    senderId = senderUserId,
                    content = content
                )
                val messageJson = Json.encodeToString(chatMessage)

                ablyClient.publishMessage(chatChannel.name, messageJson)
            }

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("ConversationRepository: Error sending message: $errors")
            }

        } catch (e: Exception) {
            println("ConversationRepository: Exception sending message: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun ensureChannelExists(channelId: String, creatorId: String) {
        try {
            // Extract sender and receiver from channelId (format: "userId1_userId2")
            val userIds = channelId.split("_")
            if (userIds.size != 2) {
                println("ConversationRepository: Invalid channelId format: $channelId")
                return
            }

            val receiverId = if (userIds[0] == creatorId) userIds[1] else userIds[0]

            // Try to create the channel (will fail silently if it already exists)
            val input = CreateChannelInput(
                id = Optional.presentIfNotNull(channelId),
                name = channelId, // Use channelId as name for 1-on-1 chats
                creatorId = creatorId,
                receiverId = receiverId,
                isGroup = Optional.presentIfNotNull(false),
                isPublic = Optional.presentIfNotNull(false)
            )

            val mutation = CreateChannelMutation(input = input)
            val response = apolloClient.mutation(mutation).execute()

            if (response.hasErrors()) {
                // Channel might already exist, which is fine
                println("ConversationRepository: Channel creation response (may already exist): ${response.errors?.firstOrNull()?.message}")
            } else {
                println("ConversationRepository: Channel created successfully: $channelId")
            }

        } catch (e: Exception) {
            // Channel might already exist, continue anyway
            println("ConversationRepository: Channel creation error (may already exist): ${e.message}")
        }
    }

    override suspend fun getUserChannels(userId: String): Result<List<ListUserChannelsQuery.Item>> {
        return try {
            val response = apolloClient
                .query(ListUserChannelsQuery(userId))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("ConversationRepository: Error fetching channels: $errors")
                Result.failure(Exception(errors ?: "Unknown error"))
            } else {
                val channels = response.data?.listChannels?.items?.filterNotNull() ?: emptyList()
                println("ConversationRepository: Fetched ${channels.size} channels")
                Result.success(channels)
            }
        } catch (e: Exception) {
            println("ConversationRepository: Exception fetching channels: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}