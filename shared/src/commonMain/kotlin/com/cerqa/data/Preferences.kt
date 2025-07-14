package com.cerqa.data

import com.cerqa.models.UserData

interface Preferences {
    suspend fun setUserData(userName: String, userId: String)
    fun getUserData(): UserData
}