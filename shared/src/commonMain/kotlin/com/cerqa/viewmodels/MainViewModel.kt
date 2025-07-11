package com.cerqa.viewmodels

import androidx.lifecycle.ViewModel
import com.cerqa.data.Preferences

class MainViewModel : ViewModel() {

    val preferences = Preferences()
    fun setUserData(userId: String, userName: String) {
        preferences.setUserData(userId = userId, userName = userName)
    }

    fun getUserData() {
        val userData = preferences.getUserData()
        println("MainViewModel ***** USER ID ${userData.userId} NAME ${userData.userName}")
    }
}