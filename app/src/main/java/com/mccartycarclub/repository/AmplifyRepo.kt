package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.LazyModelReference
import com.amplifyframework.core.model.LoadedModelReference
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.ModelReference
import com.amplifyframework.core.model.query.predicate.QueryField
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.core.model.query.predicate.QueryPredicateGroup
import com.amplifyframework.core.model.query.predicate.QueryPredicateOperation
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact

import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.mccartycarclub.ui.components.ConnectionAccepted
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date
import javax.inject.Inject
import javax.inject.Named
import com.amplifyframework.datastore.generated.model.Contact as AmplifyContact

class AmplifyRepo @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : RemoteRepo {

    sealed class ContactType() {
        data class Received(val type: String) : ContactType()
        data class Sent(val type: String) : ContactType()
        data class Current(val type: String) : ContactType()
    }

    override fun contactExists(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean> = flow {
        val filter = UserContact.USER.eq(senderUserId)
            .and(UserContact.CONTACT.eq(receiverUserId))

        val response = amplifyApi.query(ModelQuery.list(UserContact::class.java, filter))
        val count = response.data.items.count()
        emit(count > 0)
    }

    override fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean> = flow {

        val filter = Invite.SENDER.eq(senderUserId)
            .and(Invite.RECEIVER.eq(receiverUserId))

        val response = amplifyApi.query(ModelQuery.list(Invite::class.java, filter))
        if (response.hasData()) {
            val count = response.data.items.count()
            emit(count > 0)
        } else { // TODO: need try catch
            emit(false)
        }
    }

    override fun fetchUserByUserName(userName: String): Flow<NetSearchResult<User?>> = flow {
        try {
            val response =
                amplifyApi.query(ModelQuery.list(User::class.java, User.USER_NAME.eq(userName)))
            if (response.hasData() && response.data.firstOrNull() != null) {
                emit(NetSearchResult.Success(response.data.first()))
            } else {
                emit(NetSearchResult.Error(ResponseException("No User Name Found")))
            }
        } catch (e: ApiException) {
            emit(NetSearchResult.Error(e))
        }
    }

    override suspend fun sendInviteToConnect(
        senderUserId: String?,
        receiverUserId: String,
    ): Boolean {
        // firstName and lastName are required when creating a record,
        // but here we just need the id because we're creating an invite
        val sender =
            User.builder()
                //.firstName(DUMMY)
                //.lastName(DUMMY)
                .userId(DUMMY)
                .firstName(DUMMY)
                .lastName(DUMMY)
                .build()

        val invite = Invite
            .builder()
            .sender(senderUserId)
            .receiver(receiverUserId)
            .user(sender)
            .build()

        return try {
            val result = amplifyApi.mutate(ModelMutation.create(invite))
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
            amplifyApi.query(
                ModelQuery.list(
                    Invite::class.java, Invite.SENDER.eq(senderUserId)
                        .and(Invite.RECEIVER.eq(receiverUserId))
                )
            ).data.items.firstOrNull()?.id
        } catch (e: ApiException) {
            null
        }
    }

    override suspend fun createContact(user: User) {
        val response = amplifyApi.mutate(ModelMutation.create(user))
    }

    override fun fetchSentInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val senderResponse = fetchSentInvites(Invite.SENDER.eq(loggedInUserId))
            emit(fetchInvites(senderResponse))
        }.flowOn(ioDispatcher)

    override fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val receiverResponse =
                fetchReceivedInvites(Invite.RECEIVER.eq(loggedInUserId))
            emit(fetchInvites(receiverResponse))
        }.flowOn(ioDispatcher)

    // TODO: change and use flowOn
    override fun createContact(connectionAccepted: ConnectionAccepted): Flow<NetResult<String>> =
        flow {
            emit(NetResult.Pending)
            val sender = User.justId(connectionAccepted.senderUserId)
            val receiver = User.justId(connectionAccepted.receiverUserId)

            val senderContact = AmplifyContact.builder()
                .contactId(connectionAccepted.receiverUserId)
                .id(connectionAccepted.senderUserId)
                .firstName(connectionAccepted.name) // TODO: decide first name or name
                .userName(connectionAccepted.userName)
                .avatarUri(connectionAccepted.avatarUri)
                .build()

            val receiverContact = AmplifyContact.builder()
                .contactId(connectionAccepted.senderUserId)
                .id(connectionAccepted.receiverUserId)
                .firstName(connectionAccepted.name) // TODO: decide first name or name
                .userName(connectionAccepted.userName)
                .avatarUri(connectionAccepted.avatarUri)
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

                val predicate = QueryField.field(SENDER).eq(connectionAccepted.senderUserId)
                    .and(QueryField.field(RECEIVER).eq(connectionAccepted.receiverUserId))

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

    override suspend fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>> =
        flow<NetworkResponse<List<Contact>>> {
            coroutineScope {
                try {
                    val receivedInvites = async {
                        val receiverResponse =
                            fetchReceivedInvites(Invite.RECEIVER.eq(loggedInUserId))
                        fetchAllInvites(receiverResponse)
                    }

                    val sentInvites = async {
                        val senderResponse = fetchSentInvites(Invite.SENDER.eq(loggedInUserId))
                        fetchAllInvites(senderResponse)
                    }

                    val contacts = async {
                        val contactsResponse = fetchContacts(UserContact.CONTACT.eq(loggedInUserId))
                        createContacts(contactsResponse)
                    }

                    emit(NetworkResponse.Success(receivedInvites.await() + sentInvites.await() + contacts.await()))
                } catch (no: NoInternetException) {
                    emit(NetworkResponse.NoInternet(no.message ?: "no net"))
                } catch (re: ResponseException) {
                    emit(NetworkResponse.Error(re))
                }
            }
        }.flowOn(ioDispatcher)

    private fun handleResponse(response: () -> List<Contact>) {

    }

    private suspend fun fetchSentInvites(query: QueryPredicateOperation<Any>): List<String> {
        val invites = mutableSetOf<String>()
        try {
            val senderResponse = amplifyApi.query(
                ModelQuery.list(
                    Invite::class.java,
                    query,
                )
            )

            if (senderResponse.hasData()) {
                senderResponse.data.items.forEach { item ->
                    invites.add(item.receiver)
                }
            }

            return invites.toList()
        } catch (e: ApiException) {
            if (e.cause is java.io.IOException || e.cause is java.net.UnknownHostException) {
                throw NoInternetException("Cannot read proto.")
            } else {
                throw ResponseException("Cannot read proto.")
            }
        }
    }

    private suspend fun fetchReceivedInvites(
        query: QueryPredicateOperation<Any>,
    ): List<String> {

        val invites = mutableSetOf<String>() // TODO: string or contact?

        try {
            val receiverResponse = amplifyApi.query(
                ModelQuery.list(
                    Invite::class.java,
                    query,
                )
            )
            receiverResponse.data.items.forEach { item ->
                invites.add(item.sender)
            }
            return invites.toList()
        } catch (e: ApiException) {
            if (e.cause is java.io.IOException || e.cause is java.net.UnknownHostException) {
                throw NoInternetException("Cannot read proto.")
            } else {
                throw ResponseException("Cannot read proto.")
            }
        }
    }

    private suspend fun fetchContacts(query: QueryPredicateOperation<Any>): GraphQLResponse<PaginatedResult<UserContact>> {

        try {
            val response = amplifyApi.query(
                ModelQuery.list(
                    UserContact::class.java,
                    query,
                )
            )
            return response
        } catch (e: ApiException) {
            if (e.cause is java.io.IOException || e.cause is java.net.UnknownHostException) {
                throw NoInternetException("Cannot read proto.")
            } else {
                throw ResponseException("Cannot read proto.")
            }
        }
    }

    private suspend fun createContacts(response: GraphQLResponse<PaginatedResult<UserContact>>): List<Contact> {
        val contacts = mutableListOf<Contact>()

        response.data.items.forEach {
            when (val user: ModelReference<User> = it.user) {
                is LazyModelReference -> {

                    val contact = user.fetchModel()

                    contacts.add(
                        CurrentContact(
                            senderUserId = contact?.userId ?: "",
                            avatarUri = contact?.avatarUri ?: "",
                            name = contact?.name ?: "",
                            rowId = contact?.id ?: "",
                            userName = contact?.userName ?: "",
                            userId = "",
                            createdAt = contact?.createdAt!!, // TODO: fix this
                        )
                    )
                }

                else -> Unit
            }
        }
        return contacts
    }


    private suspend fun fetchRowId(
        senderUserId: String?,
        receiverUserId: String,
    ): String? {

        val predicate = QueryField.field("senderUserId").eq(senderUserId)
            .and(QueryField.field("receiverUserId").eq(receiverUserId))

        val filter = Invite.SENDER.eq(senderUserId)
            .and(Invite.RECEIVER.eq(receiverUserId))

        val invites = amplifyApi.query(
            ModelQuery.list(Invite::class.java, filter)
        ).data

        println("Amplify ***** USER $senderUserId -- $receiverUserId")
        println("AmplifyRepo ***** INVITES DATA ${invites.toString()}")

        return ""
    }

    // TODO: change param, change function name
    private suspend fun fetchInvites(connectionInvites: List<String>): NetWorkResult<List<Contact>> {

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
/*
    private suspend fun fetchAllInvites(connectionInvites: List<String>): Flow<NetworkResponse<List<Contact>>> =
        flow {

            if (connectionInvites.isEmpty()) {
                emit(NetworkResponse.Success(emptyList()))
                return@flow
            }

            val invites = mutableListOf<Contact>()

            try {
                val predicate = createQueryPredicate(connectionInvites)
                val response = amplifyApi.query(ModelQuery.list(User::class.java, predicate))

                invites.addAll(UserMapper.toUserList(response))

                emit(NetworkResponse.Success(invites))
            } catch (e: ApiException) {
                if (e.cause is java.io.IOException || e.cause is java.net.UnknownHostException) {
                    emit(NetworkResponse.NoInternet("No Internet"))
                } else {
                    emit(NetworkResponse.Error(e))
                }
            }
        }*/

    private suspend fun fetchAllInvites(connectionInvites: List<String>): List<Contact> {
        if (connectionInvites.isEmpty()) {
            return emptyList()
        }

        val invites = mutableListOf<Contact>()

        try {
            val predicate = createQueryPredicate(connectionInvites)
            val response = amplifyApi.query(ModelQuery.list(User::class.java, predicate))

            invites.addAll(UserMapper.toUserList(response))
            return invites
        } catch (e: ApiException) {
            if (e.cause is java.io.IOException || e.cause is java.net.UnknownHostException) {
                throw NoInternetException("Cannot read proto.")
            } else {
                throw ResponseException("Cannot read proto.")
            }
        }
    }


    private fun createQueryPredicate(ids: List<String>) = ids
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

    private suspend fun putSenderUserContact(contact: GraphQLRequest<com.amplifyframework.datastore.generated.model.UserContact>) =
        amplifyApi.mutate(contact).hasData()

    private suspend fun putReceiverUserContact(contact: GraphQLRequest<com.amplifyframework.datastore.generated.model.UserContact>) =
        amplifyApi.mutate(contact).hasData()

    private suspend fun <T : Model> fetchInviteList(
        modelClass: Class<T>,
        predicate: QueryPredicateGroup,
    ) = amplifyApi.query(ModelQuery.list(modelClass, predicate)).data.items.firstOrNull()

    private fun combineLists(
        receivedInvites: List<Contact>,
        sentInvited: List<Contact>,
        contacts: List<Contact>
    ): List<Contact> {

        val allContacts = mutableListOf<Contact>()

        if (receivedInvites.isNotEmpty()) {
            allContacts.addAll(receivedInvites)
        }

        if (sentInvited.isNotEmpty()) {
            allContacts.addAll(sentInvited)
        }

        if (contacts.isNotEmpty()) {
            allContacts.addAll(contacts)
        }

        return allContacts
    }

    override suspend fun fetchContacts(loggedInUserId: String): Flow<NetResult<List<Contact>>> =
        flow {
            val contacts = mutableListOf<Contact>()
            try {
                val response = amplifyApi.query(
                    ModelQuery.list(
                        UserContact::class.java,
                        UserContact.CONTACT.eq(loggedInUserId)
                    )
                )

                response.data.items.forEach {
                    when (val user: ModelReference<User> = it.user) {
                        is LazyModelReference -> {
                            val contact = user.fetchModel()

                            contacts.add(
                                CurrentContact(
                                    senderUserId = contact?.userId ?: "",
                                    avatarUri = contact?.avatarUri ?: "",
                                    name = contact?.name ?: "",
                                    rowId = contact?.id ?: "",
                                    userName = contact?.userName ?: "",
                                    userId = "",
                                    createdAt = contact?.createdAt!!, // TODO: fix this
                                )
                            )
                        }

                        is LoadedModelReference -> {

                        }
                    }
                }
                emit(NetResult.Success(contacts))
            } catch (e: ApiException) {
                println("AmplifyRepo ***** ERROR ${e.message}")
            }
        }.flowOn(ioDispatcher)

    companion object {
        const val DUMMY = "dummy"
        const val SENDER = "sender"
        const val RECEIVER = "receiver"
    }
}

class ResponseException(message: String) : Exception(message)
class NoInternetException(message: String) : Exception(message)

sealed class NetworkResponse<out T> {
    data class NoInternet<out T>(val message: String) : NetworkResponse<T>()
    data class Success<out T>(val data: T?) : NetworkResponse<T>()
    data class Error(val exception: Throwable) : NetworkResponse<Nothing>()
}

open class Contact(
    val rowId: String,
    val userId: String,
    val userName: String,
    val name: String,
    val avatarUri: String,
    val createdAt: Temporal.DateTime,
)

class ReceivedContactInvite(
    val receiverUserId: String,
    val receivedDate: Date, // TODO: use correct type
    rowId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: Temporal.DateTime,
) : Contact(rowId, userId, userName, name, avatarUri, createdAt)

class SentContactInvite(
    val senderUserId: String,
    val sentDate: Date,
    rowId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: Temporal.DateTime,
) : Contact(rowId, userId, userName, name, avatarUri, createdAt)

class CurrentContact(
    val senderUserId: String,
    rowId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: Temporal.DateTime,
) : Contact(rowId, userId, userName, name, avatarUri, createdAt)