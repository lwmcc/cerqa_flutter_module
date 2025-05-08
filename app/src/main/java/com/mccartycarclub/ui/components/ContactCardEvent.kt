package com.mccartycarclub.ui.components

open class ContactCardEvent {
    data class CancelSentInvite(val receiverUserId: String) : ContactCardEvent()
    data class Connect(val connectionAccepted: ConnectionAccepted) : ContactCardEvent()
    data class InviteConnectEvent(val receiverUserId: String) : ContactCardEvent()
    data class DeleteContact(val contactId: String) : ContactCardEvent()
    data class DeleteReceivedInvite(val userId: String) : ContactCardEvent()
    data object DisconnectEvent : ContactCardEvent()
    data class CancelEvent(
        val senderUserId: String?,
        val receiverUserId: String,
        val contactId: String?,
    ) : ContactCardEvent()
}