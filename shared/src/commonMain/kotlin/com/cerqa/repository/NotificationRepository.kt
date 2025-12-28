package com.cerqa.repository

interface NotificationRepository {
    suspend fun storeFcmToken(userId: String, token: String, platform: String): Result<Boolean>
}