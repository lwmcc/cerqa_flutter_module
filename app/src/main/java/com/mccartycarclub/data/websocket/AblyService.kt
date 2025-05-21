package com.mccartycarclub.data.websocket

import com.mccartycarclub.domain.websocket.AblyProvider
import com.mccartycarclub.domain.websocket.RealtimeService
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.types.AblyException

import javax.inject.Inject
import javax.inject.Named

class AblyService @Inject constructor(val provider: AblyProvider) : RealtimeService {

    private var ably: AblyRealtime? = null

    override fun init(token: String?) {
        if (token != null) {
            ably = provider.getInstance(token)
            ably?.connect()
        }
    }

    override fun connect(token: String?) {
        ably?.connect()
    }

    override fun subscribe() {

    }

    override fun publish() {

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

    private fun setDeviceId() {
        ably?.device()?.id
    }
}