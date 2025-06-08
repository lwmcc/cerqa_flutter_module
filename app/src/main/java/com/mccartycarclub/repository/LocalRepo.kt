package com.mccartycarclub.repository

import com.mccartycarclub.domain.model.LocalContact
import kotlinx.coroutines.flow.Flow

interface LocalRepo {
    fun getAllContacts(localContacts: (List<LocalContact>) -> Unit)
    suspend fun setLocalUserId(userId: String)
    fun getUserId(): Flow<String?>
}
