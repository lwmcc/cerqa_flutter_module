package com.mccartycarclub.domain.helpers

const val CHANNEL_ID_SEPARATOR = ":"

fun String.createChannelId(receiverUserId: String): String {
    return if (this < receiverUserId) {
        "$this$CHANNEL_ID_SEPARATOR$receiverUserId"
    } else {
        "$receiverUserId$CHANNEL_ID_SEPARATOR$this"
    }
}
