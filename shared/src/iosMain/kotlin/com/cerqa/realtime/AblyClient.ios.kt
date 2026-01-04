package com.cerqa.realtime

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * iOS implementation of AblyClient using Ably Cocoa SDK.
 *
 * TODO: Implement full Ably integration for iOS
 * This is currently a stub to allow the build to complete.
 */
class IOSAblyClient : AblyClient {
    private val connectionState = MutableStateFlow("disconnected")

    override suspend fun initialize(tokenData: AblyTokenData): Result<Unit> {
        println("IOSAblyClient: Stub implementation - initialization not yet implemented")
        return Result.success(Unit)
    }

    override fun subscribeToChannel(channelName: String): Flow<String> {
        println("IOSAblyClient: Stub implementation - subscribeToChannel not yet implemented for $channelName")
        return flowOf()
    }

    override suspend fun publishMessage(channelName: String, message: String): Result<Unit> {
        println("IOSAblyClient: Stub implementation - publishMessage not yet implemented")
        return Result.success(Unit)
    }

    override fun disconnect() {
        println("IOSAblyClient: Stub implementation - disconnect not yet implemented")
        connectionState.value = "disconnected"
    }

    override fun getConnectionState(): Flow<String> = connectionState
}

/**
 * Factory function to create iOS Ably client.
 */
actual fun createAblyClient(): AblyClient = IOSAblyClient()
