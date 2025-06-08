package com.mccartycarclub.data.websocket

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.ably.lib.push.ActivationContext
import io.ably.lib.types.RegistrationToken


class AblyPushMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        //FCM data is received here.
        val intent = Intent(PUSH_NOTIFICATION_ACTION)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        println("AblyPushMessagingService ***** RECEIVED PUSH WHEN APP OPEN ${intent.action}")
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s!!)
        // Store token in Ably
        ActivationContext.getActivationContext(this)
            .onNewRegistrationToken(RegistrationToken.Type.FCM, s)
    }

    companion object {
        val PUSH_NOTIFICATION_ACTION: String =
            AblyPushMessagingService::class.java.name + ".PUSH_NOTIFICATION_MESSAGE"
    }
}
