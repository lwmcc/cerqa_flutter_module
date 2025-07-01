package com.mccartycarclub.repository

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLRequest
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
import com.amplifyframework.core.model.includes
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.core.model.query.predicate.QueryPredicateGroup
import com.amplifyframework.core.model.query.predicate.QueryPredicateOperation
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.datastore.generated.model.UserPath
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.mccartycarclub.domain.helpers.SearchResult
import com.mccartycarclub.domain.model.UserSearchResult
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ably.lib.rest.Auth.TokenRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Named
import kotlin.String
import kotlin.reflect.KClass

class AmplifyRepo @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    private val contactsQueryBuilder: QueryBuilder,
    private val searchResult: SearchResult,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : RemoteRepo {

    sealed class ContactType {
        data class Received(val type: String) : ContactType()
        data class Sent(val type: String) : ContactType()
        data class Current(val type: String) : ContactType()
    }

    private var remoteUserContacts = emptyList<Contact>()

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

        val filter = Invite.SENDER_ID.eq(senderUserId)
            .and(Invite.RECEIVER_ID.eq(receiverUserId))

        val response = amplifyApi.query(ModelQuery.list(Invite::class.java, filter))
        if (response.hasData()) {
            val count = response.data.items.count()
            emit(count > 0)
        } else { // TODO: need try catch
            emit(false)
        }
    }

    override fun hasExistingInviteToAcceptOrReject(
        loggedInUserId: String,
        receiverUserId: String,
    ): Flow<Boolean> = flow {

        val filter = Invite.RECEIVER_ID.eq(loggedInUserId)
            .and(Invite.SENDER_ID.eq(receiverUserId))

        val response = amplifyApi.query(ModelQuery.list(Invite::class.java, filter))
        if (response.hasData()) {
            val sender = response.data?.firstOrNull()?.senderId
            val receiver = response.data?.firstOrNull()?.receiverId
            println("AmplifyRepo ***** S ${sender} R ${receiver}")
            if (receiver.equals(receiverUserId) && sender.equals(receiverUserId)) {

            }

            emit(true)
        } else { // TODO: need try catch
            emit(false)
        }
    }

    // TODO: flow not needed just return object
    override fun fetchUserByUserName(userName: String): Flow<NetSearchResult<User?>> = flow {
        try {
                val response =
                    amplifyApi.query(ModelQuery.list(User::class.java, User.USER_NAME.eq(userName)))

                // TODO: move to own class
            if (response.hasData() && response.data.firstOrNull() != null) {
                emit(NetSearchResult.Success(response.data.first()))
            } else {
                emit(NetSearchResult.Error(ResponseException("No User Name Found")))
            }
        } catch (e: ApiException) {
            emit(NetSearchResult.Error(e))
        }
        }

    override fun sendInviteToConnect(
        senderUserId: String?,
        receiverUserId: String,
        rowId: String,
    ): Flow<NetworkResponse<String>> = flow {
        val invite = Invite
            .builder()
            .senderId(senderUserId)
            .receiverId(receiverUserId)
            .user(
                User
                    .justId(rowId) // TODO: do not use justId, change this
            )
            .build()
        coroutineScope {
            try {
                val response = amplifyApi.mutate(ModelMutation.create(invite))
                if (response.hasData()) {
                    if (response.data.id != null) {
                        emit(NetworkResponse.Success(response.data.id))
                    } else {
                        emit(NetworkResponse.Error(ResponseException(""))) // TODO: add messages
                    }
                } else {
                    emit(NetworkResponse.Error(ResponseException(""))) // TODO: message
                }
            } catch (ae: AmplifyException) {
                when (ae.cause) {
                    is UnknownHostException, is IOException -> {
                        emit(NetworkResponse.NoInternet)
                    }

                    else -> {
                        emit(NetworkResponse.Error(ae))
                    }
                }
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun sendPhoneNumberInviteToConnect(
        senderUserId: String,
        phoneNumber: String,
    ): Flow<NetworkResponse<String>> = flow {


        emit(NetworkResponse.Error(ResponseException("")))
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

    override fun deleteContact(
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
            val senderResponse = fetchSentInvites(Invite.SENDER_ID.eq(loggedInUserId))
            emit(fetchInvites(senderResponse, SentInviteContactInvite::class))
        }.flowOn(ioDispatcher)

    override fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val receiverResponse =
                fetchReceivedInvites(Invite.RECEIVER_ID.eq(loggedInUserId))
            emit(fetchInvites(receiverResponse, ReceivedContactInvite::class))
        }.flowOn(ioDispatcher)

    // TODO: consider refactoring to make it easier to understand
    override fun createContact(
        senderUserId: String,
        loggedInUserId: String
    ): Flow<NetDeleteResult> =
        flow {
            coroutineScope {
                try {

                    val sendingUser = amplifyApi.query(
                        ModelQuery.list(
                            User::class.java,
                            User.USER_ID.eq(senderUserId)
                        )
                    ).data.firstOrNull()

                    val receivingUser = amplifyApi.query(
                        ModelQuery.list(
                            User::class.java,
                            User.USER_ID.eq(loggedInUserId)
                        )
                    ).data.firstOrNull()

                    val senderUser = UserContact.builder()
                        .user(sendingUser)
                        .contact(receivingUser)
                        .build()

                    val receiverUser = UserContact.builder()
                        .user(receivingUser)
                        .contact(sendingUser)
                        .build()

                    val senderContact = amplifyApi.mutate(ModelMutation.create(senderUser))

                    if (senderContact.hasData() && senderContact.data.id != null) {
                        val receiverContact = amplifyApi.mutate(ModelMutation.create(receiverUser))
                        if (receiverContact.hasData() && receiverContact.data.id != null) {

                            val invites = amplifyApi.query(
                                ModelQuery.list(
                                    Invite::class.java,
                                    contactsQueryBuilder.buildInviteQueryPredicate(
                                        senderUserId = senderUserId,
                                        receiverUserId = loggedInUserId,
                                    ),
                                )
                            )

                            if (invites.data.items.firstOrNull() != null) {
                                deleteInvite(invites.data.items.first())
                            } else {
                                throw ResponseException("An error occurred") // TODO: move message to constant
                            }

                            emit(NetDeleteResult.Success)
                        } else {
                            throw ResponseException("An error occurred") // TODO: move message to constant
                        }
                    } else {
                        throw ResponseException("An error occurred") // TODO: move message to constant
                    }
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

    override fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>> =
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
                                // inviteReceiver = loggedInUserId,
                                connectionInvites = invites,
                                predicate = predicate,
                                inviteType = SentInviteContactInvite::class,
                            )
                        } else {
                            emptyList()
                        }
                    }

                    val contacts = async {
                        fetchCurrentContacts(loggedInUserId)
                    }

                    val (received, sent, current) = awaitAll(receivedInvites, sentInvites, contacts)
                    remoteUserContacts = current
                    emit(NetworkResponse.Success(received + sent + current))
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
            receiverResponse.data.let {
                it?.items?.forEach { item ->
                    invites.add(item)
                }
            }
/*            receiverResponse.data.items.forEach { item ->
                invites.add(item)
            }*/
            invites
        } catch (e: ApiException) {
            if (e.cause is IOException || e.cause is UnknownHostException) {
                throw NoInternetException("No Internet") // TODO: move to constant
            } else {
                throw ResponseException("A response error occurred")// TODO: move to constant
            }
        }
    }

    private suspend fun fetchContacts(
        query: QueryPredicateOperation<Any>
    ): GraphQLResponse<PaginatedResult<UserContact>> {

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

               // val page = items.fetchPage()

               // println("AmplifyRepo ***** ${page.items}")
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
                            name = contact.name ?: "",
                            userName = contact.userName ?: "",
                            userId = contact.userId!!, // TODO
                            createdAt = contact.createdAt.toString(), // TODO: fix this
                            phoneNUmber = contact.phone,
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

            invites.addAll(UserMapper.toUserList(response, inviteType))

            return NetWorkResult.Success(invites)
        } catch (e: ApiException) {
            return NetWorkResult.Error(e)
        }
    }

    private suspend fun <T : Contact> fetchAllInvites(
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

            invites.addAll(UserMapper.toUserList(response, inviteType))
            return invites
        } catch (e: ApiException) {
            if (e.cause is IOException || e.cause is UnknownHostException) {
                throw NoInternetException("No Internet")
            } else {
                throw ResponseException("There was an error in the response")
            }
        }
    }

    private suspend fun fetchCurrentContacts(loggedInUserId: String): List<CurrentContact> {
        return try {
            val rowId = amplifyApi.query(
                ModelQuery.list(
                    User::class.java,
                    User.USER_ID.eq(loggedInUserId)
                )
            ).data.first().id

            val result = amplifyApi.query(
                ModelQuery.get<User, UserPath>(
                    User::class.java,
                    User.UserIdentifier(rowId)
                ) { includes(it.asContact) }
            )

            val currentContacts =
                (result.data.asContact as? LoadedModelList<UserContact>)?.items
                    ?: emptyList()

            val contacts = mutableListOf<CurrentContact>()
            currentContacts.forEach { contact ->
                val user = when (val reference = contact.user) {
                    is LazyModelReference<User> -> {
                        reference.fetchModel()
                    }

                    is LoadedModelReference<User> -> {
                        reference.value
                    }

                    else -> {
                        null
                    }
                }

                user?.let { userContact ->
                    contacts.add(
                        CurrentContact(
                            // TODO: might remove this may not be needed
                            contactId = "", // to id the contact in viewmodel list, this is the rowId
                            userId = userContact.userId,
                            userName = userContact.userName,
                            name = userContact.name,
                            avatarUri = userContact.avatarUri,
                            createdAt = userContact.createdAt.toString(),
                            phoneNUmber = userContact.phone,
                        )
                    )
                }
            }

            contacts
        } catch (nsee: NoSuchElementException) {
            emptyList<CurrentContact>()
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

    companion object {
        const val DUMMY = "dummy"
    }

    /**
     * Emits a token and completes
     */
    override fun fetchAblyToken(userId: String): Flow<TokenRequest> = callbackFlow {
        val document = """
            query FetchAblyJwt(${'$'}userId: String!) {
                fetchAblyJwt(userId: ${'$'}userId)  {
                    keyName
                    clientId
                    timestamp
                    nonce
                    mac
                  }
            }
            """.trimIndent()

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
                val adapter = moshi.adapter(FetchAblyJwtResponse::class.java)

                try {
                    val response = adapter.fromJson(it.data)

                    println("AmplifyRepo: ***** CID  ${response?.fetchAblyJwt?.clientId}")
                    println("AmplifyRepo: ***** TOKEN  ${response?.fetchAblyJwt}")
                    if (response != null) {
                        val token = response.fetchAblyJwt

                        val tokenResponse = TokenRequest()
                        tokenResponse.keyName = token.keyName
                        tokenResponse.clientId = token.clientId
                        tokenResponse.timestamp = token.timestamp?.toLong() ?: 0
                        tokenResponse.nonce = token.nonce
                        tokenResponse.mac = token.mac
                        //tokenResponse.capability = "{\"*\":[\"*\"]}"
                        //tokenResponse.ttl = 3_600_000

                        trySend(tokenResponse).onFailure {
                            println("AmplifyRepo ***** ERROR ${it?.message}")
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

    // TODO: return flow no need to suspend
    override fun searchUsers(
        loggedInUserId: String?,
        userName: String,
    ): Flow<NetworkResponse<UserSearchResult>> = flow {
        coroutineScope {
            try {
                val user = amplifyApi.query(
                    ModelQuery.list(User::class.java, User.USER_NAME.eq(userName))
                ).data.items.firstOrNull()

                if (!loggedInUserId.isNullOrEmpty() && !user?.userId.isNullOrEmpty()) {
                    val relatedUserData = amplifyApi.query(
                        ModelQuery.get<User, UserPath>(
                            User::class.java,
                            User.UserIdentifier(user?.id)
                        ) { includes(it.contacts, it.invites) }
                    ).data

                    val predicate =
                        contactsQueryBuilder.buildInviteQueryPredicate(
                            user.userId,
                            loggedInUserId,
                        )
                    val response = amplifyApi.query(ModelQuery.list(Invite::class.java, predicate))

                    emit(
                        NetworkResponse.Success(
                            searchResult.searchResultOf(
                                loggedInUserId,
                                user,
                                relatedUserData,
                                response,
                            )
                        )
                    )
                }
            } catch (ae: AmplifyException) {
                when (ae.cause) {
                    is UnknownHostException, is IOException -> {
                        emit(NetworkResponse.NoInternet)
                    }

                    else -> {
                        emit(NetworkResponse.Error(ae))
                    }
                }
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun searchUsersByUserName(userName: String, loggedInUserId: String):
            Flow<NetworkResponse<List<SearchUser>>> {
        val document = """
                        query SearchByUserName(${'$'}userName: String!, ${'$'}loggedInUserId: String!) {
                          searchByUserName(userName: ${'$'}userName, loggedInUserId: ${'$'}loggedInUserId) {
                            id
                            userName
                            avatarUri
                            userId
                            phone
                          }
                        }
                    """.trimIndent()

        val variables = mapOf("userName" to userName, "loggedInUserId" to loggedInUserId)

        val request = SimpleGraphQLRequest<SearchByUserNameResponse>(
            document,
            variables,
            SearchByUserNameResponse::class.java,
            GsonVariablesSerializer(),
        )

    /*    remoteUserContacts.forEach {
            println("AmplifyRepo ***** REMOTE ${it.userName}")
            println("AmplifyRepo ***** REMOTE ${it.userId}")
        }*/

      //  try {

            return flow { emit(NetworkResponse.Success(amplifyApi.query(request).data.searchByUserName)) }
/*        } catch (ae: AmplifyException) {
            when (ae.cause) {
                is UnknownHostException, is IOException -> {
                    emit(NetworkResponse.NoInternet)
                }

                else -> {
                    emit(NetworkResponse.Error(ae))
                }
            }
        }*/
    }
}

class ResponseException(message: String) : Exception(message)
class NoInternetException(message: String) : Exception(message)

// TODO: consolidate all of these
sealed class NetworkResponse<out T> {
    data object NoInternet : NetworkResponse<Nothing>()
    data class Success<out T>(val data: T?) : NetworkResponse<T>()
    data class Error(val exception: Throwable) : NetworkResponse<Nothing>()
}

open class Contact(
    val contactId: String,
    val userId: String,
    val userName: String,
    val name: String,
    val avatarUri: String,
    val createdAt: String,
    val phoneNUmber: String,
)

class ReceivedContactInvite(
    contactId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: String,
    phoneNUmber: String,
) : Contact(contactId, userId, userName, name, avatarUri, createdAt, phoneNUmber)

class SentInviteContactInvite(
    val senderUserId: String,
    contactId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: String,
    phoneNUmber: String,
) : Contact(contactId, userId, userName, name, avatarUri, createdAt, phoneNUmber)

class CurrentContact(
    contactId: String,
    userId: String,
    userName: String,
    name: String,
    avatarUri: String,
    createdAt: String,
    phoneNUmber: String,
) : Contact(contactId, userId, userName, name, avatarUri, createdAt, phoneNUmber)


@JsonClass(generateAdapter = true)
data class FetchAblyJwtResponse(
    val fetchAblyJwt: AblyJwt
)

@JsonClass(generateAdapter = true)
data class AblyJwt(
    val keyName: String?,
    val clientId: String?,
    val timestamp: Double?,
    val nonce: String?,
    val mac: String?
)

data class SearchByUserNameResponse(
    val searchByUserName: List<SearchUser>
)

data class SearchUser(
    val id: String,
    val userName: String?,
    val avatarUri: String?,
    val userId: String,
    val phone: String?,
    val connectButtonEnabled: Boolean = true,
    val contactType: ContactType?,
)

enum class ContactType {
    RECEIVED, SENT
}