package com.cerqa.realtime

enum class RealtimeChannel(val prefix: String) {
    NOTIFICATIONS("notifications:"),
    NOTIFICATIONS_INVITES("notifications:invites:");

    fun build(id: String): String = "$prefix$id"
}