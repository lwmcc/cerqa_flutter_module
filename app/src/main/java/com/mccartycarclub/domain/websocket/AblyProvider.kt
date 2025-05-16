package com.mccartycarclub.domain.websocket

import io.ably.lib.realtime.AblyRealtime

interface AblyProvider {
    fun getInstance(): AblyRealtime
}