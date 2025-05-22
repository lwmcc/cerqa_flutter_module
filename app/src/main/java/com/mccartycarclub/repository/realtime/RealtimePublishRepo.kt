package com.mccartycarclub.repository.realtime

import io.ably.lib.types.Message
import kotlinx.coroutines.flow.Flow

interface RealtimePublishRepo {
    fun createPrivateChannel(channelName: String)
    fun publish(channelName: String)
    fun subscribeToInviteNotifications(channelName: String): Flow<Message>
    fun createReceiverInviteSubscription(senderId: String, channelName: String)
}