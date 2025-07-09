package com.cerqa.kotlin.viewmodels

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    fun setUserData(userId: String, userName: String) {
        println("MainViewModel ***** UID $userId UNAME $userName")
    }
}