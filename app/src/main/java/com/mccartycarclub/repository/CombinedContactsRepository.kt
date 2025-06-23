package com.mccartycarclub.repository

import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.google.common.reflect.TypeToken
import com.mccartycarclub.domain.helpers.DeviceContacts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CombinedContactsRepository @Inject constructor(
    private val deviceContacts: DeviceContacts,
    private val contactsHelper: CombinedContactsHelper,
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

    override suspend fun combineDeviceAppUserContacts() {
        println("CombinedContactsRepository ***** combineDeviceAppUserContacts")
        deviceContacts.getDeviceContacts().forEach {
            println("CombinedContactsRepository ***** NAME ${it.name}")
            println("CombinedContactsRepository ***** PHOTO ${it.photoUri}")
            println("CombinedContactsRepository ***** NUMBERS ${it.phoneNumbers}")
            println("CombinedContactsRepository ***** THUMB ${it.thumbnailUri}")
            println("CombinedContactsRepository ***** ALL ${it.toString()}")
        }
    }

    override suspend fun fetchUsersByPhoneNumber() {
        val phoneNumber = "+14805553211"

        val document = """
                query FetchUsersByPhoneNumber(${'$'}phoneNumber: String!) {
                        fetchUsersByPhoneNumber(phoneNumber: ${'$'}phoneNumber) {
                            id
                            name
                            phone
                        }
                    }
                    """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            document,
            mapOf("phoneNumber" to phoneNumber),
            object : TypeToken<List<User>>() {}.type,
            GsonVariablesSerializer()
        )

        Amplify.API.query(
            request,
            { response ->
                println("Lambda RESPONSE → ${response.data}")
            },
            { error ->
                println("Lambda ERROR → ${error.localizedMessage}")
            }
        )
    }

    /*
    on device and remote
     */
    override fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>> =
        contactsHelper.fetchAllContacts(loggedInUserId)

    override fun createContact(
        senderUserId: String,
        loggedInUserId: String
    ): Flow<NetDeleteResult> {
        TODO("Not yet implemented")
    }
}

data class User(
    val id: String,
    val name: String?,
    val phone: String?
)