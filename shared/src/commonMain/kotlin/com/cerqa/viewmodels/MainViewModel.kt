package com.cerqa.viewmodels

import androidx.lifecycle.viewmodel.compose.viewModel
import com.cerqa.data.FetchContacts
import com.cerqa.data.Preferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainViewModel(
    private val preferences: Preferences,
    private val fetchContacts: FetchContacts,
    private val mainDispatcher: CoroutineDispatcher,
) {
    // TODO: +Job ?
    private val viewModelScope = CoroutineScope(mainDispatcher) //
    fun setUserData(userId: String, userName: String) {
        viewModelScope.launch {
            preferences.setUserData(userId = userId, userName = userName)
        }
    }

    fun getUserData() {
        preferences.getUserData()
    }
}