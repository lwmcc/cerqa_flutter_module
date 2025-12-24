package com.cerqa.notifications

import com.cerqa.realtime.AblyService
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Implementation of Notifications using Ably for real-time delivery.
 * Sends push notifications via Ably channels to trigger FCM on the recipient's device.
 */
class NotificationsImpl(
    private val ablyService: AblyService
) : Notifications {

    override suspend fun sendConnectionInviteNotification(
        recipientUserId: String,
        senderName: String,
        senderUserName: String
    ): Result<Unit> {
        return try {
            println("NotificationsImpl: Sending connection invite notification to user: $recipientUserId")

            // Create notification payload
            val notification = ConnectionInviteNotification(
                type = "connection_invite",
                senderName = senderName,
                senderUserName = senderUserName,
                message = "$senderName (@$senderUserName) sent you a connection invite"
            )

            // Serialize to JSON
            val messageJson = Json.encodeToString(notification)

            // Send to the recipient's user channel
            val recipientChannel = "user:$recipientUserId"

            ablyService.publishMessage(recipientChannel, messageJson)
                .onSuccess {
                    println("NotificationsImpl: Successfully sent notification to $recipientChannel")
                }
                .onFailure { error ->
                    println("NotificationsImpl: Failed to send notification: ${error.message}")
                }
        } catch (e: Exception) {
            println("NotificationsImpl: Exception sending notification: ${e.message}")
            Result.failure(e)
        }
    }
}

/**
 * Data class representing a connection invite notification payload.
 */
@Serializable
data class ConnectionInviteNotification(
    val type: String,
    val senderName: String,
    val senderUserName: String,
    val message: String
)
