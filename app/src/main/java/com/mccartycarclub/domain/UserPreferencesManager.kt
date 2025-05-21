package com.mccartycarclub.domain

import kotlinx.coroutines.flow.Flow

interface UserPreferencesManager {
    suspend fun setUserId(userId: String)
    fun getUserId(): Flow<String?>
}