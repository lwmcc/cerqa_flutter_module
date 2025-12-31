package com.cerqa.realtime

import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.realtime.Channel
import io.ably.lib.realtime.CompletionListener
import io.ably.lib.realtime.ConnectionState
import io.ably.lib.rest.Auth
import io.ably.lib.types.AblyException
import io.ably.lib.types.ClientOptions
import io.ably.lib.types.ErrorInfo
import io.ably.lib.types.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Android implementation of AblyClient using Ably Android SDK.
 */
class AndroidAblyClient : AblyClient {
    private var realtime: AblyRealtime? = null
    private val connectionState = MutableStateFlow("disconnected")
    private val activeChannels = mutableMapOf<String, Channel>()

    override suspend fun initialize(tokenData: AblyTokenData): Result<Unit> = suspendCoroutine { continuation ->
        try {
            println("AndroidAblyClient: Initializing with token")

            val options = ClientOptions().apply {
                // Use token authentication with authCallback
                authCallback = Auth.TokenCallback { params ->
                    println("AndroidAblyClient: Creating token request")
                    Auth.TokenRequest().apply {
                        keyName = tokenData.keyName
                        clientId = tokenData.clientId
                        timestamp = tokenData.timestamp
                        nonce = tokenData.nonce
                        mac = tokenData.mac
                    }
                }
                this.clientId = tokenData.clientId
            }

            realtime = AblyRealtime(options)

            // Listen for connection state changes
            realtime?.connection?.on { stateChange ->
                val state = stateChange.current.name.lowercase()
                println("AndroidAblyClient: Connection state changed to: $state")
                connectionState.value = state

                if (state == "connected") {
                    println("AndroidAblyClient: Successfully connected")
                    continuation.resume(Result.success(Unit))
                } else if (state == "failed" || state == "suspended") {
                    val reason = stateChange.reason?.message ?: "Unknown error"
                    println("AndroidAblyClient: Connection failed: $reason")
                    continuation.resume(Result.failure(Exception(reason)))
                }
            }

        } catch (e: Exception) {
            println("AndroidAblyClient: Initialization error: ${e.message}")
            e.printStackTrace()
            continuation.resume(Result.failure(e))
        }
    }

    override fun subscribeToChannel(channelName: String): Flow<String> = callbackFlow {
        try {
            println("AndroidAblyClient: Subscribing to channel: $channelName")

            val channel = realtime?.channels?.get(channelName)
            if (channel == null) {
                close(Exception("Ably not initialized"))
                return@callbackFlow
            }

            activeChannels[channelName] = channel

            // Attach to channel
            channel.attach()

            // Subscribe to messages
            val listener = Channel.MessageListener { message ->
                println("AndroidAblyClient: Received message: ${message.data}")
                trySend(message.data.toString())
            }

            channel.subscribe(listener)

            awaitClose {
                println("AndroidAblyClient: Unsubscribing from channel: $channelName")
                channel.unsubscribe(listener)
                channel.detach()
                activeChannels.remove(channelName)
            }
        } catch (e: Exception) {
            println("AndroidAblyClient: Error subscribing: ${e.message}")
            close(e)
        }
    }

    override suspend fun publishMessage(channelName: String, message: String): Result<Unit> = suspendCoroutine { continuation ->
            try {
                val channel = realtime?.channels?.get(channelName)
                if (channel == null) {
                    continuation.resume(Result.failure(Exception("Ably not initialized")))
                    return@suspendCoroutine
                }

                channel.publish("message", message, object : CompletionListener {
                    override fun onSuccess() {
                        println("AndroidAblyClient: Message published successfully")
                        continuation.resume(Result.success(Unit))
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        println("AndroidAblyClient: Publish error: ${errorInfo?.message}")
                        continuation.resume(Result.failure(Exception(errorInfo?.message ?: "Unknown error")))
                    }
                })
            } catch (e: Exception) {
                println("AndroidAblyClient: Exception publishing message: ${e.message}")
                continuation.resume(Result.failure(e))
            }
        }

    override fun disconnect() {
        println("AndroidAblyClient: Disconnecting")
        activeChannels.values.forEach { it.detach() }
        activeChannels.clear()
        realtime?.close()
        realtime = null
        connectionState.value = "disconnected"
    }

    override fun getConnectionState(): Flow<String> = connectionState
}

/**
 * Factory function to create Android Ably client.
 */
actual fun createAblyClient(): AblyClient = AndroidAblyClient()
