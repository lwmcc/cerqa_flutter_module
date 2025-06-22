package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.mccartycarclub.repository.DbRepository
import com.mccartycarclub.repository.LocalRepository
import javax.inject.Inject

class GetContactsData @Inject constructor(
    private val dbRepo: DbRepository,
    private val localRepo: LocalRepository,
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

    // TODO: remove
/*
    override fun getDeviceContacts(localContacts: (List<LocalContact>) -> Unit): List<LocalContact> {
        val contacts = localRepo.getAllContacts(localContacts = { contacts ->
            localContacts(contacts)
        })
        return contacts
    }
*/

    override fun addNewContact(userId: String, rowId: (String?) -> Unit) {
        dbRepo.acceptContactInvite(userId, rowId = {
            rowId(it)
        })
    }

    override fun acceptContactInvite() {
        dbRepo.updateSenderReceiverContacts()
    }
}
