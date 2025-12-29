package com.cerqa.repository

import com.cerqa.realtime.AblyService
import kotlinx.coroutines.flow.Flow

class RealtimeRepositoryImpl(private val ablyService: AblyService): RealtimeRepository {

    override fun subscribeToChannel(channelName: String): Flow<String> {
        println("RealtimeRepositoryImpl ***** Subscribing to channel: $channelName")
        return ablyService.subscribeToChannel(channelName)
    }

    override suspend fun publishMessage(channelName: String, message: String): Result<Unit> {
        println("RealtimeRepositoryImpl ***** Publishing message to channel: $channelName")
        return ablyService.publishMessage(channelName, message)
    }
}