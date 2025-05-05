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
import kotlin.reflect.KClass

// TODO: split this class up
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

    override fun cancelInviteToConnect(filter: QueryPredicateGroup): Flow<NetDeleteResult> =
        flow {
            try {
                val response = amplifyApi.query(ModelQuery.list(Invite::class.java, filter))

                val items = response.data.items.toList()
                if (items.isEmpty()) {
                    throw ResponseException("Send message")
                }
                amplifyApi.mutate(ModelMutation.delete(items.first()))
                emit(NetDeleteResult.Success)
            } catch (e: ApiException) {

                when (e.cause) {
                    is java.net.UnknownHostException, is java.io.IOException -> {
                        emit(NetDeleteResult.NoInternet)
                    }

                    else -> {
                        emit(NetDeleteResult.Error(e))
                    }
                }

            } catch (re: ResponseException) {
                emit(NetDeleteResult.Error(re))
            }
        }

    override suspend fun deleteContact(
        loggedInUserId: String,
        contactId: String,
    ): Flow<NetDeleteResult> = flow {
        coroutineScope {
            val sender = User.justId(loggedInUserId)
            val receiver = User.justId(contactId)

            val senderContact = UserContact.builder()
                .user(sender)
                .id(loggedInUserId)
                .contact(receiver)
                .build()

            val receiverContact = UserContact.builder()
                .user(receiver)
                .id(contactId)
                .contact(sender)
                .build()

            try {
                amplifyApi.mutate(ModelMutation.delete(senderContact))
                amplifyApi.mutate(ModelMutation.delete(receiverContact))
                emit(NetDeleteResult.Success)
            } catch (e: ApiException) {
                if (e.cause is java.io.IOException || e.cause is java.net.UnknownHostException) {
                    emit(NetDeleteResult.NoInternet)
                } else {
                    emit(NetDeleteResult.Error(e))
                }
            }
        }
    }.flowOn(ioDispatcher)

    private fun throwsApiExceptions(e: ApiException, message: String? = "An Error Occurred") {
        when (e.cause) {
            is java.net.UnknownHostException, is java.io.IOException-> {
                throw NoInternetException("Send message") // TODO: no message needed
            }

            else -> {
                throw ResponseException("Send message")
            }
        }
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
            emit(fetchInvites(senderResponse, SentContactInvite::class))
        }.flowOn(ioDispatcher)

    override fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val receiverResponse =
                fetchReceivedInvites(Invite.RECEIVER.eq(loggedInUserId))
            emit(fetchInvites(receiverResponse, ReceivedContactInvite::class))
        }.flowOn(ioDispatcher)

    // TODO: change and use flowOn this needs to be refactored
    override fun createContact(connectionAccepted: ConnectionAccepted): Flow<NetDeleteResult> =
        flow {
            coroutineScope {

                val invite = Invite.builder()
                    .sender(connectionAccepted.senderUserId).receiver(connectionAccepted.receiverUserId).build()

               val userContact = UserContact.builder()
                    .user(
                        User.builder().userId(connectionAccepted.senderUserId).firstName(DUMMY)
                            .lastName(DUMMY).build()
                    )
                    .contact(
                        User.builder().userId(connectionAccepted.receiverUserId).firstName(DUMMY)
                            .lastName(DUMMY).build()
                    )
                    .build()
                try {

                    //val senderPutResult = putSenderContact(senderContact)
                    //val senderPutResult = amplifyApi.mutate(ModelMutation.create(senderContact))
                    val senderPutResult = amplifyApi.mutate(ModelMutation.create(userContact))

                    val filter = Invite.SENDER.eq(connectionAccepted.senderUserId)
                        .and(Invite.RECEIVER.eq(connectionAccepted.receiverUserId))

                    val invites = amplifyApi.query(
                        ModelQuery.list(
                            Invite::class.java,
                            filter
                        )
                    )

                    println("AmplifyRepo ***** ${invites.data}")

/*
                    val filter = Invite.SENDER.eq(connectionAccepted.senderUserId)
                        .and(Invite.RECEIVER.eq(connectionAccepted.receiverUserId))

                    val invites = amplifyApi.query(ModelQuery.list(Invite::class.java, filter))

                    println("AmplifyRepo ***** RESULT DEL ${invites.data}")*/

/*                    if (invites == null) {
                        emit(NetResult.Error(ResponseException("")))
                        return@coroutineScope
                    }*/

                    // deleteInvite(invites)

                    emit(NetDeleteResult.Success)
                } catch (e: ApiException) {
                    if (e.cause is java.io.IOException || e.cause is java.net.UnknownHostException) {
                        emit(NetDeleteResult.Error(e))
                    } else {
                        emit(NetDeleteResult.Error(e))
                    }
                }
            }
        }.flowOn(ioDispatcher)

    override suspend fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>> =
        flow {
            coroutineScope {
                try {
                    val receivedInvites = async {
                        val receiverResponse =
                            fetchReceivedInvites(Invite.RECEIVER.eq(loggedInUserId))
                        fetchAllInvites(
                            loggedInUserId,
                            receiverResponse,
                            ReceivedContactInvite::class
                        )
                    }

                    val sentInvites = async {
                        val senderResponse = fetchSentInvites(Invite.SENDER.eq(loggedInUserId))
                        fetchAllInvites("", senderResponse, SentContactInvite::class)
                    }

                    val contacts = async {
                        val contactsResponse = fetchContacts(UserContact.CONTACT.eq(loggedInUserId))
                        createContacts(contactsResponse)
                    }

                    emit(NetworkResponse.Success(receivedInvites.await() + sentInvites.await() + contacts.await()))
                } catch (no: NoInternetException) {
                    // TODO: log
                    emit(NetworkResponse.NoInternet)
                } catch (re: ResponseException) {
                    // TODO: log
                    emit(NetworkResponse.Error(re))
                }
            }
        }.flowOn(ioDispatcher)

/*    private fun handleResponse(response: () -> List<Contact>) {

    }*/

    private fun <T> handleResponse(response: GraphQLResponse<PaginatedResult<T>>): T {
        if (response.hasData()) {
            val items = response.data.items.toList()
            if (items.isNotEmpty()) {
                return items.first()
            } else {
                throw ResponseException("") // TODO: add message
            }
        } else {
            throw ResponseException("") // TODO: add message
        }
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
                throw NoInternetException("Send message") // TODO: no message needed
            } else {
                throw ResponseException("Send message")
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
                            contactId = contact?.userId!!, // TODO
                            senderUserId = contact?.userId ?: "",
                            avatarUri = contact?.avatarUri ?: "",
                            name = contact?.name ?: "",
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
    private suspend fun <T : Contact> fetchInvites(
        connectionInvites: List<String>,
        inviteType: KClass<T>
    ): NetWorkResult<List<Contact>> {

        if (connectionInvites.isEmpty()) {
            return NetWorkResult.Success(emptyList())
        }

        val invites = mutableListOf<Contact>()

        try {
            val predicate = createQueryPredicate(connectionInvites)
            val response = amplifyApi.query(ModelQuery.list(User::class.java, predicate))

            invites.addAll(UserMapper.toUserList("", response, inviteType))

            return NetWorkResult.Success(invites)
        } catch (e: ApiException) {
            return NetWorkResult.Error(e)
        }
    }

    private suspend fun <T : Contact> fetchAllInvites(
        inviteReceiver: String,
        connectionInvites: List<String>,
        inviteType: KClass<T>
    ): List<Contact> {
        if (connectionInvites.isEmpty()) {
            return emptyList()
        }

        val invites = mutableListOf<Contact>()

        try {
            val predicate = createQueryPredicate(connectionInvites)
            val response = amplifyApi.query(ModelQuery.list(User::class.java, predicate))

            invites.addAll(UserMapper.toUserList(inviteReceiver, response, inviteType))
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

    private fun buildSenderUserContact(receiver: User, senderContact: User) =
        UserContact.builder()
            .user(receiver)
            .contact(senderContact)
            .build()

    private fun buildReceiverUserContact(sender: User, receiverContact: User) =
        UserContact.builder()
            .user(sender)
            .contact(receiverContact)
            .build()

    private suspend fun putSenderContact(senderContact: UserContact) =
        amplifyApi.mutate(ModelMutation.create(senderContact))

    private suspend fun putReceiverContact(receiverContact: UserContact) =
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
                                    contactId = contact?.id!!, // TODO: fix
                                    senderUserId = contact?.userId ?: "",
                                    avatarUri = contact?.avatarUri ?: "",
                                    name = contact?.name ?: "",
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
    data object NoInternet : NetworkResponse<Nothing>()
    data class Success<out T>(val data: T?) : NetworkResponse<T>()
    data class Error(val exception: Throwable) : NetworkResponse<Nothing>()
}

open class Contact(
    val contactId: String, // to id the contact in viewmodel list
    val userId: String,
    val userName: String,
    val name: String,
    val avatarUri: String,
    val createdAt: Temporal.DateTime?, // TODO: fix this
)

class ReceivedContactInvite(
    val receiverUserId: String,
    val receivedDate: Date, // TODO: use correct type
    contactId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: Temporal.DateTime,
) : Contact(contactId, userId, userName, name, avatarUri, createdAt)

class SentContactInvite(
    val senderUserId: String,
    val sentDate: Date,
    contactId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: Temporal.DateTime,
) : Contact(contactId, userId, userName, name, avatarUri, createdAt)

class CurrentContact(
    val senderUserId: String,
    contactId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: Temporal.DateTime,
) : Contact(contactId, userId, userName, name, avatarUri, createdAt)