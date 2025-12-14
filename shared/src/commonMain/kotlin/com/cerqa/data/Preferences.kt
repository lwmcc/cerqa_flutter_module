package com.cerqa.data

import com.cerqa.models.UserData

interface Preferences {
    suspend fun setUserData(
        userName: String,
        userId: String,
        userEmail: String,
        createdAt: String,
        avatarUri: String
    )

    fun getUserData(): UserData

    suspend fun clearUserData()
}