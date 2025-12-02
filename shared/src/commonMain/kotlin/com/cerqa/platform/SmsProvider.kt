package com.cerqa.platform

/**
 * Platform-specific interface for sending SMS messages.
 */
expect class SmsProvider {
    /**
     * Send an SMS message to the specified phone number.
     * Opens the platform's SMS composer with the pre-filled message.
     *
     * @param phoneNumber The recipient's phone number
     * @param message The message text to send
     */
    fun sendSms(phoneNumber: String, message: String)

    /**
     * Check if SMS functionality is available on this device.
     */
    fun isSmsAvailable(): Boolean
}
