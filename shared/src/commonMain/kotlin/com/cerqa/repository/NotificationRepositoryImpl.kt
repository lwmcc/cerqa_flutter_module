package com.cerqa.repository

import com.apollographql.apollo.ApolloClient

/**
 * Notification repository implementation
 * TODO: Re-implement once notification endpoints are available in API
 */
class NotificationRepositoryImpl(private val apolloClient: ApolloClient): NotificationRepository {

    override suspend fun storeFcmToken(userId: String, token: String, platform: String): Result<Boolean> {
        // TODO: Implement when CreateFcmTokenMutation is available
        println("NotificationRepository: FCM token storage not implemented yet")
        return Result.success(true)
    }

    override suspend fun getUnreadCount(userId: String): Result<Int> {
        // TODO: Implement when GetUnreadNotificationCountQuery is available
        return Result.success(0)
    }

    override suspend fun createNotification(
        userId: String,
        type: String,
        title: String,
        message: String,
        relatedId: String?,
        senderUserId: String?,
        senderName: String?
    ): Result<String> {
        // TODO: Implement when CreateNotificationMutation is available
        println("NotificationRepository: Notification creation not implemented yet")
        return Result.success("stub-notification-id")
    }

    override suspend fun markAsRead(notificationId: String): Result<Boolean> {
        // TODO: Implement when UpdateNotificationMutation is available
        return Result.success(true)
    }

    override suspend fun markAllAsRead(userId: String): Result<Boolean> {
        // TODO: Implement when UpdateNotificationMutation is available
        return Result.success(true)
    }
}
