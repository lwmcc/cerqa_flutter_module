package com.mccartycarclub.data.websocket

import com.mccartycarclub.domain.ChannelModel
import com.mccartycarclub.domain.websocket.RealTime
import com.mccartycarclub.domain.websocket.RealtimeService
import io.ably.lib.rest.Auth
import javax.inject.Inject

class RealTimeMessaging @Inject constructor(private val realtimeService: RealtimeService) :
    RealTime {
    override fun initAbly(userId: String, channelName: String, tokenRequest: Auth.TokenRequest?) {
        realtimeService.init(tokenRequest)
        realtimeService.activatePush()
        realtimeService.createPrivateChannel(channelName)
        realtimeService.subscribeToInviteNotifications(channelName)
    }
}