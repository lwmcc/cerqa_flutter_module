package com.cerqa.realtime

sealed class RealtimeChannel(protected val prefix: String) {
    abstract val name: String

    data class Chat(val conversationId: String) : RealtimeChannel(prefix = "chat:") {
        override val name: String
            get() = "$prefix$conversationId"
    }

    data class InboxMessages(val userId: String) : RealtimeChannel(prefix = "inbox:messages:") {
        override val name: String
            get() = "$prefix$userId"
    }

    data class InboxNotifications(val userId: String) : RealtimeChannel(prefix = "inbox:notifications:") {
        override val name: String
            get() = "$prefix$userId"
    }
}