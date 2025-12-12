package com.cerqa.data

import com.cerqa.models.UserData
import platform.Foundation.NSUserDefaults

class StoreUserDefaults(private val defaults: NSUserDefaults) : StoreDefaults {
    override suspend fun setUserData(
        userId: String,
        userName: String,
        createdAt: String,
        avatarUri: String
    ) {
        defaults.setObject(value = userId, forKey = "user-id")
        defaults.setObject(value = userName, forKey = "user-name")
        defaults.setObject(value = createdAt, forKey = "user-created-at")
        defaults.setObject(value = avatarUri, forKey = "user-avatar-uri")
    }

    override fun getUserData(): UserData {
        return UserData(
            userId = defaults.stringForKey(defaultName = "user-id"),
            userName = defaults.stringForKey(defaultName = "user-name"),
            createdAt = defaults.stringForKey(defaultName = "user-created-at"),
            avatarUri = defaults.stringForKey(defaultName = "user-avatar-uri"),
        )
    }
}