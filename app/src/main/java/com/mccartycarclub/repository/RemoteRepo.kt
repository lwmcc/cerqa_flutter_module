package com.mccartycarclub.repository

import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.domain.model.ContactsSearchResult
import io.ably.lib.rest.Auth.TokenRequest
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

    fun hasExistingInviteToAcceptOrReject(
        loggedInUserId: String,
        receiverUserId: String,
    ): Flow<Boolean>

    fun fetchUserByUserName(userName: String): Flow<NetSearchResult<User?>>

    suspend fun sendInviteToConnect(
        senderUserId: String?,
        receiverUserId: String,
        rowId: String,
    ): Boolean

    fun cancelInviteToConnect(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<NetDeleteResult>

    suspend fun deleteContact(loggedInUserId: String, contactId: String): Flow<NetDeleteResult>

    fun deleteReceivedInviteToContact(
        loggedInUserId: String,
        contactId: String,
    ): Flow<NetDeleteResult>

    suspend fun createContact(user: User)

    fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>>

    fun fetchSentInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>>

    suspend fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>>

    fun createContact(senderUserId: String, receiverUserId: String): Flow<NetDeleteResult>

    suspend fun fetchContacts(loggedInUserId: String): Flow<NetResult<List<Contact>>>

    fun fetchAblyToken(userId: String): Flow<TokenRequest>

    suspend fun searchUsers(userName: String)
}