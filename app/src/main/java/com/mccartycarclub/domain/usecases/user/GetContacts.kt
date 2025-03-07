package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.datastore.generated.model.Contact
import com.amplifyframework.datastore.generated.model.User

interface GetContacts {
    fun fetchContacts(userId: String, userContacts: (List<Contact>) -> Unit)

    // TODO: move to own interface
    fun createContact(user: User)
    fun getUserContacts()
}