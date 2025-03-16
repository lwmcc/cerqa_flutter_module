package com.mccartycarclub.repository

import com.mccartycarclub.domain.helpers.ContactsHelper
import com.mccartycarclub.domain.model.LocalContact

interface LocalRepo {
    fun getAllContacts(localContacts: (List<LocalContact>) -> Unit)
}