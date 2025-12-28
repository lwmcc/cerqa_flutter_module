package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.cerqa.graphql.StoreFcmTokenMutation

class NotificationRepositoryImpl(private val apolloClient: ApolloClient): NotificationRepository {

    override suspend fun storeFcmToken(userId: String, token: String, platform: String): Result<Boolean> {
        return try {
            println("NotificationRepository: Storing FCM token for userId: $userId, platform: $platform")
            println("NotificationRepository: Token: $token")

            val response = apolloClient.mutation(
                StoreFcmTokenMutation(
                    userId = userId,
                    token = token,
                    platform = platform
                )
            ).execute()

            if (response.hasErrors()) {
                val errorMessage = response.errors?.firstOrNull()?.message ?: "Unknown error"
                println("NotificationRepository: Error storing FCM token: $errorMessage")
                Result.failure(Exception(errorMessage))
            } else {
                val success = response.data?.storeFcmToken ?: false
                println("NotificationRepository: FCM token stored successfully: $success")
                Result.success(success)
            }
        } catch (e: Exception) {
            println("NotificationRepository: Exception storing FCM token: ${e.message}")
            Result.failure(e)
        }
    }
}