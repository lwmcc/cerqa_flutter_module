package com.mccartycarclub.repository

import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
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
            val phoneNumbers = "+14805553211" //listOf("+14805553211", "+4805554545")

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
                    println("CombinedContactsRepository ***** ${response.data}")
                },
                { error ->
                    println("CombinedContactsRepository Query error ***** ${error.localizedMessage}")
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