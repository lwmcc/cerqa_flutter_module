package com.mccartycarclub.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.mccartycarclub.domain.model.LocalContact
import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.domain.usecases.user.GetUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    user: GetUser,  // TODO: edit thesee names to use usecase
    private val userContacts: GetContacts,
) : ViewModel() {

    private val _localContacts = MutableStateFlow(emptyList<LocalContact>())
    val localContacts = _localContacts.asStateFlow()

    init {
        user.getUser(TEST_USER_1,
            user = {
                //userContacts.createContact(it) //  Create contact for user
        })

//        user.getUserGroups(TEST_USER_1)
//
//        userContacts.fetchContacts(TEST_USER_1, userContacts = {
//            println("MainViewModel ***** CONTACTS $it")
//        })
//
        userContacts.getUserContacts(TEST_USER_1)
        // TODO: get all contacts
    }

    companion object {
        const val TEST_USER_1 = "14f8f4e8-a0b1-7015-d3b4-ded92d05abe5"
        const val TEST_USER_2 = "c468f4b8-3021-70f1-7f5b-2b7aef73da45"
    }

    fun getDeviceContacts() = userContacts.getDeviceContacts(localContacts = { contacts ->
        _localContacts.update { contacts }
    })


}