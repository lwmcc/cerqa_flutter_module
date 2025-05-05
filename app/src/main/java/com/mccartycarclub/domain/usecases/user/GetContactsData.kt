package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.mccartycarclub.domain.model.LocalContact
import com.mccartycarclub.repository.DbRepo
import com.mccartycarclub.repository.LocalRepo
import javax.inject.Inject

class GetContactsData @Inject constructor(
    private val dbRepo: DbRepo,
    private val localRepo: LocalRepo,
    ) : GetContacts {
    override fun fetchContacts(userId: String, userContacts: (List<UserContact>) -> Unit) {
        dbRepo.fetchUserContacts(
            userId, userContacts = {
                //userContacts(it)
            }
        )
    }

    override fun createContact(user: User) {
        dbRepo.createContact(user)
    }

    override fun getUserContacts(userId: String) {
        dbRepo.fetchUserContacts(userId)
    }

    override fun getDeviceContacts(localContacts: (List<LocalContact>) -> Unit) {
        localRepo.getAllContacts(localContacts = { contacts ->
            localContacts(contacts)
        })
    }

    override fun addNewContact(userId: String, rowId: (String?) -> Unit) {
        dbRepo.acceptContactInvite(userId, rowId = {
            rowId(it)
        })
    }

    override fun acceptContactInvite() {
        dbRepo.updateSenderReceiverContacts()
    }
}