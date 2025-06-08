package com.mccartycarclub.repository.realtime

import com.mccartycarclub.domain.websocket.RealtimeService
import io.ably.lib.types.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PublishRepo @Inject constructor(private val realtimeService: RealtimeService) :
    RealtimePublishRepo {
    override fun createPrivateChannel(channelName: String) {
        realtimeService.createPrivateChannel(channelName)
    }

    override fun publish(channelName: String) {
        realtimeService.publish(channelName)
    }

    override fun subscribeToInviteNotifications(channelName: String): Flow<Message> {
        return realtimeService.subscribeToInviteNotifications(channelName)
    }

    override fun createReceiverInviteSubscription(senderId: String, channelName: String) {
        realtimeService.createReceiverInviteSubscription(senderId, channelName)
    }
}
