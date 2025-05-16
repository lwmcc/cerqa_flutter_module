package com.mccartycarclub.repository.realtime

import com.mccartycarclub.domain.websocket.AblyProvider
import com.mccartycarclub.domain.websocket.RealtimeService
import javax.inject.Inject

class PublishRepo @Inject constructor(private val ablyProvider: AblyProvider) :
    RealtimePublishRepo {
}