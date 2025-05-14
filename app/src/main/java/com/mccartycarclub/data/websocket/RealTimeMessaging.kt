package com.mccartycarclub.data.websocket

import com.mccartycarclub.domain.websocket.RealTime
import io.ably.lib.realtime.AblyRealtime
import javax.inject.Inject

class RealTimeMessaging @Inject constructor(private val ably: AblyRealtime) : RealTime {
    override fun publish() {

    }

    override fun subscribe() {

    }
}