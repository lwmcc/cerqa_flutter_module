package com.cerqa.realtime

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.cinterop.*
import platform.Foundation.*
import cocoapods.Ably.*

/**
 * iOS implementation of AblyClient using Ably Cocoa SDK.
 *
 * Note: This requires the Ably Cocoa Pod to be included in the iOS project.
 * Add to shared/build.gradle.kts:
 *
 * cocoapods {
 *     pod("Ably") {
 *         version = "1.2.33"
 *     }
 * }
 */
class IOSAblyClient : AblyClient {
    private var realtime: ARTRealtime? = null
    private val connectionState = MutableStateFlow("disconnected")
    private val activeChannels = mutableMapOf<String, ARTRealtimeChannel>()

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun initialize(tokenData: AblyTokenData): Result<Unit> {
        return try {
            println("IOSAblyClient: Initializing with token")

            val options = ARTClientOptions().apply {
                // Set up token authentication
                authCallback = { params, callback ->
                    val tokenParams = ARTTokenParams().apply {
                        clientId = tokenData.clientId
                        ttl = 3600.0 // 1 hour in seconds
                    }

                    val tokenRequest = ARTTokenRequest().apply {
                        keyName = tokenData.keyName
                        this.clientId = tokenData.clientId
                        timestamp = NSNumber(longLong = tokenData.timestamp)
                        nonce = tokenData.nonce
                        mac = tokenData.mac
                    }

                    callback?.invoke(tokenRequest, null)
                }
                this.clientId = tokenData.clientId
            }

            realtime = ARTRealtime(options)

            // Monitor connection state
            realtime?.connection?.on { stateChange ->
                val state = when (stateChange?.current) {
                    ARTRealtimeConnectionState.ARTRealtimeConnecting -> "connecting"
                    ARTRealtimeConnectionState.ARTRealtimeConnected -> "connected"
                    ARTRealtimeConnectionState.ARTRealtimeDisconnected -> "disconnected"
                    ARTRealtimeConnectionState.ARTRealtimeSuspended -> "suspended"
                    ARTRealtimeConnectionState.ARTRealtimeClosing -> "closing"
                    ARTRealtimeConnectionState.ARTRealtimeClosed -> "closed"
                    ARTRealtimeConnectionState.ARTRealtimeFailed -> "failed"
                    else -> "unknown"
                }
                println("IOSAblyClient: Connection state: $state")
                connectionState.value = state
            }

            println("IOSAblyClient: Successfully initialized")
            Result.success(Unit)
        } catch (e: Exception) {
            println("IOSAblyClient: Initialization error: ${e.message}")
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun subscribeToChannel(channelName: String): Flow<String> = callbackFlow {
        try {
            println("IOSAblyClient: Subscribing to channel: $channelName")

            val channel = realtime?.channels?.get(channelName)
            if (channel == null) {
                close(Exception("Ably not initialized"))
                return@callbackFlow
            }

            activeChannels[channelName] = channel

            // Attach to channel
            channel.attach()

            // Subscribe to messages
            channel.subscribe { message ->
                message?.data?.let { data ->
                    println("IOSAblyClient: Received message: $data")
                    trySend(data.toString())
                }
            }

            awaitClose {
                println("IOSAblyClient: Unsubscribing from channel: $channelName")
                channel.detach()
                activeChannels.remove(channelName)
            }
        } catch (e: Exception) {
            println("IOSAblyClient: Error subscribing: ${e.message}")
            close(e)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun publishMessage(channelName: String, message: String): Result<Unit> {
        return try {
            val channel = realtime?.channels?.get(channelName)
            if (channel == null) {
                return Result.failure(Exception("Ably not initialized"))
            }

            channel.publish("message", message) { error ->
                if (error != null) {
                    println("IOSAblyClient: Publish error: ${error.localizedDescription}")
                } else {
                    println("IOSAblyClient: Message published successfully")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            println("IOSAblyClient: Exception publishing message: ${e.message}")
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun disconnect() {
        println("IOSAblyClient: Disconnecting")
        activeChannels.values.forEach { channel ->
            channel.detach()
        }
        activeChannels.clear()
        realtime?.connection?.close()
        realtime = null
        connectionState.value = "disconnected"
    }

    override fun getConnectionState(): Flow<String> = connectionState
}

/**
 * Factory function to create iOS Ably client.
 */
actual fun createAblyClient(): AblyClient = IOSAblyClient()
