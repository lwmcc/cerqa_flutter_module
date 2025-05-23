package com.mccartycarclub.repository

import com.mccartycarclub.domain.UserPreferencesManager
import com.mccartycarclub.domain.helpers.ContactsHelper
import com.mccartycarclub.domain.model.LocalContact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repo @Inject constructor(
    private val contactsHelper: ContactsHelper, // TODO: change name add manager?
    private val userPreferencesManager: UserPreferencesManager,
) : LocalRepo {
    override fun getAllContacts(localContacts: (List<LocalContact>) -> Unit) {
        contactsHelper.getAllContacts(localContacts = { contacts ->
            localContacts(contacts)
        })
    }

    override suspend fun setLocalUserId(userId: String) {
        userPreferencesManager.setLocalUserId(userId)
    }

    override fun getUserId(): Flow<String?> = userPreferencesManager.getUserId()

}