package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
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
        val count = response.data.items.count()
        emit(count > 0)
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
            Amplify.API.mutate(ModelMutation.create(invite)).hasData()
        } catch (error: ApiException) {
            false
        }
    }

    override suspend fun cancelInviteToConnect(
        senderUserId: String?,
        receiverUserId: String
    ): Boolean {

        /*val invite = InviteToConnect.builder()
            .receiverUserId(receiverUserId)
            .invites(User.justId(senderUserId))
            .build()

        val response = Amplify.API.mutate(ModelMutation.delete(invite))
        println("AmplifyRepo ***** RESPONSE ${response.hasData()}")*/

        val predicate = InviteToConnect.RECEIVER_USER_ID.contains(receiverUserId)
            .and(InviteToConnect.INVITES.eq(User.justId(senderUserId)))

        val response = Amplify.API.query(
            ModelQuery.list(InviteToConnect::class.java, predicate))

        println("AmplifyRepo ***** RESPONSE ${response.hasData()} --  ${response}")

/*        val filter = InviteToConnect.ID.eq(senderUserId)
            .and(InviteToConnect.RECEIVER_USER_ID.eq(receiverUserId))

        val query = InviteToConnect.RECEIVER_USER_ID .eq(receiverUserId)
            .and(InviteToConnect.INVITES.eq(User.justId(senderUserId)))

        val response = Amplify.API.query(
            ModelQuery.list(InviteToConnect::class.java, filter))*/



        return true
    }
}