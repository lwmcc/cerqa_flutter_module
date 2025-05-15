package com.mccartycarclub.domain.websocket

import io.ably.lib.types.Message
import kotlinx.coroutines.flow.Flow

interface RealTime {

    fun subscribeToInviteNotifications(channelName: String): Flow<Message>

    fun createReceiverInviteSubscription(senderId: String, channelName: String)
}