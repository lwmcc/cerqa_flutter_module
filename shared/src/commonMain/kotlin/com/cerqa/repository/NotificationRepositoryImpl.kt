package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.cerqa.graphql.CreateNotificationMutation
import com.cerqa.graphql.GetUnreadNotificationCountQuery
import com.cerqa.graphql.CreateFcmTokenMutation
import com.cerqa.graphql.UpdateNotificationMutation
import com.cerqa.graphql.type.CreateNotificationInput
import com.cerqa.graphql.type.CreateFcmTokenInput
import com.cerqa.graphql.type.UpdateNotificationInput

class NotificationRepositoryImpl(private val apolloClient: ApolloClient): NotificationRepository {

    override suspend fun storeFcmToken(userId: String, token: String, platform: String): Result<Boolean> {
        return try {
            println("NotificationRepository: Storing FCM token for userId: $userId, platform: $platform")
            println("NotificationRepository: Token: $token")

            // Generate a deviceId by combining userId and platform
            // This ensures the same device gets the same ID
            val deviceId = "${userId}_${platform}".hashCode().toString()

            val input = CreateFcmTokenInput(
                userId = userId,
                deviceId = deviceId,
                token = token,
                platform = platform
            )

            val response = apolloClient.mutation(
                CreateFcmTokenMutation(input = input)
            ).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                println("NotificationRepository: Error storing FCM token: $errorMessage")
                Result.failure(Exception(errorMessage))
            } else {
                val success = response.data?.createFcmToken != null
                println("NotificationRepository: FCM token stored successfully: $success")
                Result.success(success)
            }
        } catch (e: Exception) {
            println("NotificationRepository: Exception storing FCM token: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getUnreadCount(userId: String): Result<Int> {
        return try {
            println("NotificationRepository: Getting unread count for userId: $userId")

            val response = apolloClient.query(
                GetUnreadNotificationCountQuery(userId = userId)
            ).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                println("NotificationRepository: Error getting unread count: $errorMessage")
                Result.failure(Exception(errorMessage))
            } else {
                val count = response.data?.listNotifications?.items?.size ?: 0
                println("NotificationRepository: Unread count: $count")
                Result.success(count)
            }
        } catch (e: Exception) {
            println("NotificationRepository: Exception getting unread count: ${e.message}")
            Result.failure(e)
        }
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
        return try {
            println("NotificationRepository: Creating notification for userId: $userId, type: $type")

            val input = CreateNotificationInput(
                userId = userId,
                type = type,
                title = title,
                message = message,
                isRead = Optional.presentIfNotNull(false),
                relatedId = Optional.presentIfNotNull(relatedId),
                senderUserId = Optional.presentIfNotNull(senderUserId),
                senderName = Optional.presentIfNotNull(senderName)
            )

            val response = apolloClient.mutation(
                CreateNotificationMutation(input = input)
            ).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                println("NotificationRepository: Error creating notification: $errorMessage")
                Result.failure(Exception(errorMessage))
            } else {
                val notificationId = response.data?.createNotification?.id ?: ""
                println("NotificationRepository: Notification created with ID: $notificationId")
                Result.success(notificationId)
            }
        } catch (e: Exception) {
            println("NotificationRepository: Exception creating notification: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(notificationId: String): Result<Boolean> {
        return try {
            println("NotificationRepository: Marking notification as read: $notificationId")

            val input = UpdateNotificationInput(
                id = notificationId,
                isRead = Optional.present(true)
            )

            val response = apolloClient.mutation(
                UpdateNotificationMutation(input = input)
            ).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                println("NotificationRepository: Error marking as read: $errorMessage")
                Result.failure(Exception(errorMessage))
            } else {
                println("NotificationRepository: Notification marked as read successfully")
                Result.success(true)
            }
        } catch (e: Exception) {
            println("NotificationRepository: Exception marking as read: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun markAllAsRead(userId: String): Result<Boolean> {
        // TODO: Implement batch update - for now, we'll need to fetch and update individually
        println("NotificationRepository: markAllAsRead not yet implemented for userId: $userId")
        return Result.success(false)
    }
}