package com.mccartycarclub.repository

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.LazyModelList
import com.amplifyframework.core.model.LazyModelReference
import com.amplifyframework.core.model.LoadedModelList
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
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.UnknownHostException
import java.util.Date
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KClass

// TODO: split this class up
class AmplifyRepo @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    private val contactsQueryBuilder: QueryBuilder,
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
                .userId(DUMMY)
                .firstName(DUMMY)
                .lastName(DUMMY)
                .build()

        val invite = Invite
            .builder()
            .senderId(senderUserId)
            .receiverId(receiverUserId)
            .sender("to-remove") // TODO: remove these
            .receiver("to-remove")
            .build()

        return try {
            val result = amplifyApi.mutate(ModelMutation.create(invite))
            if (result.hasData()) {
                println("AmplifyRepo ***** ${result.data}")
            } else {
                println("AmplifyRepo ***** NO DATA")
            }

            false
        } catch (e: ApiException) {
            false
        }
    }

    override fun cancelInviteToConnect(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<NetDeleteResult> =
        flow {
            coroutineScope {
                val predicate =
                    contactsQueryBuilder.buildInviteQueryPredicate(senderUserId, receiverUserId)
                try {
                    val response = amplifyApi.query(ModelQuery.list(Invite::class.java, predicate))

                    val items = response.data.items.toList()
                    if (items.isEmpty()) {
                        throw ResponseException("Send message")
                    }
                    amplifyApi.mutate(ModelMutation.delete(items.first()))
                    emit(NetDeleteResult.Success)
                } catch (e: ApiException) {

                    when (e.cause) {
                        is UnknownHostException, is IOException -> {
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
        }.flowOn(ioDispatcher)

    override suspend fun deleteContact(
        loggedInUserId: String,
        contactId: String,
    ): Flow<NetDeleteResult> = flow {
        coroutineScope {
            val senderContactQuery = amplifyApi.query(
                ModelQuery.list(
                    UserContact::class.java,
                    UserContact.USER.eq(loggedInUserId).and(UserContact.CONTACT.eq(contactId))
                )
            )

            val receiverContactQuery = amplifyApi.query(
                ModelQuery.list(
                    UserContact::class.java,
                    UserContact.USER.eq(contactId).and(UserContact.CONTACT.eq(loggedInUserId))
                )
            )

            try {
                val sender = senderContactQuery.data.items.firstOrNull()
                val receiver = receiverContactQuery.data.items.firstOrNull()

                if (sender != null && receiver != null) {
                    amplifyApi.mutate(ModelMutation.delete(sender))
                    amplifyApi.mutate(ModelMutation.delete(receiver))
                } else {
                    throw ResponseException("Unable to delete UserContact")
                }

                emit(NetDeleteResult.Success)
            } catch (e: ApiException) {
                if (e.cause is IOException || e.cause is UnknownHostException) {
                    emit(NetDeleteResult.NoInternet)
                } else {
                    emit(NetDeleteResult.Error(e))
                }
            } catch (re: ResponseException) {
                emit(NetDeleteResult.Error(re))
            }
        }
    }.flowOn(ioDispatcher)

    override fun deleteReceivedInviteToContact(
        loggedInUserId: String,
        contactId: String
    ): Flow<NetDeleteResult> = flow {
        coroutineScope {
            val predicate = contactsQueryBuilder.buildInviteQueryPredicate(
                senderUserId = contactId,
                receiverUserId = loggedInUserId,
            )

            val invites = amplifyApi.query(
                ModelQuery.list(
                    Invite::class.java,
                    predicate,
                )
            )

            try {
                if (invites.data.items.firstOrNull() != null) {
                    deleteInvite(invites.data.items.first())
                    emit(NetDeleteResult.Success)
                } else {
                    throw ResponseException("An error occurred")
                }
            } catch (e: ApiException) {
                if (e.cause is IOException || e.cause is UnknownHostException) {
                    emit(NetDeleteResult.NoInternet)
                } else {
                    emit(NetDeleteResult.Error(e))
                }
            }
        }
    }

    override suspend fun createContact(user: User) {
        val response = amplifyApi.mutate(ModelMutation.create(user))
    }

    override fun fetchSentInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val senderResponse = fetchSentInvites(Invite.SENDER.eq(loggedInUserId))
            emit(fetchInvites(senderResponse, SentInviteContactInvite::class))
        }.flowOn(ioDispatcher)

    override fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val receiverResponse =
                fetchReceivedInvites(Invite.RECEIVER_ID.eq(loggedInUserId))
            emit(fetchInvites(receiverResponse, ReceivedContactInvite::class))
        }.flowOn(ioDispatcher)

    // TODO: change and use flowOn this needs to be refactored
    override fun createContact(
        senderUserId: String,
        receiverUserId: String
    ): Flow<NetDeleteResult> =
        flow {
            coroutineScope {
                try {
                    val predicate = contactsQueryBuilder.buildInviteQueryPredicate(
                        senderUserId = senderUserId,
                        receiverUserId = receiverUserId,
                    )

                    val invites = amplifyApi.query(
                        ModelQuery.list(
                            Invite::class.java,
                            predicate,
                        )
                    )

                    if (invites.data.items.firstOrNull() != null) {
                        val sender =
                            User.justId(senderUserId)
                        val receiver = User.justId(receiverUserId)

                        val senderContact =
                            UserContact.builder()
                                .user(sender)
                                .contact(receiver)
                                .build()
                        amplifyApi.mutate(ModelMutation.create(senderContact))

                        val receiverContact =
                            UserContact.builder()
                                .user(receiver)
                                .contact(sender)
                                .build()
                        amplifyApi.mutate(ModelMutation.create(receiverContact))

                        deleteInvite(invites.data.items.first())
                    } else {
                        throw ResponseException("An error occurred") // TODO: move message to constant
                    }

                    emit(NetDeleteResult.Success)
                } catch (e: ApiException) {
                    if (e.cause is IOException || e.cause is UnknownHostException) {
                        emit(NetDeleteResult.NoInternet)
                    } else {
                        emit(NetDeleteResult.Error(e))
                    }
                } catch (re: ResponseException) {
                    emit(NetDeleteResult.Error(re))
                }
            }
        }.flowOn(ioDispatcher)

    override suspend fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>> =
        flow {
            coroutineScope {
                try {
                    val receivedInvites = async {
                        val invites =
                            fetchReceivedInvites(Invite.RECEIVER_ID.eq(loggedInUserId))
                        val predicate =
                            contactsQueryBuilder.buildReceiverQueryPredicate(invites)

                        if (predicate != null) {
                            fetchAllInvites(
                                inviteReceiver = loggedInUserId,
                                connectionInvites = invites,
                                predicate = predicate,
                                inviteType = ReceivedContactInvite::class,
                            )
                        } else {
                            emptyList()
                        }
                    }

                    val sentInvites = async {
                        val invites = fetchSentInvites(Invite.SENDER_ID.eq(loggedInUserId))
                        val predicate = contactsQueryBuilder.buildSenderQueryPredicate(invites)

                        if (predicate != null) {
                            fetchAllInvites(
                                inviteReceiver = loggedInUserId,
                                connectionInvites = invites,
                                predicate = predicate,
                                inviteType = SentInviteContactInvite::class,
                            )
                        } else {
                            emptyList()
                        }
                    }

                    val contacts = async {
                        val contactsResponse = fetchContacts(UserContact.CONTACT.eq(loggedInUserId))
                        createContacts(contactsResponse)
                    }

                    // TODO: testing
                    emit(NetworkResponse.Success(receivedInvites.await() + sentInvites.await() + contacts.await()))
                    //emit(NetworkResponse.Success(MockContacts.loadMockSentInvites()))
                } catch (no: NoInternetException) {
                    emit(NetworkResponse.NoInternet)
                } catch (re: ResponseException) {
                    emit(NetworkResponse.Error(re))
                }
            }
        }.flowOn(ioDispatcher)

    private suspend fun fetchSentInvites(query: QueryPredicateOperation<Any>): List<Invite> {
        val invites = mutableListOf<Invite>()
        try {
            val senderResponse = amplifyApi.query(
                ModelQuery.list(
                    Invite::class.java,
                    query,
                )
            )

            if (senderResponse.hasData()) {
                senderResponse.data.items.forEach { item ->
                    invites.add(item)
                }
            }

            return invites
        } catch (e: ApiException) {
            if (e.cause is IOException || e.cause is UnknownHostException) {
                throw NoInternetException("Send message") // TODO: no message needed
            } else {
                throw ResponseException("Send message")
            }
        }
    }

    private suspend fun fetchReceivedInvites(
        query: QueryPredicateOperation<Any>,
    ): List<Invite> {

        val invites = mutableListOf<Invite>()

        return try {
            val receiverResponse = amplifyApi.query(
                ModelQuery.list(
                    Invite::class.java,
                    query,
                )
            )
            receiverResponse.data.items.forEach { item ->
                invites.add(item)
            }
            invites
        } catch (e: ApiException) {
            if (e.cause is java.io.IOException || e.cause is java.net.UnknownHostException) {
                throw NoInternetException("No Internet") // TODO: move to constant
            } else {
                throw ResponseException("A response error occurred")// TODO: move to constant
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
            if (e.cause is IOException || e.cause is UnknownHostException) {
                throw NoInternetException("Cannot read proto.")
            } else {
                throw ResponseException("Cannot read proto.")
            }
        }
    }

    // TODO: we are mapping here
    private suspend fun createContacts(response: GraphQLResponse<PaginatedResult<UserContact>>): List<Contact> {
        val contacts = mutableListOf<Contact>()

        when (val items = response.data.items) {
            is LoadedModelList<*> -> {

                println("AmplifyRepo ***** ${items.items}")
                items.items.forEach {

                }
            }
            is LazyModelList<*> -> {

                val page = items.fetchPage()

                println("AmplifyRepo ***** ${page.items}")
            }
        }


        response.data.items.forEach {
            when (val user: ModelReference<User> = it.user) {

                is LazyModelReference -> {
                    val contact = user.fetchModel()

                    contacts.add(
                        CurrentContact(
                            contactId = contact?.userId!!, // TODO
                            //senderUserId = contact?.userId ?: "",
                            avatarUri = contact?.avatarUri ?: "",
                            name = contact?.name ?: "",
                            userName = contact?.userName ?: "",
                            userId = contact?.userId!!, // TODO
                            createdAt = contact?.createdAt!!, // TODO: fix this
                        )
                    )
                }

                is LoadedModelReference -> {

                    println("AmplifyRepo ***** LOADED REFERENCE ${it.id}")
                }

                else -> {
                    println("AmplifyRepo ***** LAZY  ELSE ")
                }
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
        connectionInvites: List<Invite>,
        inviteType: KClass<T>
    ): NetWorkResult<List<Contact>> {

        if (connectionInvites.isEmpty()) {
            return NetWorkResult.Success(emptyList())
        }

        val invites = mutableListOf<Contact>()

        try {
            val predicate = createReceivedQueryPredicate(connectionInvites) // TODO:
            val response = amplifyApi.query(ModelQuery.list(User::class.java, predicate))

            invites.addAll(UserMapper.toUserList("", response, inviteType))

            return NetWorkResult.Success(invites)
        } catch (e: ApiException) {
            return NetWorkResult.Error(e)
        }
    }

    private suspend fun <T : Contact> fetchAllInvites(
        inviteReceiver: String,
        connectionInvites: List<Invite>,
        predicate: QueryPredicate,
        inviteType: KClass<T>,
    ): List<Contact> {
        if (connectionInvites.isEmpty()) {
            return emptyList()
        }

        val invites = mutableListOf<Contact>()

        try {
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

    private fun createSentQueryPredicate(invites: List<Invite>) = invites
        .map { User.USER_ID.eq(it.receiverId) as QueryPredicate }
        .reduce { acc, value -> acc.or(value) }

    private fun createReceivedQueryPredicate(invites: List<Invite>) = invites
        .map { User.USER_ID.eq(it.senderId) as QueryPredicate }
        .reduce { acc, value -> acc.or(value) }

    private suspend fun deleteInvite(invite: Invite) =
        amplifyApi.mutate(ModelMutation.delete(invite))

    private suspend fun <T : Model> fetchInviteList(
        modelClass: Class<T>,
        predicate: QueryPredicateGroup,
    ) = amplifyApi.query(ModelQuery.list(modelClass, predicate)).data.items.firstOrNull()

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

                            // TODO: needs to be in mapper
                            contacts.add(
                                CurrentContact(
                                    contactId = contact?.id!!, // TODO: fix
                                    // senderUserId = contact?.userId ?: "",
                                    avatarUri = contact?.avatarUri ?: "",
                                    name = contact?.name ?: "",
                                    userName = contact?.userName ?: "",
                                    userId = contact?.id!!, // TODO: fix !!
                                    createdAt = contact?.createdAt!!, // TODO: fix this
                                )
                            )
                        }

                        else -> {
                            // TODO: log this not needed
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
    }

    /**
     * Emits a token and completes
     */
    override fun fetchAblyToken(userId: String): Flow<String> = callbackFlow {
        val document = """
            query FetchAblyJwt(${'$'}userId: String!) {
                fetchAblyJwt(userId: ${'$'}userId)
            }
            """.trimIndent()

        /*
                        query FetchAblyJwt(${'$'}userId: String!) {
                    fetchAblyJwt(${'$'}userId) {
                        token
                        clientId
                    }
                }
                """.trimIndent()
         */
        val fetchAblyJwtQuery = SimpleGraphQLRequest<String>(
            document,
            mapOf("userId" to userId),
            String::class.java,
            GsonVariablesSerializer()
        )

        Amplify.API.query(
            fetchAblyJwtQuery,
            {
                // TODO: refactor this
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())  // <-- add this
                    .build()
                val adapter = moshi.adapter(FetchAblyJw::class.java)

                try {
                    val response = adapter.fromJson(it.data)

                    if (response != null) {
                        val token = response.fetchAblyJwt
                        trySend(token).onFailure {
                            // TODO: log
                        }
                        close()
                    }
                } catch (jde: JsonDataException) {
                    close(jde)
                } catch (ae: AmplifyException) {
                    close(ae)
                }
            },
            { close(it) }
        )
        awaitClose { /* no-op */ }
    }.flowOn(ioDispatcher)
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

class SentInviteContactInvite(
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
    //val senderUserId: String,
    contactId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: Temporal.DateTime,
) : Contact(contactId, userId, userName, name, avatarUri, createdAt)


data class FetchAblyJw(val fetchAblyJwt: String)