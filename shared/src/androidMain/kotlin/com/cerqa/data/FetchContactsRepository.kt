package com.cerqa.data

import com.amplifyframework.api.ApiCategoryBehavior
import kotlinx.coroutines.CoroutineDispatcher

class FetchContactsRepository(
    private val defaults: StoreDefaults,
    private val amplifyApi: ApiCategoryBehavior,
    private val ioDispatcher: CoroutineDispatcher
) : FetchContacts {
    override suspend fun fetchAllContacts() {

        println("FetchContactsRepository *****  TEST ${defaults.getUserData().userId}")
    }
}