package com.cerqa.data

import com.cerqa.models.UserData
import platform.Foundation.NSUserDefaults

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Preferences {

    val defaults = NSUserDefaults.standardUserDefaults

    actual fun setUserData(userName: String, userId: String) {
        defaults.setObject(userId, forKey = "user_id")
        defaults.setObject(userId, forKey = "user_name")
    }

    actual fun getUserData(): UserData {
        val userId = defaults.stringForKey("user_id").orEmpty()
        val userName = defaults.stringForKey("user_name").orEmpty()
        return UserData(userId = userId, userName = userName)
    }
}
