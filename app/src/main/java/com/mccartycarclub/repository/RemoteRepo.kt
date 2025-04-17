package com.mccartycarclub.repository

import com.amplifyframework.datastore.generated.model.User
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

    suspend fun fetchContacts(inviteReceiverUserId: String)

    suspend fun createContact(user: User)

    suspend fun fetchReceivedInvites(receiverUserId: String)
}