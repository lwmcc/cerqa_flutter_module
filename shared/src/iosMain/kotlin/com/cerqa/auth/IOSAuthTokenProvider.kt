package com.cerqa.auth

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Callback interface for iOS to implement using Swift.
 * This allows Swift code to provide Amplify auth functionality to Kotlin.
 */
interface IOSAuthCallback {
    fun getAccessToken(completion: (String?, String?) -> Unit)
    fun isAuthenticated(completion: (Boolean) -> Unit)
    fun getCurrentUserId(completion: (String?) -> Unit)
}

/**
 * iOS implementation of AuthTokenProvider using Amplify Swift SDK.
 * This bridges your existing native Amplify Auth to the shared KMP code.
 *
 * The actual Amplify Swift calls are made via the IOSAuthCallback
 * which is provided from the iOS app side.
 */
class IOSAuthTokenProvider : AuthTokenProvider {

    // This will be set from the iOS side before any auth calls are made
    var authCallback: IOSAuthCallback? = null

    override suspend fun getAccessToken(): String = suspendCoroutine { continuation ->
        val callback = authCallback
        if (callback == null) {
            continuation.resumeWithException(
                AuthenticationException("IOSAuthCallback not configured. Call setIOSAuthCallback first.")
            )
            return@suspendCoroutine
        }

        callback.getAccessToken { token, error ->
            if (token != null) {
                continuation.resume(token)
            } else {
                continuation.resumeWithException(
                    AuthenticationException(
                        message = error ?: "Failed to get access token",
                        cause = null
                    )
                )
            }
        }
    }

    override suspend fun isAuthenticated(): Boolean = suspendCoroutine { continuation ->
        val callback = authCallback
        if (callback == null) {
            continuation.resume(false)
            return@suspendCoroutine
        }

        callback.isAuthenticated { isAuth ->
            continuation.resume(isAuth)
        }
    }

    override suspend fun getCurrentUserId(): String? = suspendCoroutine { continuation ->
        val callback = authCallback
        if (callback == null) {
            continuation.resume(null)
            return@suspendCoroutine
        }

        callback.getCurrentUserId { userId ->
            continuation.resume(userId)
        }
    }
}

/**
 * Helper function to set the iOS auth callback.
 * Call this from Swift during app initialization.
 */
fun setIOSAuthCallback(provider: IOSAuthTokenProvider, callback: IOSAuthCallback) {
    provider.authCallback = callback
}
