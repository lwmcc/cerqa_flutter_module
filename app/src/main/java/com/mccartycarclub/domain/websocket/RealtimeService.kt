package com.mccartycarclub.domain.websocket

interface RealtimeService {
    fun init(token: String?)
    fun connect(token: String?)
    fun subscribe()
    fun publish()
    fun disconnect()
    fun activatePush()
    fun deactivatePush()
}
