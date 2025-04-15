package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
 import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.LoadedModelList
import com.amplifyframework.core.model.includes
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Contact
import com.amplifyframework.datastore.generated.model.InviteToConnect
import com.amplifyframework.datastore.generated.model.InviteToConnectPath
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.datastore.generated.model.UserPath
import com.amplifyframework.kotlin.core.Amplify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AmplifyRepo @Inject constructor() : RemoteRepo {
    override suspend fun contactExists(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean> = flow {
        val filter = UserContact.USER.eq(senderUserId)
            .and(UserContact.CONTACT.eq(receiverUserId))

        val response = Amplify.API.query(ModelQuery.list(UserContact::class.java, filter))
        val count = response.data.items.count()
        emit(count > 0)
    }

    override suspend fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean> = flow {
        val filter = InviteToConnect.INVITES.eq(senderUserId)
            .and(InviteToConnect.RECEIVER_USER_ID.eq(receiverUserId))
        val response = Amplify.API.query(ModelQuery.list(InviteToConnect::class.java, filter))
        if (response.hasData()) {
            val count = response.data.items.count()
            emit(count > 0)
        } else { // TODO: need try catch
            emit(false)
        }
    }

    override suspend fun fetchUserByUserName(userName: String): Flow<NetResult<User?>> = flow {
        try {
            val response =
                Amplify.API.query(ModelQuery.list(User::class.java, User.USER_NAME.eq(userName)))
            if (response.hasData() && response.data.firstOrNull() != null) {
                emit(NetResult.Success(response.data.first()))
            } else {
                emit(NetResult.Error(ResponseException("No User Name Found")))
            }
        } catch (e: ApiException) {
            emit(NetResult.Error(e))
        }
    }

    override suspend fun sendInviteToConnect(
        senderUserId: String?,
        receiverUserId: String,
    ): Boolean {
        val invite = InviteToConnect.builder()
            .receiverUserId(receiverUserId)
            .invites(User.justId(senderUserId))
            .build()

        return try {
            val result = Amplify.API.mutate(ModelMutation.create(invite))
            result.hasData()
        } catch (error: ApiException) {
            false
        }
    }

    override suspend fun cancelInviteToConnect(
        senderUserId: String?,
        receiverUserId: String
    ): Boolean {

        val rowId =
            fetchRowId(senderUserId = senderUserId, receiverUserId = receiverUserId) ?: return false

        return try {
            val response = Amplify.API.query(ModelQuery[InviteToConnect::class.java, rowId])
            if (response.hasData()) {
                println("AmplifyRepo ***** DELETE SUCCESS ${response.data}")
                Amplify.API.mutate(ModelMutation.delete(response.data)).hasData()
            } else {
                println("AmplifyRepo ***** DELETE RESPONSE NO DATA ${response.data}")
                false
            }

        } catch (e: ApiException) {
            println("AmplifyRepo ***** ERROR ${e.message}")
            false
        }
    }

    override suspend fun fetchContacts(inviteReceiverUserId: String) {




    }



    private suspend fun fetchRowId(
        senderUserId: String?,
        receiverUserId: String,
    ): String? {
        val filter = InviteToConnect.INVITES.eq(senderUserId)
            .and(InviteToConnect.RECEIVER_USER_ID.eq(receiverUserId))

        return try {
            val response = Amplify.API.query(ModelQuery.list(InviteToConnect::class.java, filter))
            val rowId = response.data.items.firstOrNull()

            return if (rowId?.id != null) {
                return rowId.id
            } else {
                null
            }
        } catch (e: ApiException) {
            null
        }
    }
}

class ResponseException(message: String) : Exception(message)