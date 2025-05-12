package com.mccartycarclub.repository.realtime

interface RealtimeSubscribeRepo {
    fun createUserChannel(userId: String)
}