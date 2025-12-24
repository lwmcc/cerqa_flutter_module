package com.cerqa.notifications

/**
 * Interface for sending notifications across the app.
 */
interface Notifications {
    /**
     * Send a push notification when a connection invite is sent.
     *
     * @param recipientUserId The user ID of the recipient
     * @param senderName The name of the user sending the invite
     * @param senderUserName The username of the sender
     */
    suspend fun sendConnectionInviteNotification(
        recipientUserId: String,
        senderName: String,
        senderUserName: String
    ): Result<Unit>
}
