package com.cerqa.data

import com.cerqa.models.UserData

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Preferences actual constructor() {
    actual fun setUserData(userName: String, userId: String) {
    }

    actual fun getUserData(): UserData {
        return UserData("", "")
    }
}