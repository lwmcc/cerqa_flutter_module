package com.mccartycarclub.repository

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.mccartycarclub.domain.helpers.DeviceContacts
import com.mccartycarclub.domain.model.DeviceContact
import com.mccartycarclub.domain.model.SearchContact
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class CombinedContactsRepository @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    private val deviceContacts: DeviceContacts,
    private val contactsHelper: CombinedContactsHelper,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : ContactsRepository {

    override suspend fun createContact(user: User) {
        TODO("Not yet implemented")
    }

    override fun contactExists(
        senderUserId: String,
        receiverUserId: String
    ): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    private suspend fun combineDeviceAppUserContacts() = withContext(ioDispatcher) {
        deviceContacts.getDeviceContacts().flatMap { contact ->
            contact.phoneNumbers.mapNotNull { phone ->
                phone?.let {
                    DeviceContact(
                        name = contact.name,
                        phoneNumbers = listOf(it),
                        avatarUri = contact.photoUri,
                        thumbnailUri = contact.thumbnailUri
                    )
                }
            }
        }
    }

    override suspend fun fetchUsersByPhoneNumber(loggedInUserId: String):
            Pair<List<SearchContact>, List<SearchContact>> {
        val deviceContacts = combineDeviceAppUserContacts()
        val phoneNumbers: List<String?> = deviceContacts.flatMap { it.phoneNumbers }

        val document = """
                        query SearchUsers(${'$'}phoneNumbers: [String!]!) {
                            searchUsers(phoneNumbers: ${'$'}phoneNumbers) {
                                id
                                phone
                                firstName
                                lastName
                                userName
                            }
                        }
                    """.trimIndent()

        val request = SimpleGraphQLRequest<UsersPhoneSearchResponse>(
            document,
            mapOf("phoneNumbers" to phoneNumbers),
            UsersPhoneSearchResponse::class.java,
            GsonVariablesSerializer()
        )

        val contacts =
            when (val response = contactsHelper.fetchAllContacts(loggedInUserId).first()) {
                is NetworkResponse.Success -> {
                    response.data ?: emptyList()
                }

                else -> {
                    emptyList()
                }
            }

        return withContext(ioDispatcher) {
            try {
                val response = amplifyApi.query(request)
                val appUsers =
                    createAppUsers(
                        cacheContacts = contacts,
                        localPhoneNumbers = deviceContacts,
                        remoteUserPhoneNumbers = response.data.searchUsers,
                    )

                val nonAppUsers = createNonAppUsers(deviceContacts, response.data.searchUsers)

                Pair(appUsers, nonAppUsers)
            } catch (ae: AmplifyException) {
                Pair(emptyList(), emptyList())
            }
        }
    }

    override fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>> {
        return contactsHelper.fetchAllContacts(loggedInUserId).onEach { response ->
            if (response is NetworkResponse.Success) {
                response.data?.let { contacts ->
                    cacheContacts = contacts
                }
            }
        }
    }

    override fun createContact(
        senderUserId: String,
        loggedInUserId: String
    ): Flow<NetDeleteResult> {
        TODO("Not yet implemented")
    }

    private fun createAppUsers(
        cacheContacts: List<Contact>,
        localPhoneNumbers: List<DeviceContact>,
        remoteUserPhoneNumbers: List<UserPhoneSearch>,
    ): List<SearchContact> {

        val cached = cacheContacts.map { it.phoneNUmber }.toSet()
        val remote = remoteUserPhoneNumbers.map { it.phone }.toSet()

        val appUsers = localPhoneNumbers.filter { contact ->
            contact.phoneNumbers.any { phone ->
                phone != null && phone in remote && phone !in cached
            }
        }

        val user = mutableListOf<SearchContact>()

        appUsers.map { contact ->
            user.add(
                SearchContact(
                    name = contact.name,
                    phoneNumbers = contact.phoneNumbers,
                    avatarUri = contact.avatarUri,
                    thumbnailUri = contact.thumbnailUri
                )
            )
        }

        return user
    }

    private fun createNonAppUsers(
        localPhoneNumbers: List<DeviceContact>,
        remoteUserPhoneNumbers: List<UserPhoneSearch>,
    ): List<SearchContact> {
        val remote = remoteUserPhoneNumbers.map { it.phone }.toSet()

        val nonAppUsers = localPhoneNumbers.filterNot { contact ->
            contact.phoneNumbers.any { phone ->
                phone != null && phone in remote
            }
        }

        val nonUser = mutableListOf<SearchContact>()

        nonAppUsers.map {
            nonUser.add(
                SearchContact(
                    name = it.name,
                    phoneNumbers = it.phoneNumbers,
                    avatarUri = it.avatarUri,
                    thumbnailUri = it.thumbnailUri,
                )
            )
        }
        return nonUser
    }

    companion object {
        private var cacheContacts: List<Contact> = emptyList()
    }
}
