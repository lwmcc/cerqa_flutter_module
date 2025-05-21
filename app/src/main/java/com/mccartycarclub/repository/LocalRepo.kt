package com.mccartycarclub.repository

import com.mccartycarclub.domain.helpers.ContactsHelper
import com.mccartycarclub.domain.model.LocalContact
import kotlinx.coroutines.flow.Flow

interface LocalRepo {
    fun getAllContacts(localContacts: (List<LocalContact>) -> Unit)
    suspend fun setUserId(userId: String)
    fun getUserId(): Flow<String?>
}