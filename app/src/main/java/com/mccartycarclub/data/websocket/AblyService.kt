package com.mccartycarclub.data.websocket

import com.mccartycarclub.domain.websocket.AblyProvider
import com.mccartycarclub.domain.websocket.RealtimeService
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.realtime.Channel
import io.ably.lib.realtime.CompletionListener
import io.ably.lib.realtime.ConnectionState
import io.ably.lib.rest.Auth.TokenRequest
import io.ably.lib.types.AblyException
import io.ably.lib.types.ErrorInfo
import io.ably.lib.types.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AblyService @Inject constructor(val provider: AblyProvider) : RealtimeService {

    private var ably: AblyRealtime? = null

    override fun init(token: TokenRequest?) {
        if (token != null) {
            ably = provider.getInstance(token)
            ably?.connect()
        }
    }


    override fun connect(token: String?) {
        ably?.connect()
    }

    override fun createPrivateChannel(channelName: String?) {
        ably?.channels?.get(channelName)
        ably?.connection?.close()
        ably?.connection?.on(
            ConnectionState.closed
        ) { state ->
            when (state?.current) {
                ConnectionState.closed -> {
                    println("AblyService ***** ABLY CLOSED")
                }

                ConnectionState.failed -> {
                    println("AblyService ***** ABLY FAILED TO CLOSE")
                }

                else -> { /* no-op */
                }
            }
        }
    }

    override fun subscribe() {

    }

    override fun publish(channelName: String) {

        val channel = ably?.channels?.get(channelName)

        channel?.publish(
            "New Contact Invitation",
            "You have an invite to connect.",
            object : CompletionListener {
                override fun onSuccess() {
                    println("AblyService ***** You connected")
                }

                override fun onError(reason: ErrorInfo) {
                    println("AblyService ***** Message not sent, error occurred: " + reason.message)
                }
            })
    }

    override fun disconnect() {

    }

    override fun activatePush() {
        try {
            ably?.push?.activate()
        } catch (ae: AblyException) {
            // TODO: log this
        }
    }

    override fun deactivatePush() {
        try {
            ably?.push?.deactivate();
        } catch (ae: AblyException) {

        }
    }

    override fun subscribeToInviteNotifications(channelName: String): Flow<Message> = callbackFlow {
        val channel = ably?.channels?.get(channelName)

        channel?.subscribe(object : Channel.MessageListener {
            override fun onMessage(message: Message?) {
                if (message != null) {
                    trySend(message).onFailure {
                        // TODO: log
                    }
                }
            }
        })

/*        val listener = Channel.MessageListener { message ->
            if (message != null) {
                trySend(message).onFailure {
                    // TODO: log
                }
            }
        }

        channel?.subscribe(listener)
        awaitClose {
            channel?.unsubscribe(listener)
        }*/
    }

    /**
    Receiver of invitation to connect will get a notification letting
    them know that the invite has been sent
     */
    override fun createReceiverInviteSubscription(senderId: String, channelName: String) {
        val channel = ably?.channels?.get(channelName)
        channel?.publish("Invite From Larry", "You have an invite to connect")

        channel?.subscribe(object : Channel.MessageListener {
            override fun onMessage(message: Message?) {
                println("RealTimeMessaging ***** MESSAGE ${message?.name}")
            }
        })
    }
}