package com.cerqa.data

import kotlinx.coroutines.CoroutineDispatcher

class FetchContactsRepository(private val ioDispatcher: CoroutineDispatcher) : FetchContacts {
    override fun fetchAllContacts() {

    }
}