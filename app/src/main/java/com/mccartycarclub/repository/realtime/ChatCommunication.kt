package com.mccartycarclub.repository.realtime

interface ChatCommunication {
    fun connect()
    fun sendMessage()
    fun observeMessages()
    fun disconnect()
}