package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.mccartycarclub.domain.model.LocalContact

interface GetContacts {
    fun fetchContacts(userId: String, userContacts: (List<UserContact>) -> Unit)

    // TODO: move to own interface
    fun createContact(user: User)
    fun getUserContacts(userId: String)
    fun getDeviceContacts(localContacts: (List<LocalContact>) -> Unit)
    fun addNewContact(userId: String, rowId: (String?) -> Unit)
    fun acceptContactInvite()
}
