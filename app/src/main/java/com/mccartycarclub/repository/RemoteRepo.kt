package com.mccartycarclub.repository

import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.ui.components.ConnectionAccepted
import kotlinx.coroutines.flow.Flow

interface RemoteRepo {
    suspend fun contactExists(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean>

    suspend fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean>

    suspend fun fetchUserByUserName(userName: String): Flow<NetResult<User?>>

    suspend fun sendInviteToConnect(
        senderUserId: String?,
        receiverUserId: String,
    ): Boolean

    suspend fun cancelInviteToConnect(
        senderUserId: String?,
        receiverUserId: String,
    ): Boolean

    // suspend fun fetchContacts(userId: String): Flow<GraphQLResponse<PaginatedResult<User>>>

    suspend fun createContact(user: User)

    suspend fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>>

    suspend fun fetchSentInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>>

    suspend fun createContact(connectionAccepted: ConnectionAccepted): Flow<NetResult<String>>

    suspend fun fetchContacts(loggedInUserId: String)
}