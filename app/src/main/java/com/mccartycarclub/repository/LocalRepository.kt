package com.mccartycarclub.repository

import com.mccartycarclub.domain.helpers.LocalDeviceContacts
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun getUserId(): Flow<String?>

    suspend fun getAllContacts() : List<LocalDeviceContacts>
    suspend fun setLocalUserId(userId: String)
}
