package com.cerqa.data

import com.cerqa.models.UserData

interface StoreDefaults {
    suspend fun setUserData(
        userId: String,
        userName: String,
        createdAt: String,
        avatarUri: String
    )
    fun getUserData(): UserData
}