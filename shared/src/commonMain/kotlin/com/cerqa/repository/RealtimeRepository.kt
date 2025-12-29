package com.cerqa.repository

import kotlinx.coroutines.flow.Flow

interface RealtimeRepository {
    /**
     * Subscribe to a channel and receive messages.
     * @param channelName The name of the channel to subscribe to
     * @return Flow of messages from the channel
     */
    fun subscribeToChannel(channelName: String): Flow<String>

    /**
     * Publish a message to a channel.
     * @param channelName The channel to publish to
     * @param message The message to send
     */
    suspend fun publishMessage(channelName: String, message: String): Result<Unit>
}