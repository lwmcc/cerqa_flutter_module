package com.cerqa.realtime

import com.cerqa.graphql.FetchAblyJwtQuery
import com.cerqa.repository.AblyRepository
import kotlinx.coroutines.flow.Flow

/**
 * High-level service for managing Ably Realtime connections.
 * Handles token fetching and client initialization.
 */
class AblyService(
    private val ablyRepository: AblyRepository,
    private val ablyClient: AblyClient = createAblyClient()
) {
    /**
     * Initialize Ably for the given user.
     * Fetches the authentication token and connects to Ably.
     *
     * @param userId The user ID to authenticate
     * @return Result indicating success or failure
     */
    suspend fun initialize(userId: String): Result<Unit> {
        println("AblyService: Initializing for user: $userId")

        // Fetch token from backend
        val tokenResult = ablyRepository.fetchAblyToken(userId)
        if (tokenResult.isFailure) {
            val error = tokenResult.exceptionOrNull()
            println("AblyService: Failed to fetch token: ${error?.message}")
            return Result.failure(error ?: Exception("Unknown error fetching token"))
        }

        val tokenResponse = tokenResult.getOrNull()!!
        println("AblyService: Token fetched successfully")

        // Convert to platform-agnostic token data
        val tokenData = AblyTokenData(
            keyName = tokenResponse.keyName,
            clientId = tokenResponse.clientId,
            timestamp = tokenResponse.timestamp.toLong(),
            nonce = tokenResponse.nonce,
            mac = tokenResponse.mac
        )

        // Initialize Ably client
        return ablyClient.initialize(tokenData)
    }

    /**
     * Subscribe to a channel and receive messages.
     *
     * @param channelName The name of the channel
     * @return Flow of messages
     */
    fun subscribeToChannel(channelName: String): Flow<String> {
        println("AblyService: Subscribing to channel: $channelName")
        return ablyClient.subscribeToChannel(channelName)
    }

    /**
     * Publish a message to a channel.
     *
     * @param channelName The name of the channel
     * @param message The message to publish
     */
    suspend fun publishMessage(channelName: String, message: String): Result<Unit> {
        println("AblyService: Publishing message to channel: $channelName")
        return ablyClient.publishMessage(channelName, message)
    }

    /**
     * Get the current connection state.
     */
    fun getConnectionState(): Flow<String> {
        return ablyClient.getConnectionState()
    }

    /**
     * Disconnect from Ably and clean up resources.
     */
    fun disconnect() {
        println("AblyService: Disconnecting")
        ablyClient.disconnect()
    }
}
