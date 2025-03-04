package com.mccartycarclub.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.domain.usecases.user.GetUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    user: GetUser,  // TODO: edit thesee names to use usecase
    userContacts: GetContacts,
) : ViewModel() {

    init {
        user.getUserGroups("31cb55f0-1031-7026-1ea5-9e5c424b27de")

        userContacts.fetchContacts("31cb55f0-1031-7026-1ea5-9e5c424b27de", userContacts = {
            println("MainViewModel ***** CONTACTS $it")
        })

        // user.getUsers()
    }
}