package com.cerqa.viewmodels

import com.cerqa.data.Preferences

class MainViewModel(private val defaults: Preferences) {

    fun setUserData(userId: String, userName: String) {
        defaults.setUserData(userId = userId, userName = userName)
    }

    fun getUserData() {
        defaults.getUserData()
    }
}