package com.cerqa.viewmodels

import com.cerqa.data.FetchContacts
import com.cerqa.data.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


data class UiState(
    val pending: Boolean = false,
    //val contacts: List<Contact> = emptyList<Contact>(),
    //val message: MessageTypes? = null,
)


/*
class ContactsViewModel(
    private val preferences: Preferences,
    private val fetchContacts: FetchContacts,
) {

    private val viewModelJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        scope.launch {
            fetchContacts.fetchAllContacts()
        }
    }

    fun onCleared() {

        viewModelJob.cancel()
    }

}*/
