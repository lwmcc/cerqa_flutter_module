package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.datastore.generated.model.Contact

interface GetContacts {
    fun fetchContacts(userId: String, userContacts: (List<Contact>) -> Unit)
}