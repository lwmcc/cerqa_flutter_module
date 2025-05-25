package com.mccartycarclub.domain.websocket

import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.rest.Auth.TokenRequest

interface AblyProvider {
    fun getInstance(token: TokenRequest?): AblyRealtime
}