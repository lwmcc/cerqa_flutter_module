package com.mccartycarclub.data.websocket

import com.mccartycarclub.domain.websocket.RealTime
import com.mccartycarclub.domain.websocket.RealtimeService
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.realtime.Channel
import io.ably.lib.realtime.Channel.MessageListener
import io.ably.lib.types.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RealTimeMessaging @Inject constructor(private val ably: RealtimeService) : RealTime {

    override fun subscribeToInviteNotifications(channelName: String): Flow<Message> = callbackFlow {
/*        val channel = ably.channels.get(channelName)
        val listener = MessageListener { message ->
            if (message != null) {
                trySend(message).onFailure {
                    // TODO: log
                }
            }
        }

        channel.subscribe(listener)
        awaitClose {
            channel.unsubscribe(listener)
        }*/
    }

    /**
    Receiver of invitation to connect will get a notification letting
    them know that the invite has been sent
     */
    override fun createReceiverInviteSubscription(senderId: String, channelName: String) {

/*        val channel = ably.channels.get(channelName)
        channel.publish("Invite From Larry", "You have an invite to connect")

        channel.subscribe(object : MessageListener {
            override fun onMessage(message: Message?) {
                println("RealTimeMessaging ***** MESSAGE ${message?.name}")
            }
        })*/
    }
}