package com.cerqa.notifications

import cocoapods.FirebaseMessaging.FIRMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class IosFcmTokenProvider : FcmTokenProvider {
    override suspend fun getToken(): String? {
        return suspendCancellableCoroutine { continuation ->
            FIRMessaging.messaging().tokenWithCompletion { token, error ->
                if (error != null) {
                    println("IosFcmTokenProvider: Error getting FCM token: ${error.localizedDescription}")
                    continuation.resume(null)
                } else {
                    continuation.resume(token)
                }
            }
        }
    }

    override fun getPlatform(): String = "ios"
}
