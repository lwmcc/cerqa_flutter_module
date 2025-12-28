package com.cerqa.notifications

/**
 * Platform-specific FCM token provider.
 */
interface FcmTokenProvider {
    /**
     * Get the current FCM token for this device.
     * @return The FCM token, or null if unavailable
     */
    suspend fun getToken(): String?

    /**
     * Get the platform name ("android" or "ios")
     */
    fun getPlatform(): String
}
