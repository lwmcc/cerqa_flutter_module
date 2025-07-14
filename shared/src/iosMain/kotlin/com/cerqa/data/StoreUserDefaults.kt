package com.cerqa.data

import com.cerqa.models.UserData
import platform.Foundation.NSUserDefaults

class StoreUserDefaults(private val defaults: NSUserDefaults) : StoreDefaults {
    override fun setUserData(userId: String, userName: String) {
        defaults.setObject(value = userId, forKey = "user-id")
        defaults.setObject(value = userName, forKey = "user-name")
    }

    override fun getUserData(): UserData {
        return UserData(
            userId =  defaults.stringForKey(defaultName = "user-id"),
            userName = defaults.stringForKey(defaultName = "user-name"),
        )
    }
}