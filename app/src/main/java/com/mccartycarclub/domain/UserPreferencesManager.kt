package com.mccartycarclub.domain

import kotlinx.coroutines.flow.Flow

interface UserPreferencesManager {
    suspend fun setLocalUserId(userId: String)
    fun getUserId(): Flow<String?>
}
