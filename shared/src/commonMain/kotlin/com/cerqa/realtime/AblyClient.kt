package com.cerqa.realtime

import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic interface for Ably Realtime client.
 * Platform-specific implementations will handle actual Ably SDK integration.
 */
interface AblyClient {
    /**
     * Initialize Ably with the given token data.
     *
     * @param tokenData The authentication token from the backend
     * @return Result indicating success or failure
     */
    suspend fun initialize(tokenData: AblyTokenData): Result<Unit>

    /**
     * Subscribe to a channel and listen for messages.
     *
     * @param channelName The name of the channel to subscribe to
     * @return Flow of messages received on the channel
     */
    fun subscribeToChannel(channelName: String): Flow<String>

    /**
     * Publish a message to a channel.
     *
     * @param channelName The name of the channel
     * @param message The message to publish
     */
    suspend fun publishMessage(channelName: String, message: String): Result<Unit>

    /**
     * Disconnect from Ably and clean up resources.
     */
    fun disconnect()

    /**
     * Get the connection state as a Flow.
     * Values: "connecting", "connected", "disconnected", "failed", etc.
     */
    fun getConnectionState(): Flow<String>
}

/**
 * Factory function to create platform-specific Ably client.
 * This is implemented using expect/actual pattern.
 */
expect fun createAblyClient(): AblyClient
