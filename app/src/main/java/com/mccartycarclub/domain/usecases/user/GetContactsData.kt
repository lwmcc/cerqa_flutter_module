package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.datastore.generated.model.Contact
import com.mccartycarclub.repository.DbRepo
import javax.inject.Inject

class GetContactsData @Inject constructor(private val dbRepo: DbRepo) : GetContacts {
    override fun fetchContacts(userId: String, userContacts: (List<Contact>) -> Unit) {
        dbRepo.fetchUserContacts(
            userId, userContacts = {
                userContacts(it)
            }
        )
    }
}