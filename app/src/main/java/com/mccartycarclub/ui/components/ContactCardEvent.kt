package com.mccartycarclub.ui.components

open class ContactCardEvent {
    data class CancelSentInvite(val receiverUserId: String) : ContactCardEvent()
    data class AcceptConnection(val connectionAccepted: ConnectionAccepted) : ContactCardEvent()
    data class InviteConnectEvent(val receiverUserId: String) : ContactCardEvent()
    data class DeleteContact(val contactId: String) : ContactCardEvent()
    data class DeleteReceivedInvite(val userId: String) : ContactCardEvent()
    data object DisconnectEvent : ContactCardEvent()
}