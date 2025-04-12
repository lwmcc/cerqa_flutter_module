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
        println("AmplifyRepo ***** ${response.hasData()}")
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
            println("AmplifyRepo ***** ${result.data}")
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
            .invites(User.justId(senderUserId))
            .build()

        val filter = InviteToConnect.INVITES.eq(senderUserId)
            .and(InviteToConnect.RECEIVER_USER_ID.eq(receiverUserId))
        val response = Amplify.API.query(ModelQuery.list(InviteToConnect::class.java, filter))

        println("AmplifyRepo ***** ${response.data}")
        return true
    }

}