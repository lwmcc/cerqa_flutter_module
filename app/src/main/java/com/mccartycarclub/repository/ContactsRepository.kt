package com.mccartycarclub.repository

import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.domain.model.DeviceContact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun createContact(senderUserId: String, loggedInUserId: String): Flow<NetDeleteResult>
    fun contactExists(senderUserId: String, receiverUserId: String): Flow<Boolean>
    fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>>

    suspend fun combineDeviceAppUserContacts(): List<DeviceContact>
    suspend fun fetchUsersByPhoneNumber()
    suspend fun createContact(user: User)
}