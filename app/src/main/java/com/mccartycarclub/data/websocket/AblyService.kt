package com.mccartycarclub.data.websocket

import com.mccartycarclub.domain.websocket.AblyProvider
import com.mccartycarclub.domain.websocket.RealtimeService
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.types.AblyException

import javax.inject.Inject

class AblyService @Inject constructor(val provider: AblyProvider) : RealtimeService {

    private val ably: AblyRealtime by lazy { provider.getInstance() }
    override fun connect() {
        ably.connect()
    }

    override fun subscribe() {

    }

    override fun publish() {

    }

    override fun disconnect() {

    }

    override fun activatePush() {
        try {
            ably.push.activate()
        } catch (ae: AblyException) {
            // TODO: log this
        }
    }

    override fun deactivatePush() {
        try {
            ably.push.deactivate();
        } catch (ae: AblyException) {

        }
    }
}