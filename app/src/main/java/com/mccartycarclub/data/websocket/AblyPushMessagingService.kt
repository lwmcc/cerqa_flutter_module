package com.mccartycarclub.data.websocket

import com.cerqa.notifications.PushRouter
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.ably.lib.push.ActivationContext
import io.ably.lib.types.RegistrationToken


class AblyPushMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        // FCM data is received here.
        PushRouter.onPushReceived(message.data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Store token
        ActivationContext
            .getActivationContext(this)
            .onNewRegistrationToken(RegistrationToken.Type.FCM, token)
    }

    companion object {
        val PUSH_NOTIFICATION_ACTION: String =
            AblyPushMessagingService::class.java.name + ".PUSH_NOTIFICATION_MESSAGE"
    }
}
