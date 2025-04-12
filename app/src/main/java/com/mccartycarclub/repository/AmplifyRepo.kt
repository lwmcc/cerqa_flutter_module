package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.datastore.generated.model.InviteToConnect
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.kotlin.core.Amplify
import kotlinx.coroutines.flow.Flow
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
            if (response.hasData()) {
                val data = response.data.firstOrNull()
                emit(NetResult.Success(data))
            } else {
                emit(NetResult.Success(null))
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

        val invite = InviteToConnect.builder()
            .receiverUserId(receiverUserId)
            .build()

        val rowId = fetchRowId(
            senderUserId = senderUserId,
            receiverUserId = receiverUserId,
        )

        if (rowId != null) {
            try {

                val response = Amplify.API.query(ModelQuery.get(InviteToConnect::class.java, rowId))

                if (response.hasData()) {
                    val delete = Amplify.API.mutate(ModelMutation.delete(response.data))
                    if (delete.hasData()) {
                        println("AmplifyRepo ***** DELETE ${delete.data}")
                    } else {
                        println("AmplifyRepo ***** DELETE NOT DATA")
                    }
                } else {
                    println("AmplifyRepo ***** DELETE RESPONSE NO DATA ${response.data}")
                }


            } catch (e: ApiException) {
                println("AmplifyRepo ***** ERROR ${e.message}")
            }
        }

        return true
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