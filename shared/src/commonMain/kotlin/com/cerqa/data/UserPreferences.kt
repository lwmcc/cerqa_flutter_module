package com.cerqa.data

import com.cerqa.models.UserData

class UserPreferences(val defaults: StoreDefaults) : Preferences {
    override suspend fun setUserData(userName: String, userId: String) {
        defaults.setUserData(userId = userId, userName = userName)
    }

    override fun getUserData(): UserData {
        return defaults.getUserData()
    }
}