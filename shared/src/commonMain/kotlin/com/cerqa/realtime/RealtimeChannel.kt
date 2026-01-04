package com.cerqa.realtime

sealed class RealtimeChannel(val prefix: String) {
    abstract val name: String

    data class UserInbox(val userId: String) : RealtimeChannel("inbox:user:") {
        override val name: String
            get() = "user:$userId:inbox"
    }

    data class Chat(val conversationId: String) : RealtimeChannel("chat:") {
        override val name: String
            get() = "chat:$conversationId"
    }

    companion object {
        const val NOTIFICATIONS_INVITES = "notifications:invites"
    }
}