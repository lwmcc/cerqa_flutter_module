package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
 import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.LazyModelList
import com.amplifyframework.core.model.LoadedModelList
import com.amplifyframework.core.model.ModelReference
import com.amplifyframework.core.model.includes
import com.amplifyframework.core.model.query.predicate.QueryField
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.datastore.generated.model.UserPath
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

        val filter = Invite.SENDER.eq(senderUserId)
            .and(Invite.RECEIVER.eq(receiverUserId))

        val response = Amplify.API.query(ModelQuery.list(Invite::class.java, filter))
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
        // firstName and lastName are required when creating a record,
        // but here we just need the id because we're creating an invite
        val sender =
            User.builder().userId(DUMMY).firstName("").lastName(DUMMY)
            //User.builder().firstName(DUMMY).lastName(DUMMY).id(senderUserId)
                .build()
        // TODO: move these into function for reuse

        val receiver =
            User.builder().userId(receiverUserId).firstName(DUMMY).lastName(DUMMY)
            //User.builder().firstName(DUMMY).lastName(DUMMY).id(receiverUserId)
            //    .build()

        val invite = Invite
            .builder()
            .sender(senderUserId)
            .receiver(receiverUserId)
            .user(sender)
            .build()

        return try {
            val result = Amplify.API.mutate(ModelMutation.create(invite))
            if (result.hasData()) {
                println("AmplifyRepo ***** ${result.data}")
            } else {
                println("AmplifyRepo ***** NO DATA")
            }

            //Amplify.API.mutate(ModelMutation.create(invite)).data.id != null
            false
        } catch (e: ApiException) {
            // TODO: to log
            false
        }
    }

    override suspend fun cancelInviteToConnect(
        senderUserId: String?,
        receiverUserId: String
    ): Boolean {

/*        val sender = getInviteSender(receiverUserId)
        val receiver = getInviteReceiver(receiverUserId)

        if (sender == null && receiver == null) {
            return false
        }

        return try {
            val response = Amplify.API.mutate(
                ModelMutation.delete(
                    Invite
                        .builder()
                        .sender(sender)
                        .id(fetchInviteId(senderUserId, receiverUserId))
                        .receiver(receiver)
                        .build()
                )
            )
            response.data.id != null
        } catch (e: ApiException) {
            false
        }*/
        return false
    }

    private suspend fun fetchInviteId(senderUserId: String?, receiverUserId: String): String? {
        return try {
            Amplify.API.query(
                ModelQuery.list(
                    Invite::class.java, Invite.SENDER.eq(senderUserId)
                        .and(Invite.RECEIVER.eq(receiverUserId))
                )
            ).data.items.firstOrNull()?.id
        } catch (e: ApiException) {
            null
        }
    }

    override suspend fun fetchContacts(inviteReceiverUserId: String) {

    }

    override suspend fun createContact(user: User) {
        val response = Amplify.API.mutate(ModelMutation.create(user))
    }

    override suspend fun fetchReceivedInvites(receiverUserId: String) {

        try {
/*            val user = Amplify.API.query(
                ModelQuery.list(
                    User::class.java,
                    User.USER_ID.eq(receiverUserId)
                )
            )*/
            val response = Amplify.API.query(
                ModelQuery.list(
                    Invite::class.java,
                    Invite.RECEIVER.eq("216ba540-0011-70d0-bb72-5b51c19ae56a")
                )
            )


        } catch (e: Exception) {
            println("AmplifyRepo ***** ${e.message}")
        }



        /*val response = Amplify.API.query(
            ModelQuery.list(
                Invite::class.java,
                Invite.RECEIVER.eq(receiverUserId)
            )
        )*/






/*
        val test = Amplify.API.query(
            ModelQuery.get<User, UserPath>(
                User::class.java,
                User.UserIdentifier(receiverUserId)
            ) { userPath -> includes(userPath.sentInvites) }
        )

        //val invites = (test.data.receivedInvites as? LoadedModelList<Invite>)?.items
        //invites?.forEach {
            println("AmplifyRepo ***** MY ID  ${test.data}")
        //    it.id
        //}
*/








/*        val response = Amplify.API.query(ModelQuery.get(User::class.java, User.UserIdentifier(receiverUserId)))

        val invites =
            when (val inviteModels = response.data.receivedInvites) {
                is LazyModelList -> {
                    var page = inviteModels.fetchPage()
                    var loadedInvites = mutableListOf(page.items)
                    while (page.hasNextPage) {
                        val nextToken = page.nextToken
                        page = inviteModels.fetchPage(nextToken)
                        loadedInvites += page.items

                    }
                    loadedInvites
                }

                is LoadedModelList -> {

                }
            }*/


        //println("AmplifyRepo ***** INVITES ${invites.}")
    }



    private suspend fun fetchRowId(
        senderUserId: String?,
        receiverUserId: String,
    ): String? {

        val predicate = QueryField.field("senderUserId").eq(senderUserId)
            .and(QueryField.field("receiverUserId").eq(receiverUserId))

        val filter = Invite.SENDER.eq(senderUserId)
            .and(Invite.RECEIVER.eq(receiverUserId))

        val invites = Amplify.API.query(
            ModelQuery.list(Invite::class.java, filter)
        ).data

        println("Amplify ***** USER $senderUserId -- $receiverUserId")
        println("AmplifyRepo ***** INVITES DATA ${invites.toString()}")

        return ""
    }

    private fun getInviteSender(senderUserId: String) =
        //User.builder().id(senderUserId).firstName(DUMMY).lastName(DUMMY)
        //    .build()
        //User.builder().firstName(DUMMY).lastName(DUMMY).userId(senderUserId)


        User.builder().userId(senderUserId).firstName(DUMMY).lastName(DUMMY).build()

    private fun getInviteReceiver(receiverUserId: String) =

        User.builder().userId(receiverUserId).firstName(DUMMY).lastName(DUMMY).build()
        //User.builder().firstName(DUMMY).lastName(DUMMY).id(receiverUserId)
        //    .build()


    companion object {
        const val DUMMY = "dummy"
    }
}

class ResponseException(message: String) : Exception(message)