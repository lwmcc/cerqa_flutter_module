package com.cerqa.viewmodels

import androidx.lifecycle.ViewModel
import com.cerqa.data.Preferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel(private val preferences: Preferences) : KoinComponent {

    fun setUserData(userId: String, userName: String) {
        preferences.setUserData(userId = userId, userName = userName)
    }

    fun getUserData() {
        val userData = preferences.getUserData()
    }
}