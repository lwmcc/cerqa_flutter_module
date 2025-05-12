package com.mccartycarclub.repository.realtime

interface RealtimeSubscribeRepo {
    suspend fun createUserChannel(userId: String)
}