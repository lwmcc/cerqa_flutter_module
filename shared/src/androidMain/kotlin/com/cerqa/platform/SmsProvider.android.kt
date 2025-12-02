package com.cerqa.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager

/**
 * Android implementation of SmsProvider
 * Uses Android's Intent system to open the SMS composer
 */
actual class SmsProvider(private val context: Context) {

    /**
     * Send SMS by opening the default SMS app with pre-filled message
     */
    actual fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsUri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            println("Error sending SMS: ${e.message}")
        }
    }

    /**
     * Check if SMS is available on this device
     */
    actual fun isSmsAvailable(): Boolean {
        return try {
            val smsManager = context.getSystemService(Context.TELEPHONY_SERVICE)
            smsManager != null
        } catch (e: Exception) {
            false
        }
    }
}
