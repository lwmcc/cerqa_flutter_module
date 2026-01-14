package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.cerqa.graphql.CreateChannelMutation
import com.cerqa.graphql.ListChannelMessagesQuery
import com.cerqa.graphql.ListUserChannelsQuery
import com.cerqa.graphql.ListUserSentMessagesQuery
import com.cerqa.graphql.ListUsersQuery
import com.cerqa.graphql.SendMessageMutation
import com.cerqa.graphql.type.ModelIDInput
import com.cerqa.graphql.type.ModelUserFilterInput
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
            // Extract sender and receiver from channelId (format: "chat:userId1:userId2")
            val parts = channelId.split(":")
            if (parts.size != 3 || parts[0] != "chat") {
                println("ConversationRepository: Invalid channelId format: $channelId (expected chat:userId1:userId2)")
                return
            }

            val userId1 = parts[1]
            val userId2 = parts[2]
            val receiverId = if (userId1 == creatorId) userId2 else userId1

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

    override suspend fun getChannelMessages(channelId: String): Result<List<ListChannelMessagesQuery.Item>> {
        return try {
            val response = apolloClient
                .query(ListChannelMessagesQuery(channelId = channelId, limit = Optional.presentIfNotNull(100)))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("ConversationRepository: Error fetching messages: $errors")
                Result.failure(Exception(errors ?: "Unknown error"))
            } else {
                val messages = response.data?.listMessages?.items?.filterNotNull() ?: emptyList()
                println("ConversationRepository: Fetched ${messages.size} messages for channel $channelId")
                Result.success(messages)
            }
        } catch (e: Exception) {
            println("ConversationRepository: Exception fetching messages: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getUserSentMessages(userId: String): Result<List<ListUserSentMessagesQuery.Item>> {
        return try {
            val response = apolloClient
                .query(ListUserSentMessagesQuery(senderId = userId, limit = Optional.presentIfNotNull(500)))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("ConversationRepository: Error fetching user sent messages: $errors")
                Result.failure(Exception(errors ?: "Unknown error"))
            } else {
                val messages = response.data?.listMessages?.items?.filterNotNull() ?: emptyList()
                println("ConversationRepository: Fetched ${messages.size} sent messages for user $userId")
                Result.success(messages)
            }
        } catch (e: Exception) {
            println("ConversationRepository: Exception fetching user sent messages: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getUserByUserId(userId: String): Result<ListUsersQuery.Item?> {
        return try {
            val filter = ModelUserFilterInput(
                userId = Optional.presentIfNotNull(
                    ModelIDInput(eq = Optional.presentIfNotNull(userId))
                )
            )

            val response = apolloClient
                .query(ListUsersQuery(filter = Optional.presentIfNotNull(filter), limit = Optional.presentIfNotNull(1)))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("ConversationRepository: Error fetching user by userId: $errors")
                Result.failure(Exception(errors ?: "Unknown error"))
            } else {
                val user = response.data?.listUsers?.items?.filterNotNull()?.firstOrNull()
                println("ConversationRepository: Found user for userId $userId: ${user?.userName}")
                Result.success(user)
            }
        } catch (e: Exception) {
            println("ConversationRepository: Exception fetching user by userId: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getUsersByUserIds(userIds: List<String>): Result<Map<String, ListUsersQuery.Item>> {
        return try {
            if (userIds.isEmpty()) {
                return Result.success(emptyMap())
            }

            // For now, query each user individually
            // TODO: Optimize with batch query if the API supports it
            val userMap = mutableMapOf<String, ListUsersQuery.Item>()

            for (userId in userIds.distinct()) {
                getUserByUserId(userId).onSuccess { user ->
                    if (user != null) {
                        userMap[userId] = user
                    }
                }
            }

            println("ConversationRepository: Fetched ${userMap.size} users for ${userIds.size} userIds")
            Result.success(userMap)
        } catch (e: Exception) {
            println("ConversationRepository: Exception fetching users by userIds: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}