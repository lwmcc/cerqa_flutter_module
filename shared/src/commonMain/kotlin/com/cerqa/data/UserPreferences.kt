package com.cerqa.data

import com.cerqa.models.UserData

class UserPreferences(val defaults: StoreDefaults) : Preferences {
    override suspend fun setUserData(
        userName: String,
        userId: String,
        userEmail: String,
        createdAt: String,
        avatarUri: String
    ) {
        defaults.setUserData(
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            createdAt = createdAt,
            avatarUri = avatarUri
        )
    }

    override fun getUserData(): UserData {
        return defaults.getUserData()
    }

    override suspend fun clearUserData() {
        defaults.clearUserData()
    }
}