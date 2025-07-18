package com.cerqa.data

import kotlinx.coroutines.CoroutineDispatcher

class FetchContactsRepository(
    private val defaults: StoreDefaults,
    private val ioDispatcher: CoroutineDispatcher
) : FetchContacts {
    override suspend fun fetchAllContacts() {

        println("FetchContactsRepository *****  TEST ${defaults.getUserData().userId}")
    }
}