package com.mccartycarclub.repository

import com.mccartycarclub.domain.helpers.ContactsHelper
import com.mccartycarclub.domain.model.LocalContact
import javax.inject.Inject

class Repo @Inject constructor(private val contactsHelper: ContactsHelper) : LocalRepo {
    override fun getAllContacts(localContacts: (List<LocalContact>) -> Unit) {
        contactsHelper.getAllContacts(localContacts = { contacts ->
            localContacts(contacts)
        })
    }
}