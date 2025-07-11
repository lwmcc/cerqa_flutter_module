package com.cerqa.data

import com.cerqa.models.UserData

expect class Preferences() {
    fun setUserData(userName: String, userId: String)
    fun getUserData(): UserData
}