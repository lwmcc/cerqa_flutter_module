package com.cerqa.data

import com.cerqa.models.UserData

class UserPreferences(val defaults: StoreDefaults) : Preferences {
    override suspend fun setUserData(
        userName: String,
        userId: String,
        createdAt: String,
        avatarUri: String
    ) {
        defaults.setUserData(
            userId = userId,
            userName = userName,
            createdAt = createdAt,
            avatarUri = avatarUri
        )
    }

    override fun getUserData(): UserData {
        return defaults.getUserData()
    }
}