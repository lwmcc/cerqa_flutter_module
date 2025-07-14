package com.cerqa.viewmodels

import com.cerqa.data.FetchContacts
import com.cerqa.data.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class UiState(
    val pending: Boolean = false,
    //val contacts: List<Contact> = emptyList<Contact>(),
    //val message: MessageTypes? = null,
)


class ContactsViewModel(
    private val preferences: Preferences,
    private val fetchContacts: FetchContacts,
) {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        val userData = preferences.getUserData()
        _userId.value = userData.userId
    }

}