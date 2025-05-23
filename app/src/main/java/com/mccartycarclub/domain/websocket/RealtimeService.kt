package com.mccartycarclub.domain.websocket

import io.ably.lib.types.Message
import kotlinx.coroutines.flow.Flow

interface RealtimeService {
    fun init(token: String?)
    fun connect(token: String?)
    fun createPrivateChannel(channelName: String?)
    fun subscribe()
    fun publish(channelName: String)
    fun disconnect()
    fun activatePush()
    fun deactivatePush()
    fun subscribeToInviteNotifications(channelName: String): Flow<Message>
    fun createReceiverInviteSubscription(senderId: String, channelName: String)
}
