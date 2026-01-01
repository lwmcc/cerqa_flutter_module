package com.cerqa.repository

interface NotificationRepository {
    suspend fun storeFcmToken(userId: String, token: String, platform: String): Result<Boolean>

    /**
     * Get count of unread notifications for a user
     */
    suspend fun getUnreadCount(userId: String): Result<Int>

    /**
     * Create a new notification
     */
    suspend fun createNotification(
        userId: String,
        type: String,
        title: String,
        message: String,
        relatedId: String? = null,
        senderUserId: String? = null,
        senderName: String? = null
    ): Result<String> // Returns notification ID

    /**
     * Mark a notification as read
     */
    suspend fun markAsRead(notificationId: String): Result<Boolean>

    /**
     * Mark all notifications as read for a user
     */
    suspend fun markAllAsRead(userId: String): Result<Boolean>
}