package com.cerqa.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.MessageUI.MFMessageComposeViewController

/**
 * iOS implementation of SmsProvider.
 * Uses iOS's URL scheme and MessageUI framework to send SMS.
 */
@OptIn(ExperimentalForeignApi::class)
actual class SmsProvider {

    /**
     * Send SMS using iOS URL scheme.
     */
    actual fun sendSms(phoneNumber: String, message: String) {
        try {
            // URL encode the message
            val encodedMessage = message.replace(" ", "%20")
                .replace("\n", "%0A")

            // Create SMS URL
            val smsUrl = NSURL.URLWithString("sms:$phoneNumber&body=$encodedMessage")

            smsUrl?.let { url ->
                if (UIApplication.sharedApplication.canOpenURL(url)) {
                    UIApplication.sharedApplication.openURL(url)
                }
            }
        } catch (e: Exception) {
            println("Error sending SMS: ${e.message}")
        }
    }

    /**
     * Check if SMS is available on this device.
     */
    actual fun isSmsAvailable(): Boolean {
        return MFMessageComposeViewController.canSendText()
    }
}
