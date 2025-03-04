package com.mccartycarclub.repository

import com.amplifyframework.datastore.generated.model.Contact

interface DbRepo {
    fun fetchUserGroups(userId: String)
    fun fetchUserContacts(userId: String, userContacts: (List<Contact>) -> Unit) // TODO:: move to own interface
    fun fetchUsers()
}