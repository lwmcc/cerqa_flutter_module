package com.cerqa.viewmodels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ContactsViewModel {
    private val viewModelJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)
}