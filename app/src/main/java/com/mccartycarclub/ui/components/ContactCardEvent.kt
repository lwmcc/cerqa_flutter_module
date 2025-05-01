package com.mccartycarclub.ui.components

open class ContactCardEvent {
    data class CancelSentInvite(val receiverUserId: String) : ContactCardEvent()
    data object ConnectClick : ContactCardEvent()
    // data class Connect(val senderUserId: String, val receiverUserId: String?) : ContactCardEvent()
    data class Connect(val connectionAccepted: ConnectionAccepted) : ContactCardEvent()
    data class DeleteContact(val userId: String) : ContactCardEvent()
    data class DeleteReceivedInvite(val userId: String) : ContactCardEvent()
}