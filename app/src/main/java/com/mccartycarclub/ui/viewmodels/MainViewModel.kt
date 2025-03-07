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
        user.getUser(TEST_USER_1,
            user = {
                userContacts.createContact(it)
                println("MainViewModel ***** USER ${it.name}")
                println("MainViewModel ***** USER ${it.id}")
                println("MainViewModel ***** USER ${it.firstName}")
        })

        user.getUserGroups(TEST_USER_1)

        userContacts.fetchContacts(TEST_USER_1, userContacts = {
            println("MainViewModel ***** CONTACTS $it")
        })

        userContacts.getUserContacts()
    }

    companion object {
        const val TEST_USER_1 = "14f8f4e8-a0b1-7015-d3b4-ded92d05abe5"
        const val TEST_USER_2 = "c468f4b8-3021-70f1-7f5b-2b7aef73da45"
    }
}