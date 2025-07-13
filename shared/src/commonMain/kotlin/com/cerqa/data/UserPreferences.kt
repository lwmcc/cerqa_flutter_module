package com.cerqa.data

import com.cerqa.models.UserData

class UserPreferences : Preferences {
    override fun setUserData(userName: String, userId: String) {
        println("UserPreferences ***** setUserData")
    }

    override fun getUserData(): UserData {
        println("getUserData ***** setUserData")
        return UserData("", "")
    }
}