package com.mccartycarclub.repository

import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.ui.components.ConnectionAccepted
import kotlinx.coroutines.flow.Flow

interface RemoteRepo {
    fun contactExists(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean>

    fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean>

    fun fetchUserByUserName(userName: String): Flow<NetSearchResult<User?>>

    suspend fun sendInviteToConnect(
        senderUserId: String?,
        receiverUserId: String,
    ): Boolean

    suspend fun cancelInviteToConnect(
        senderUserId: String?,
        receiverUserId: String,
    ): Boolean

    suspend fun createContact(user: User)

    fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>>

    fun fetchSentInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>>

    suspend fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>>

    fun createContact(connectionAccepted: ConnectionAccepted): Flow<NetResult<String>>

    suspend fun fetchContacts(loggedInUserId: String): Flow<NetResult<List<Contact>>>
}