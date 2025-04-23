package com.mccartycarclub.ui.components

open class ContactCardEvent {
    data class CancelSentInvite(val userId: String) : ContactCardEvent()
    data object ConnectClick : ContactCardEvent()
    data class Connect(val senderUserId: String, val receiverUserId: String?) : ContactCardEvent()
    data class DeleteContact(val userId: String) : ContactCardEvent()
    data class DeleteReceivedInvite(val userId: String) : ContactCardEvent()
}