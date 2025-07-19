package com.mccartycarclub.repository

import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.domain.model.UserSearchResult
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

    fun sendInviteToConnect(
        receiverUserId: String,
        rowId: String,
    ): Flow<NetworkResponse<String>> // TODO: use object?

    fun sendPhoneNumberInviteToConnect(phoneNumber: String, ): Flow<NetworkResponse<String>>

    fun cancelInviteToConnect(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<NetDeleteResult>

    fun deleteContact(loggedInUserId: String, contactId: String): Flow<NetDeleteResult>

    fun deleteReceivedInviteToContact(
        loggedInUserId: String,
        contactId: String,
    ): Flow<NetDeleteResult>

    suspend fun createContact(user: User)

    fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>>

    fun fetchSentInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>>

    // TODO: will be in combined repo
    fun fetchAllContacts(): Flow<NetworkResponse<List<Contact>>>

    fun createContact(senderUserId: String, loggedInUserId: String): Flow<NetDeleteResult>

    fun fetchAblyToken(userId: String): Flow<TokenRequest>

    fun searchUsers(
        loggedInUserId: String?,
        userName: String
    ): Flow<NetworkResponse<UserSearchResult>>

    suspend fun searchUsersByUserName(userName: String): Flow<NetworkResponse<List<User>>>
}
