package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.LazyModelReference
import com.amplifyframework.core.model.LoadedModelList
import com.amplifyframework.core.model.LoadedModelReference
import com.amplifyframework.core.model.includes
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.core.model.query.predicate.QueryPredicateOperation
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.datastore.generated.model.UserPath
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.mccartycarclub.domain.helpers.SearchResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KClass

class ContactsQueryHelper @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    private val contactsQueryBuilder: QueryBuilder,
    private val searchResult: SearchResult,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : CombinedContactsHelper {
    override fun combineSources() {

    }

    override fun getDeviceContacts() {

    }

    override fun fetchRemoteContacts() {

    }

    override fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>> =
        flow {
            coroutineScope {
                try {
                    val receivedInvites = async {
                        getContactsFromInvites(loggedInUserId = loggedInUserId, isSender = false)
                    }

                    val sentInvites = async {
                        getContactsFromInvites(loggedInUserId = loggedInUserId, isSender = true)
                    }

                    val contacts = async {
                        fetchCurrentContacts(loggedInUserId)
                    }

                    val (received, sent, current) = awaitAll(receivedInvites, sentInvites, contacts)

                    emit(NetworkResponse.Success(received + sent + current))
                } catch (no: NoInternetException) {
                    emit(NetworkResponse.NoInternet)
                } catch (re: ResponseException) {
                    emit(NetworkResponse.Error(re))
                }
            }
        }.flowOn(ioDispatcher)

    override fun fetchContactAppUsers() {
        val phoneNumbers = listOf("+15555551234", "+15555555678")

        val document = """
                        query FetchUsersByPhoneNumbers(${'$'}phoneNumbers: [String!]!) {
                            fetchUsersByPhoneNumbers(phoneNumbers: ${'$'}phoneNumbers) {
                                userId
                                firstName
                                lastName
                                phone
                                email
                            }
                        }
                    """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            document,
            mapOf("phoneNumbers" to phoneNumbers),
            String::class.java,
            GsonVariablesSerializer()
        )

        Amplify.API.query(
            request,
            { response ->
                println("ContactsQueryHelper ***** ${response.data}")
            },
            { error ->
                println("ContactsQueryHelper Query error ***** ${error.localizedMessage}")
            }
        )
    }

    private suspend fun getContactsFromInvites(
        loggedInUserId: String,
        isSender: Boolean
    ): List<Contact> {

        val (invites, predicate, inviteType) = if (isSender) {
            val invites = fetchInvitesForSender(Invite.SENDER_ID.eq(loggedInUserId))
            val predicate = contactsQueryBuilder.buildSenderQueryPredicate(invites)
            Triple(invites, predicate, SentInviteContactInvite::class)
        } else {
            val invites = fetchInvitesForReceiver(Invite.RECEIVER_ID.eq(loggedInUserId))
            val predicate = contactsQueryBuilder.buildReceiverQueryPredicate(invites)
            Triple(invites, predicate, ReceivedContactInvite::class)
        }

        return if (predicate != null) {
            fetchAllInvites(
                connectionInvites = invites,
                predicate = predicate,
                inviteType = inviteType,
            )
        } else {
            emptyList()
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

    fun fetchSentInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val senderResponse = fetchSentInvites(Invite.SENDER_ID.eq(loggedInUserId).toString())
            emit(fetchInvites(senderResponse as List<Invite>, SentInviteContactInvite::class))
        }.flowOn(ioDispatcher)

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

    private fun createReceivedQueryPredicate(invites: List<Invite>) = invites
        .map { User.USER_ID.eq(it.senderId) as QueryPredicate }
        .reduce { acc, value -> acc.or(value) }

    fun fetchReceivedInvites(loggedInUserId: String): Flow<NetWorkResult<List<Contact>>> =
        flow {
            val receiverResponse =
                fetchReceivedInviteList(Invite.RECEIVER_ID.eq(loggedInUserId))
            emit(fetchInvites(receiverResponse, ReceivedContactInvite::class))
        }.flowOn(ioDispatcher)

    private suspend fun fetchInvitesForReceiver(
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

    private suspend fun fetchInvitesForSender(query: QueryPredicateOperation<Any>): List<Invite> {
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

    private suspend fun fetchReceivedInviteList(
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

}