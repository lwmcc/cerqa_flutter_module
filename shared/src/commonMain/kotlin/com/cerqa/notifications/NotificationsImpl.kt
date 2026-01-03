package com.cerqa.notifications

import com.apollographql.apollo.ApolloClient
import kotlinx.serialization.Serializable

/**
 * Implementation of Notifications using FCM (Firebase Cloud Messaging).
 * TODO: Re-implement once SendInviteNotificationMutation is available in API
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
        // TODO: Implement when SendInviteNotificationMutation is available
        println("NotificationsImpl: Notification sending not implemented yet")
        return Result.success(Unit)
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
