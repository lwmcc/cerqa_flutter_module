package com.cerqa.notifications

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * iOS FCM token provider that delegates to native iOS Firebase implementation.
 * The native implementation should call `IosFcmTokenProvider.setNativeTokenProvider()`
 * during app initialization.
 */
class IosFcmTokenProvider : FcmTokenProvider {
    companion object {
        private var nativeTokenProvider: ((callback: (String?) -> Unit) -> Unit)? = null

        /**
         * Called from native iOS code to inject the Firebase token provider.
         * This should be called with a callback that receives the token.
         */
        fun setNativeTokenProvider(provider: (callback: (String?) -> Unit) -> Unit) {
            nativeTokenProvider = provider
        }
    }

    override suspend fun getToken(): String? {
        val provider = nativeTokenProvider
        if (provider == null) {
            println("IosFcmTokenProvider: Native token provider not set. Please initialize Firebase in your iOS app.")
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            provider { token: String? ->
                continuation.resume(token)
            }
        }
    }

    override fun getPlatform(): String = "ios"
}
