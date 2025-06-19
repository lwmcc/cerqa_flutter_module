package com.mccartycarclub.ui.components

sealed class ContactCardEvent {
    data class CancelSentInvite(val receiverUserId: String) : ContactCardEvent()
    data class AcceptConnection(val connectionAccepted: ConnectionAccepted) : ContactCardEvent()
    data class DeleteContact(val contactId: String) : ContactCardEvent()
    data class DeleteReceivedInvite(val userId: String) : ContactCardEvent()
}

sealed class ContactCardConnectionEvent {
    data class InviteConnectEvent(val receiverUserId: String, val rowId: String) :
        ContactCardConnectionEvent()
}

