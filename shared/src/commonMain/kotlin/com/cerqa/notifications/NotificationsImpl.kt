package com.cerqa.notifications

import com.apollographql.apollo.ApolloClient
import com.cerqa.graphql.SendInviteNotificationMutation
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Implementation of Notifications using FCM (Firebase Cloud Messaging).
 * Sends push notifications via AWS Lambda function that triggers FCM on the recipient's device.
 */
class NotificationsImpl(
    private val apolloClient: ApolloClient
) : Notifications {

    override suspend fun sendConnectionInviteNotification(
        recipientUserId: String,
        senderName: String,
        senderUserName: String,
        inviteId: String
    ): Result<Unit> {
        return try {
            println("NotificationsImpl: Sending FCM notification to user: $recipientUserId")
            println("NotificationsImpl: Sender: $senderName (@$senderUserName), InviteId: $inviteId")

            val response = apolloClient.mutation(
                SendInviteNotificationMutation(
                    recipientUserId = recipientUserId,
                    senderName = senderName,
                    inviteId = inviteId
                )
            ).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                println("NotificationsImpl: Error sending notification: $errorMessage")
                return Result.failure(Exception(errorMessage))
            }

            val result = response.data?.sendInviteNotification
            if (result?.success == true) {
                println("NotificationsImpl: Successfully sent notification - ${result.message}")
                Result.success(Unit)
            } else {
                val message = result?.message ?: "Failed to send notification"
                println("NotificationsImpl: Notification failed - $message")
                Result.failure(Exception(message))
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
