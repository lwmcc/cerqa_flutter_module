package com.cerqa.notifications

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class AndroidFcmTokenProvider : FcmTokenProvider {
    override suspend fun getToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            // TODO: log this
            println("AndroidFcmTokenProvider ***** Error getting FCM token: ${e.message}")
            null
        }
    }

    override fun getPlatform(): String = "android"
}
