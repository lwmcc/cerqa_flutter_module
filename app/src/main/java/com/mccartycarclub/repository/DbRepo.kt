package com.mccartycarclub.repository

import com.amplifyframework.datastore.generated.model.Contact
import com.amplifyframework.datastore.generated.model.User

interface DbRepo {
    fun fetchUserGroups(userId: String)
    fun fetchUserContacts(userId: String, userContacts: (List<Contact>) -> Unit) // TODO:: move to own interface
    fun fetchUsers()
    fun fetchUser(userId: String,  user: (User) -> Unit)
    fun createContact(user: User)
    fun fetchUserContacts()
}