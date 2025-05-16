package com.mccartycarclub.domain.websocket

interface RealtimeService {
    fun connect()
    fun subscribe()
    fun publish()
    fun disconnect()
    fun activatePush()
    fun deactivatePush()
}
