package com.cerqa.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.cerqa.models.UserData

class StoreUserDefaults(private val preferences: SharedPreferences) : StoreDefaults {
    override suspend fun setUserData(
        userId: String,
        userName: String,
        createdAt: String,
        avatarUri: String
    ) {
        preferences.edit {
            putString("user-id", userId)
            putString("user-name", userName)
            putString("user-created-at", createdAt)
            putString("user-avatar-uri", avatarUri)
        }
    }

    override fun getUserData() = UserData(
        userId = preferences.getString("user-id", "-1"),
        userName = preferences.getString("user-name", "Name not found"),
        createdAt = preferences.getString("user-created-at", null),
        avatarUri = preferences.getString("user-avatar-uri", null),
    )
}