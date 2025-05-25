package com.mccartycarclub.domain.websocket

import io.ably.lib.rest.Auth


interface RealTime {
    fun initAbly(userId: String, channelName: String, tokenRequest: Auth.TokenRequest?)
}