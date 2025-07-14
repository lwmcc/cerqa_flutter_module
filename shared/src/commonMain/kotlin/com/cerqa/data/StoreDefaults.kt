package com.cerqa.data

import com.cerqa.models.UserData

interface StoreDefaults {
    suspend fun setUserData(userId: String, userName: String)
    fun getUserData(): UserData
}