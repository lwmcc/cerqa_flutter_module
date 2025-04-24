package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.LazyModelList
import com.amplifyframework.core.model.LazyModelReference
import com.amplifyframework.core.model.LoadedModelList
import com.amplifyframework.core.model.LoadedModelReference
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.predicate.QueryField
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.core.model.query.predicate.QueryPredicateGroup
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.amplifyframework.kotlin.core.Amplify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject
import com.amplifyframework.datastore.generated.model.Contact as AmplifyContact

class AmplifyRepo @Inject constructor(private val amplifyApi: KotlinApiFacade) : RemoteRepo {

    sealed class ContactType() {
        data class Received(val type: String) : ContactType()
        data class Sent(val type: String) : ContactType()
        data class Current(val type: String) : ContactType()
    }

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
                .build()

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

    // TODO make private
    override suspend fun fetchContacts(loggedInUserId: String):
            Flow<GraphQLResponse<PaginatedResult<User>>> = flow {
        val response =
            Amplify.API.query(ModelQuery.list(User::class.java, User.USER_ID.eq(loggedInUserId)))

        response.data.forEach {
            println("AmplifyRepo ***** fetchContacts CONTACTS ${it.name}")
            println("AmplifyRepo ***** fetchContacts CONTACTS ${it.userName}")
        }

    }

    override suspend fun createContact(user: User) {
        val response = Amplify.API.mutate(ModelMutation.create(user))
    }

    override suspend fun fetchSentInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val senderResponse = sentInvites(loggedInUserId)
            emit(fetchInvites(senderResponse))
        }

    override suspend fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {

            // TODO: and return flow
            // TODO: set up so that no invites does not cause crash
            val senderResponse = sentInvites(loggedInUserId)
            fetchInvites(senderResponse)

            // TODO: change name receivedInvites
            val receiverResponse = receivedInvites(loggedInUserId)
            emit(fetchInvites(receiverResponse))
        }

    override suspend fun createContact(
        senderUserId: String,
        receiverUserId: String
    ): Flow<NetResult<String>> = flow {
        emit(NetResult.Pending)
        val sender = User.justId(senderUserId)
        val receiver = User.justId(receiverUserId)

        val senderContact = AmplifyContact.builder()
            .contactId(receiverUserId)
            .id(senderUserId)
            .build()

        val receiverContact = AmplifyContact.builder()
            .contactId(senderUserId)
            .id(receiverUserId)
            .build()

        try {
            val senderPutResult = putSenderContact(senderContact)
            val receiverPutResult = putReceiverContact(receiverContact)
            if (!senderPutResult.hasData() || !receiverPutResult.hasData()) {
                emit(NetResult.Error(ResponseException("")))
                return@flow
            }

            val putSenderSuccess = putSenderUserContact(
                ModelMutation.create(
                    buildSenderUserContact(
                        receiver,
                        senderContact
                    )
                )
            )

            val putReceiverSuccess = putReceiverUserContact(
                ModelMutation.create(
                    buildReceiverUserContact(
                        sender,
                        receiverContact
                    )
                )
            )

            if (!putSenderSuccess || !putReceiverSuccess) {
                emit(NetResult.Error(ResponseException("")))
                return@flow
            }

            val predicate = QueryField.field(SENDER).eq(senderUserId)
                .and(QueryField.field(RECEIVER).eq(receiverUserId))

            val invites = fetchInviteList(Invite::class.java, predicate)

            if (invites == null) {
                emit(NetResult.Error(ResponseException("")))
                return@flow
            }

            deleteInvite(invites)
            emit(NetResult.Success("send message"))

        } catch (e: ApiException) {
            emit(NetResult.Error(e))
        } catch (ex: Exception) {
            emit(NetResult.Error(ex))
        }
    }

    private suspend fun sentInvites(loggedInUserId: String): Set<String> {
        val set = mutableSetOf<String>()
        val senderResponse = Amplify.API.query(
            ModelQuery.list(
                Invite::class.java,
                Invite.SENDER.eq(loggedInUserId)
            )
        )

        if (senderResponse.hasData()) {
            senderResponse.data.items.forEach { item ->
                println("AmplifyRepo ***** WHO RECEIVED ${item.receiver}")
            }
        }

        return set
    }

    private suspend fun receivedInvites(loggedInUserId: String): Set<String> {

        val set = mutableSetOf<String>()

        return try {
            val receiverResponse = Amplify.API.query(
                ModelQuery.list(
                    Invite::class.java,
                    Invite.RECEIVER.eq(loggedInUserId)
                )
            )
            receiverResponse.data.items.forEach { item ->
                set.add(item.sender)
            }
            set
        } catch (e: ApiException) {
            emptySet()
        }
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
        User.builder().userId(senderUserId).firstName(DUMMY).lastName(DUMMY).build()

    private fun getInviteReceiver(receiverUserId: String) =
        User.builder().userId(receiverUserId).firstName(DUMMY).lastName(DUMMY).build()

    // TODO: change param, change function name
    private suspend fun fetchInvites(connectionInvites: Set<String>): NetWorkResult<List<Contact>> {

        if (connectionInvites.isEmpty()) {
            return NetWorkResult.Success(emptyList())
        }

        val invites = mutableListOf<Contact>()

        try {
            val predicate = createQueryPredicate(connectionInvites)
            val response = amplifyApi.query(ModelQuery.list(User::class.java, predicate))

            invites.addAll(UserMapper.toUserList(response))

            return NetWorkResult.Success(invites)
        } catch (e: ApiException) {
            return NetWorkResult.Error(e)
        }
    }

    private fun createQueryPredicate(ids: Set<String>) = ids
        .map { User.USER_ID.eq(it) as QueryPredicate }
        .reduce { acc, value -> acc.or(value) }

    private fun createInvite(inviteRowId: String) = Invite
        .builder()
        .sender(DUMMY)
        .receiver(DUMMY)
        .id(inviteRowId)
        .build()

    private fun buildSenderUserContact(receiver: User, senderContact: AmplifyContact) =
        UserContact.builder()
            .user(receiver)
            .contact(senderContact)
            .build()

    private fun buildReceiverUserContact(sender: User, receiverContact: AmplifyContact) =
        UserContact.builder()
            .user(sender)
            .contact(receiverContact)
            .build()

    private suspend fun putSenderContact(senderContact: AmplifyContact) =
        amplifyApi.mutate(ModelMutation.create(senderContact))

    private suspend fun putReceiverContact(receiverContact: AmplifyContact) =
        amplifyApi.mutate(ModelMutation.create(receiverContact))

    private suspend fun deleteInvite(inviteRowId: Invite) =
        amplifyApi.mutate(ModelMutation.delete(createInvite(inviteRowId.id)))

    private suspend fun putSenderUserContact(contact: GraphQLRequest<UserContact>) =
        amplifyApi.mutate(contact).hasData()

    private suspend fun putReceiverUserContact(contact: GraphQLRequest<UserContact>) =
        amplifyApi.mutate(contact).hasData()

    private suspend fun <T : Model> fetchInviteList(
        modelClass: Class<T>,
        predicate: QueryPredicateGroup,
    ) = amplifyApi.query(ModelQuery.list(modelClass, predicate)).data.items.firstOrNull()

    override suspend fun myTest(loggedInUserId: String) {

        val response = amplifyApi.query(
            ModelQuery[User::class.java, User.UserIdentifier(loggedInUserId)],
        )

        when (val userContacts = response.data.contacts) {
            is LoadedModelList -> {

            }

            is LazyModelList -> {

                val allContacts = mutableListOf<UserContact>()
                var page = userContacts.fetchPage()

                allContacts.addAll(page.items)

                while (page.hasNextPage) {
                    val nextPage = userContacts.fetchPage(page.nextToken)
                    allContacts.addAll(nextPage.items)
                    page = nextPage
                }



                allContacts.forEach { userContact ->

                    val contactUser = (userContact.contact as LazyModelReference).fetchModel()
                    println("AmplifyRepo ***** ${contactUser.}")

                    when (val contactRef = userContact.contact) {
                        is LazyModelReference -> {

                            val contact = contactRef.fetchModel() as User
                            println("AmplifyRepo ***** USER ${contact.userName}")


                        }
                        is LoadedModelReference -> {

                        }
                    }
                }
            }
        }
    }

    companion object {
        const val DUMMY = "dummy"
        const val SENDER = "sender"
        const val RECEIVER = "receiver"
    }
}

class ResponseException(message: String) : Exception(message)

open class Contact(
    val rowId: String,
    val userId: String,
    val userName: String,
    val name: String,
    val avatarUri: String,
)

class ReceivedContactInvite(
    val receiverUserId: String,
    val receivedDate: Date, // TODO: use correct type
    rowId: String, userId: String, userName: String, name: String, avatarUri: String,
) : Contact(rowId, userId, userName, name, avatarUri)

class SentContactInvite(
    val senderUserId: String,
    val sentDate: Date,
    rowId: String, userId: String, userName: String, name: String, avatarUri: String,
) : Contact(rowId, userId, userName, name, avatarUri)

class CurrentContact(
    val senderUserId: String,
    rowId: String, userId: String, userName: String, name: String, avatarUri: String,
) : Contact(rowId, userId, userName, name, avatarUri)