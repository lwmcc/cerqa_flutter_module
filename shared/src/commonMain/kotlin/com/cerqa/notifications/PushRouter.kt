package com.cerqa.notifications

object PushRouter {
    fun onPushReceived(data: Map<String, String>) {
        when (data["type"]) {
            "message" -> {
                println("PushRouter ***** MESSAGE")
            }

            "invite" -> {
                println("PushRouter ***** INVITE")
            }
        }
    }
}