package com.cerqa.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.cerqa.models.UserData

class StoreUserDefaults(private val preferences: SharedPreferences) : StoreDefaults {
    override fun setUserData(userId: String, userName: String) {
        preferences.edit {
            putString("user-id", userId)
            putString("user-name", userName)
        }
    }

    override fun getUserData() = UserData(
        userId = preferences.getString("user-id", "ID not found"),
        userName = preferences.getString("user-name", "Name not found"),
    )
}