package com.cerqa.repository

import com.cerqa.realtime.AblyService
import kotlinx.coroutines.flow.Flow

class RealtimeRepositoryImpl(private val ablyService: AblyService): RealtimeRepository {

    override fun subscribeToChannel(channelName: String): Flow<String> {
        return ablyService.subscribeToChannel(channelName)
    }

    override suspend fun publishMessage(channelName: String, message: String): Result<Unit> {
        return ablyService.publishMessage(channelName, message)
    }
}