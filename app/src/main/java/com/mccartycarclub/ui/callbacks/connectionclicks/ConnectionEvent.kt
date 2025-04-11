package com.mccartycarclub.ui.callbacks.connectionclicks

sealed class ConnectionEvent {
    data object DisconnectEvent : ConnectionEvent()
    data class ConnectEvent(val receiverUserId: String) : ConnectionEvent()
    data class CancelEvent(val receiverUserId: String) : ConnectionEvent()
}