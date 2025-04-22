package com.mccartycarclub.ui.components

open class ContactCardActions {
    data class CancelSentInvite(val userId: String) : ContactCardActions()
    data class Connect(val userId: String) : ContactCardActions()
    data class DeleteContact(val userId: String) : ContactCardActions()
    data class DeleteReceivedInvite(val userId: String) : ContactCardActions()
}