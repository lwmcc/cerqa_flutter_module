package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.DeleteInviteMutation
import com.cerqa.models.*
import com.cerqa.network.GraphQLRequest
import com.cerqa.network.GraphQLResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import com.cerqa.graphql.DeleteUserMutation

/**
 * Network response wrapper for consistent error handling
 */
sealed class NetworkResponse<out T> {
    data object NoInternet : NetworkResponse<Nothing>()
    data class Success<out T>(val data: T?) : NetworkResponse<T>()
    data class Error(val exception: Throwable) : NetworkResponse<Nothing>()
}

/**
 * Delete/Mutation result wrapper
 */
sealed class NetDeleteResult {
    data object NoInternet : NetDeleteResult()
    data object Success : NetDeleteResult()
    data class Error(val exception: Throwable) : NetDeleteResult()
}

/**
 * Repository for fetching and managing contacts.
 * Uses JWT authentication from Amplify Auth (platform-specific).
 * Makes GraphQL API calls to AWS AppSync using Ktor.
 *
 * This matches your Amplify Gen 2 schema with UserContact join table.
 */
class ContactsRepository(
    private val httpClient: HttpClient,
    private val tokenProvider: AuthTokenProvider,
    private val apolloClient: ApolloClient
) {
    /**
     * Fetch all contacts for the current user from AppSync.
     * Queries UserContact where userId = currentUser, then expands the contact field.
     */
    suspend fun fetchContacts(): Result<List<Contact>> {
        return try {
            // Get current user ID first
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            // 1. Create the filter: userId == currentUserId
            val filter = com.cerqa.graphql.type.ModelUserContactFilterInput(
                userId = com.apollographql.apollo.api.Optional.present(
                    com.cerqa.graphql.type.ModelIDInput(
                        eq = com.apollographql.apollo.api.Optional.present(currentUserId)
                    )
                )
            )

            val response = apolloClient.query(
                com.cerqa.graphql.ListUserContactsQuery(
                    filter = com.apollographql.apollo.api.Optional.present(filter),
                    limit = com.apollographql.apollo.api.Optional.present(100),
                    nextToken = com.apollographql.apollo.api.Optional.absent()
                )
            ).execute()

            if (response.hasErrors()) {
                val errorMessages = response.errors?.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val items = response.data?.listUserContacts?.items ?: emptyList()

            val contacts = items.filterNotNull().mapNotNull { userContact ->
                // The 'contact' field contains the details of the other person
                userContact.contact?.let { contact ->
                    CurrentContact(
                        contactId = userContact.id,
                        userId = contact.userId.orEmpty(),
                        userName = contact.userName,
                        name = contact.name,
                        avatarUri = contact.avatarUri,
                        // createdAt = contact.createdAt.toString(), // Uncomment if needed
                        phoneNumber = contact.phone
                    )
                }
            }


            /*   val query = """
                   query ListUserContacts(${"$"}userId: ID!) {
                       listUserContacts(filter: { userId: { eq: ${"$"}userId } }) {
                           items {
                               id
                               userId
                               contactId
                               contact {
                                   id
                                   userId
                                   firstName
                                   lastName
                                   name
                                   phone
                                   userName
                                   email
                                   avatarUri
                                   createdAt
                                   updatedAt
                               }
                           }
                       }
                   }
               """.trimIndent()

               val request = GraphQLRequest(
                   query = query,
                   variables = mapOf("userId" to kotlinx.serialization.json.JsonPrimitive(currentUserId))
               )

               val response = httpClient.post {
                   contentType(ContentType.Application.Json)
                   setBody(request)
               }

               val graphQLResponse: GraphQLResponse<ListUserContactsData> = response.body()

               if (graphQLResponse.errors != null) {
                   val errorMessages = graphQLResponse.errors.joinToString { it.message }
                   return Result.failure(Exception("GraphQL errors: $errorMessages"))
               }

               // Extract contacts from the UserContact join table and map to CurrentContact
               val contacts = graphQLResponse.data?.listUserContacts?.items
                   ?.mapNotNull { userContact ->
                       userContact.contact?.let { contact ->
                           CurrentContact(
                               contactId = userContact.id,
                               userId = contact.userId ?: "",
                               userName = contact.userName,
                               name = contact.name,
                               avatarUri = contact.avatarUri,
                               //createdAt = contact.createdAt,
                               phoneNumber = contact.phone
                           )
                       }
                   } ?: emptyList()
   */
            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Search contacts by name or phone.
     * Filters locally since AppSync doesn't have a built-in search for this.
     */
    suspend fun searchContacts(query: String): Result<List<Contact>> {
        // For now, fetch all and filter locally
        // You could implement a custom Lambda query if needed
        return fetchContacts().map { contacts ->
            contacts.filter { contact ->
                contact.name?.contains(query, ignoreCase = true) == true ||
                contact.phoneNumber?.contains(query) == true ||
                contact.userName?.contains(query, ignoreCase = true) == true
            }
        }
    }

    /**
     * Add a new contact (create a UserContact relationship).
     * This links the current user to another user by their ID.
     */
    suspend fun addContact(contactUserId: String): Result<UserContact> {
        val currentUserId = tokenProvider.getCurrentUserId()
            ?: return Result.failure(Exception("User not authenticated"))

        return createContactForUser(currentUserId, contactUserId)
/*        return try {
            println("ContactsRepository: addContact - contactUserId: $contactUserId")
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            println("ContactsRepository: addContact - currentUserId: $currentUserId")

            val mutation = """
                mutation CreateUserContact(${"$"}userId: ID!, ${"$"}contactId: ID!) {
                    createUserContact(input: {
                        userId: ${"$"}userId,
                        contactId: ${"$"}contactId
                    }) {
                        id
                        userId
                        contactId
                        contact {
                            id
                            userId
                            firstName
                            lastName
                            name
                            phone
                            userName
                            email
                            avatarUri
                        }
                        createdAt
                        updatedAt
                    }
                }
            """.trimIndent()

            val request = GraphQLRequest(
                query = mutation,
                variables = mapOf(
                    "userId" to kotlinx.serialization.json.JsonPrimitive(currentUserId),
                    "contactId" to kotlinx.serialization.json.JsonPrimitive(contactUserId)
                )
            )

            println("ContactsRepository: addContact - sending createUserContact mutation")

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<CreateUserContactData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                println("ContactsRepository: addContact - GraphQL errors: $errorMessages")
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val userContact = graphQLResponse.data?.createUserContact
            if (userContact == null) {
                println("ContactsRepository: addContact - No UserContact returned from mutation")
                return Result.failure(Exception("No UserContact returned from mutation"))
            }

            println("ContactsRepository: addContact - SUCCESS - created UserContact with id: ${userContact.id}")
            Result.success(userContact)
        } catch (e: Exception) {
            println("ContactsRepository: addContact - Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }*/
    }

    /**
     * Create a contact relationship for a specific user (not necessarily the current user).
     * This is used when accepting invites to create bidirectional relationships.
     */
    private suspend fun createContactForUser(userId: String, contactId: String): Result<UserContact> {
        return try {
          /*  println("ContactsRepository: createContactForUser - userId: $userId, contactId: $contactId")
            val mutation = """
                mutation CreateUserContact(${"$"}userId: ID!, ${"$"}contactId: ID!) {
                    createUserContact(input: {
                        userId: ${"$"}userId,
                        contactId: ${"$"}contactId
                    }) {
                        id
                        userId
                        contactId
                        contact {
                            id
                            userId
                            firstName
                            lastName
                            name
                            phone
                            userName
                            email
                            avatarUri
                        }
                        createdAt
                        updatedAt
                    }
                }
            """.trimIndent()

            val request = GraphQLRequest(
                query = mutation,
                variables = mapOf(
                    "userId" to kotlinx.serialization.json.JsonPrimitive(userId),
                    "contactId" to kotlinx.serialization.json.JsonPrimitive(contactId)
                )
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<CreateUserContactData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                println("ContactsRepository: createContactForUser - GraphQL errors: $errorMessages")
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val userContact = graphQLResponse.data?.createUserContact
            if (userContact == null) {
                println("ContactsRepository: createContactForUser - No UserContact returned")
                return Result.failure(Exception("No UserContact returned from mutation"))
            }

            println("ContactsRepository: createContactForUser - SUCCESS - created UserContact with id: ${userContact.id}")
            Result.success(userContact)*/
            val input = com.cerqa.graphql.type.CreateUserContactInput(
                userId = userId,
                contactId = contactId,
                // Ensure your schema allows these to be empty or generated, otherwise add them
            )

            val response = apolloClient.mutation(
                com.cerqa.graphql.CreateUserContactMutation(input = input)
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            val item = response.data?.createUserContact
            if (item != null) {
                // Return a basic UserContact (mapping might depend on your exact UserContact class definition)
                val userContact = UserContact(
                    id = item.id,
                    userId = item.userId,
                    contactId = item.contactId,
                    createdAt = item.createdAt.toString(),
                    updatedAt = item.updatedAt.toString()
                )
                Result.success(userContact)
            } else {
                Result.failure(Exception("Created contact was null"))
            }
        } catch (e: Exception) {
            println("ContactsRepository: createContactForUser - Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Delete a contact (delete the UserContact relationship).
     */
    suspend fun deleteContact(userContactId: String): Result<Unit> {
        return try {
            val mutation = """
                mutation DeleteUserContact(${"$"}id: ID!) {
                    deleteUserContact(input: { id: ${"$"}id }) {
                        id
                    }
                }
            """.trimIndent()

            val request = GraphQLRequest(
                query = mutation,
                variables = mapOf("id" to kotlinx.serialization.json.JsonPrimitive(userContactId))
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<DeleteUserContactData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Find a user by phone number to add as contact.
     */
    suspend fun findUserByPhone(phone: String): Result<User?> {
        return try {
            val query = """
                query ListByPhone(${"$"}phone: String!) {
                    listByPhone(phone: ${"$"}phone) {
                        items {
                            id
                            userId
                            firstName
                            lastName
                            name
                            phone
                            userName
                            email
                            avatarUri
                        }
                    }
                }
            """.trimIndent()

            val request = GraphQLRequest(
                query = query,
                variables = mapOf("phone" to kotlinx.serialization.json.JsonPrimitive(phone))
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<ListUsersData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val user = graphQLResponse.data?.listByPhone?.items?.firstOrNull()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch all contacts including current contacts, sent invites, and received invites.
     * Returns a combined list of Contact objects with different states.
     * Each contact type is fetched independently - if one type fails or is empty,
     * the others will still be returned.
     */
    suspend fun fetchAllContactsWithInvites(): Result<List<Contact>> {
        return try {
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val allContacts = mutableListOf<Contact>()
            var hasAnyData = false

            // Fetch current contacts - handle independently
            try {
                val currentContacts = fetchCurrentContacts(currentUserId)
                currentContacts.onSuccess { contacts ->
                    if (contacts.isNotEmpty()) {
                        println("ContactsRepository: fetchAllContactsWithInvites - adding ${contacts.size} current contacts")
                        allContacts.addAll(contacts)
                        hasAnyData = true // TODO:  what is this
                    }
                }.onFailure { e ->
                    println("ContactsRepository: fetchAllContactsWithInvites - current contacts failed: ${e.message}")
                }
            } catch (e: Exception) {
                println("ContactsRepository: fetchAllContactsWithInvites - current contacts exception: ${e.message}")
            }

            // Fetch sent invites - handle independently
            try {
                val sentInvites = fetchSentInvites(currentUserId)
                sentInvites.onSuccess { invites ->
                    if (invites.isNotEmpty()) {
                        println("ContactsRepository: fetchAllContactsWithInvites - adding ${invites.size} sent invites")
                        allContacts.addAll(invites)
                        hasAnyData = true
                    }
                }.onFailure { e ->
                    println("ContactsRepository: fetchAllContactsWithInvites - sent invites failed: ${e.message}")
                }
            } catch (e: Exception) {
                println("ContactsRepository: fetchAllContactsWithInvites - sent invites exception: ${e.message}")
            }

            // Fetch received invites - handle independently
            try {
                val receivedInvites = fetchReceivedInvites(currentUserId)
                receivedInvites.onSuccess { invites ->
                    if (invites.isNotEmpty()) {
                        println("ContactsRepository: fetchAllContactsWithInvites - adding ${invites.size} received invites")
                        allContacts.addAll(invites)
                        hasAnyData = true
                    }
                }.onFailure { e ->
                    println("ContactsRepository: fetchAllContactsWithInvites - received invites failed: ${e.message}")
                }
            } catch (e: Exception) {
                println("ContactsRepository: fetchAllContactsWithInvites - received invites exception: ${e.message}")
            }

            // Return success with whatever data we have, even if it's empty
            println("ContactsRepository: fetchAllContactsWithInvites - returning ${allContacts.size} total contacts")
            Result.success(allContacts)
        } catch (e: Exception) {
            println("ContactsRepository: fetchAllContactsWithInvites - critical exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Fetch current (accepted) contacts for the user.
     */
    private suspend fun fetchCurrentContacts(userId: String): Result<List<CurrentContact>> {
        return try {
            val response = apolloClient.query(
                com.cerqa.graphql.ListUserContactsQuery(
                    filter = com.apollographql.apollo.api.Optional.present(
                        com.cerqa.graphql.type.ModelUserContactFilterInput(
                            userId = com.apollographql.apollo.api.Optional.present(
                                com.cerqa.graphql.type.ModelIDInput(
                                    eq = com.apollographql.apollo.api.Optional.present(userId)
                                )
                            )
                        )
                    ),
                    limit = com.apollographql.apollo.api.Optional.present(100),
                    nextToken = com.apollographql.apollo.api.Optional.absent()
                )
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            // 2. Map results
            val items = response.data?.listUserContacts?.items ?: emptyList()

            val contacts = items.filterNotNull().mapNotNull { userContact ->
                // The 'contact' field contains the details of the other person
                userContact.contact?.let { contactDetails ->
                    CurrentContact(
                        contactId = userContact.id,
                        userId = contactDetails.userId.orEmpty(),
                        userName = contactDetails.userName,
                        name = contactDetails.name,
                        avatarUri = contactDetails.avatarUri,
                        //createdAt = contactDetails.createdAt.toString(),
                        phoneNumber = contactDetails.phone
                    )
                }
            }

            println("ContactsRepository: fetchCurrentContacts - returning ${contacts.size} contacts")
            contacts.forEach { contact ->
                println("  - Contact: userName=${contact.userName}, name=${contact.name}")
            }
            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch sent connection invites.
     */
    private suspend fun fetchSentInvites(userId: String): Result<List<SentInviteContactInvite>> {
        return try {
/*            println("ContactsRepository: fetchSentInvites for userId: $userId")
            val query = """
                query ListInvites(${"$"}senderId: String!) {
                    listInvites(filter: { senderId: { eq: ${"$"}senderId } }) {
                        items {
                            id
                            userId
                            senderId
                            receiverId
                            createdAt
                        }
                    }
                }
            """.trimIndent()*/

            // 1. Create the filter input
            val filter = com.cerqa.graphql.type.ModelInviteFilterInput(
                senderId = com.apollographql.apollo.api.Optional.present(
                    com.cerqa.graphql.type.ModelStringInput(
                        eq = com.apollographql.apollo.api.Optional.present(userId)
                    )
                )
            )

            // 2. Execute the query using Apollo Client



            val response = apolloClient.query(
                com.cerqa.graphql.ListInvitesQuery(
                    filter = com.apollographql.apollo.api.Optional.present(filter)//,
                    //limit = com.apollographql.apollo.api.Optional.present(100) // Optional: limit results
                )
            ).execute()

            // 3. Handle errors
            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("ContactsRepository: fetchSentInvites GraphQL errors: $errors")
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            // 4. Extract items (Apollo handles the parsing safely)
            val invites = response.data?.listInvites?.items ?: emptyList()

/*            val request = GraphQLRequest(
                query = query,
                variables = mapOf("senderId" to kotlinx.serialization.json.JsonPrimitive(userId))
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<ListInvitesData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                println("ContactsRepository: fetchSentInvites GraphQL errors: $errorMessages")
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val invites = graphQLResponse.data?.listInvites?.items ?: emptyList()
            println("ContactsRepository: fetchSentInvites found ${invites.size} invites")*/

            // Fetch user details for each receiverId
/*            val sentInviteContacts = invites.mapNotNull { invite ->
                println("ContactsRepository: fetching receiver details for userId: ${invite.receiverId}")
                val userResult = fetchUserById(invite.receiverId)
                userResult.getOrNull()?.let { user ->
                    SentInviteContactInvite(
                        senderUserId = invite.senderId,
                        contactId = invite.id,
                        userId = user.userId ?: "",
                        userName = user.userName,
                        name = user.name,
                        avatarUri = user.avatarUri,
                        createdAt = invite.createdAt,
                        phoneNumber = user.phone
                    )
                }
            }*/
            val sentInviteContacts = invites.map { invite ->
                println("ContactsRepository: fetching receiver details for userId: ${invite?.receiverId}")
/*                val userResult = fetchUserById(invite.receiverId)
                val user = userResult.getOrNull()

                // If user is null, we create a placeholder instead of skipping it
                val sentInvite = SentInviteContactInvite(
                    senderUserId = invite.senderId,
                    contactId = invite.id,
                    userId = user?.userId ?: invite.receiverId, // Use invite ID if user obj is null
                    userName = user?.userName ?: "Unknown User",
                    name = user?.name ?: "Pending...",
                    avatarUri = user?.avatarUri,
                    createdAt = invite.createdAt,
                    phoneNumber = user?.phone ?: ""
                )*/
                // Handle nullable fields from Apollo generated code
                val receiverId = invite?.receiverId ?: ""
                val senderId = invite?.senderId ?: ""
                val inviteId = invite?.id.orEmpty()
                // Apollo usually returns generated types for createdAt (e.g. Any or specific scalar), convert to String
                val createdAtStr = invite?.createdAt.orEmpty()

                println("ContactsRepository: fetching receiver details for userId: $receiverId")

                // Fetch user details (Assuming fetchUserById is working/updated)
                val userResult = fetchUserById(receiverId)
                val user = userResult.getOrNull()

                // Create the SentInviteContactInvite object
                val sentInvite = SentInviteContactInvite(
                    senderUserId = senderId,
                    contactId = inviteId,
                    // If user object is null, fallback to the raw receiverId
                    userId = user?.userId ?: receiverId,
                    userName = user?.userName ?: "Unknown User",
                    name = user?.name ?: "Pending...",
                    avatarUri = user?.avatarUri,
                    //createdAt = createdAtStr,
                    phoneNumber = user?.phone
                )
                println("ContactsRepository: created SentInviteContactInvite - userName: ${sentInvite.userName}, name: ${sentInvite.name}")
                sentInvite
            }

            println("ContactsRepository: fetchSentInvites returning ${sentInviteContacts.size} sent invite contacts")
            Result.success(sentInviteContacts)
        } catch (e: Exception) {
            println("ContactsRepository: fetchSentInvites exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Fetch received connection invites.
     */
    private suspend fun fetchReceivedInvites(userId: String): Result<List<ReceivedContactInvite>> {
        return try {
/*            println("ContactsRepository: fetchReceivedInvites for userId: $userId")
            val query = """
                query ListInvites(${"$"}receiverId: String!) {
                    listInvites(filter: { receiverId: { eq: ${"$"}receiverId } }) {
                        items {
                            id
                            userId
                            senderId
                            receiverId
                            createdAt
                        }
                    }
                }
            """.trimIndent()

            val request = GraphQLRequest(
                query = query,
                variables = mapOf("receiverId" to kotlinx.serialization.json.JsonPrimitive(userId))
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<ListInvitesData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                println("ContactsRepository: fetchReceivedInvites GraphQL errors: $errorMessages")
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val invites = graphQLResponse.data?.listInvites?.items ?: emptyList()
            println("ContactsRepository: fetchReceivedInvites found ${invites.size} invites")

            // Fetch user details for each senderId
*//*            val receivedInviteContacts = invites.mapNotNull { invite ->
                println("ContactsRepository: fetching sender details for userId: ${invite.senderId}")
                val userResult = fetchUserById(invite.senderId)
                userResult.getOrNull()?.let { user ->
                    ReceivedContactInvite(
                        contactId = invite.id,
                        userId = user.userId ?: "",
                        userName = user.userName,
                        name = user.name,
                        avatarUri = user.avatarUri,
                        createdAt = invite.createdAt,
                        phoneNumber = user.phone
                    )
                }
            }*//*
            val receivedInviteContacts = invites.map { invite ->
                println("ContactsRepository: fetching sender details for userId: ${invite.senderId}")
                val userResult = fetchUserById(invite.senderId)
                val user = userResult.getOrNull()

                // If user is null, we create a placeholder instead of skipping it
                val receivedInvite = ReceivedContactInvite(
                    contactId = invite.id,
                    userId = user?.userId ?: invite.senderId, // Use invite ID if user obj is null
                    userName = user?.userName ?: "Unknown User",
                    name = user?.name ?: "Unknown Name",
                    avatarUri = user?.avatarUri,
                    //createdAt = invite.createdAt,
                    phoneNumber = user?.phone ?: ""
                )
                println("ContactsRepository: created ReceivedContactInvite - userName: ${receivedInvite.userName}, name: ${receivedInvite.name}")
                receivedInvite
            }*/
            val response = apolloClient.query(
                com.cerqa.graphql.ListInvitesQuery(
                    filter = com.apollographql.apollo.api.Optional.present(
                        com.cerqa.graphql.type.ModelInviteFilterInput(
                            receiverId = com.apollographql.apollo.api.Optional.present(
                                com.cerqa.graphql.type.ModelStringInput(
                                    eq = com.apollographql.apollo.api.Optional.present(userId)
                                )
                            )
                        )
                    ),
                    limit = com.apollographql.apollo.api.Optional.present(100),
                    nextToken = com.apollographql.apollo.api.Optional.absent()
                )
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            val items = response.data?.listInvites?.items ?: emptyList()

            // 2. Map results
            val receivedInvites = items.filterNotNull().map { invite ->
                val senderId = invite.senderId ?: ""

                // Fetch details of the person who sent the invite
                // IMPORTANT: Ensure fetchUserById is also using Apollo (as per previous answer)
                val userResult = fetchUserById(senderId)
                val user = userResult.getOrNull()

                ReceivedContactInvite(
                    // senderUserId = senderId.orEmpty(),
                    contactId = invite.id,
                    userId = user?.userId ?: senderId,
                    userName = user?.userName ?: "Unknown",
                    name = user?.name ?: "Pending...",
                    avatarUri = user?.avatarUri,
                    // createdAt = invite.createdAt.toString(),
                    phoneNumber = user?.phone ?: ""
                )
            }

            Result.success(receivedInvites)
        } catch (e: Exception) {
            println("ContactsRepository: fetchReceivedInvites exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Fetch a user by their userId (not the table primary key).
     */
    private suspend fun fetchUserById(userId: String): Result<User?> {
        return try {
            val query = """
                query ListUsers(${"$"}userId: ID!) {
                    listUsers(filter: { userId: { eq: ${"$"}userId } }) {
                        items {
                            id
                            userId
                            firstName
                            lastName
                            name
                            phone
                            userName
                            email
                            avatarUri
                            createdAt
                        }
                    }
                }
            """.trimIndent()

            val request = GraphQLRequest(
                query = query,
                variables = mapOf("userId" to kotlinx.serialization.json.JsonPrimitive(userId))
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<ListUsersData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                println("ContactsRepository: fetchUserById GraphQL errors: $errorMessages")
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val user = graphQLResponse.data?.listUsers?.items?.firstOrNull()
            if (user == null) {
                println("ContactsRepository: fetchUserById - no user found for userId: $userId")
            } else {
                println("ContactsRepository: fetchUserById - found user:")
                println("  - userId: ${user.userId}")
                println("  - userName: ${user.userName}")
                println("  - name: ${user.name}")
                println("  - phone: ${user.phone}")
            }
            Result.success(user)
        } catch (e: Exception) {
            println("ContactsRepository: fetchUserById exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Send a connection invite to another user.
     */
    suspend fun sendInviteToConnect(receiverUserId: String): Result<String> {
        return try {
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val mutation = """
                mutation CreateInvite(${"$"}input: CreateInviteInput!) {
                    createInvite(input: ${"$"}input) {
                        id
                        userId
                        senderId
                        receiverId
                        createdAt
                        updatedAt
                    }
                }
            """.trimIndent()

            val request = GraphQLRequest(
                query = mutation,
                variables = mapOf(
                    "input" to kotlinx.serialization.json.buildJsonObject {
                        put("userId", kotlinx.serialization.json.JsonPrimitive(currentUserId))
                        put("senderId", kotlinx.serialization.json.JsonPrimitive(currentUserId))
                        put("receiverId", kotlinx.serialization.json.JsonPrimitive(receiverUserId))
                    }
                )
            )



            // TODO: testing
            val input = com.cerqa.graphql.type.CreateInviteInput(
                senderId = com.apollographql.apollo.api.Optional.present(currentUserId),
                receiverId = com.apollographql.apollo.api.Optional.present(receiverUserId),
                userId = currentUserId, // Assuming userId corresponds to the owner/sender
                // status = com.apollographql.apollo.api.Optional.present("PENDING") // Or whatever your status enum/string is
            )

            val response = apolloClient.mutation(
                com.cerqa.graphql.CreateInviteMutation(input = input)
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("ContactsRepository ===== GraphQL Errors: $errors")
                return Result.failure(Exception("GraphQL errors: $errors"))
            }
            println("ContactsRepository input test ***** sendInviteToConnect: ${response.errors}")
            println("ContactsRepository input test ***** sendInviteToConnect: ${response.data}")
            println("ContactsRepository input test ***** sendInviteToConnect: ${response.hasErrors()}")
            // TODO: end test


  /*          val response = httpClient.post {
                contentType(ContentType.Application.Json)
                header("Authorization", tokenProvider.getAccessToken())
                setBody(request)
            }*/

            println("ContactsRepository ***** sendInviteToConnect response: $response")

            // Get raw response text for debugging
            //val responseText: String = response.body()
            val responseText = ""
            println("ContactsRepository ***** sendInviteToConnect responseText: $responseText")

            val graphQLResponse: GraphQLResponse<CreateInviteData> = try {
                kotlinx.serialization.json.Json.decodeFromString(responseText)
            } catch (e: Exception) {
                println("ContactsRepository *****  Failed to deserialize response: ${e.message}")
                println("ContactsRepository ***** Raw response: $responseText")

                // Try to parse just the errors
                val errorRegex = """"message":"([^"]+)"""".toRegex()
                val errorMessages = errorRegex.findAll(responseText)
                    .map { it.groupValues[1] }
                    .joinToString(", ")

                return Result.failure(Exception("ContactsRepository***** GraphQL error: $errorMessages"))
            }

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                println("ContactsRepository ***** GraphQL errors: $errorMessages")
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val inviteId = graphQLResponse.data?.createInvite?.id
                ?: return Result.failure(Exception("No invite ID returned"))

            Result.success(inviteId)
        } catch (e: Exception) {
            println("ContactsRepository ***** Exception: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Cancel a sent connection invite.
     */
    suspend fun cancelInviteToConnect(receiverUserId: String): Result<Unit> {
        return try {
            println("ContactsRepository: cancelInviteToConnect - receiverUserId: $receiverUserId")
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            println("ContactsRepository: cancelInviteToConnect - currentUserId: $currentUserId")

            // First find the invite ID
            val inviteIdResult = findInviteId(currentUserId, receiverUserId)
            if (inviteIdResult.isFailure) {
                println("ContactsRepository: cancelInviteToConnect - failed to find invite: ${inviteIdResult.exceptionOrNull()?.message}")
                return Result.failure(inviteIdResult.exceptionOrNull() ?: Exception("Invite not found"))
            }

            val inviteId = inviteIdResult.getOrNull()
                ?: return Result.failure(Exception("Invite not found"))

            println("ContactsRepository: cancelInviteToConnect - deleting invite with id: $inviteId")

            val mutation = """
                mutation DeleteInvite(${"$"}id: ID!) {
                    deleteInvite(input: { id: ${"$"}id }) {
                        id
                    }
                }
            """.trimIndent()

            val request = GraphQLRequest(
                query = mutation,
                variables = mapOf("id" to kotlinx.serialization.json.JsonPrimitive(inviteId))
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<DeleteInviteData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                println("ContactsRepository: cancelInviteToConnect - delete mutation errors: $errorMessages")
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            println("ContactsRepository: cancelInviteToConnect - SUCCESS")
            Result.success(Unit)
        } catch (e: Exception) {
            println("ContactsRepository: cancelInviteToConnect - Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Accept a received connection invite (creates contact relationship).
     */
    suspend fun acceptInvite(senderUserId: String): Result<UserContact> {
        return try {
            println("ContactsRepository: acceptInvite - senderUserId: $senderUserId")
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            // 1. Find the invite ID (Now uses Apollo)
            val inviteIdResult = findInviteId(senderUserId, currentUserId)
            val inviteId = inviteIdResult.getOrNull()
                ?: return Result.failure(inviteIdResult.exceptionOrNull() ?: Exception("Invite not found"))

            // 2. Create bidirectional contact relationships (Now uses Apollo)
            // A. Current user (receiver) -> Sender
            val receiverContactResult = createContactForUser(currentUserId, senderUserId)
            if (receiverContactResult.isFailure) {
                return Result.failure(receiverContactResult.exceptionOrNull() ?: Exception("Failed to create contact"))
            }

            // B. Sender -> Current user (receiver)
            val senderContactResult = createContactForUser(senderUserId, currentUserId)
            if (senderContactResult.isFailure) {
                println("ContactsRepository: acceptInvite - failed to create sender contact: ${senderContactResult.exceptionOrNull()?.message}")
                // Logic decision: Do we fail the whole thing? usually better to log and continue or retry later
            }

            // 3. Delete the invite (Now uses Apollo)
            val deleteResult = deleteInvite(inviteId)
            if (deleteResult.isFailure) {
                println("ContactsRepository: acceptInvite - WARNING: failed to delete invite")
            }

            println("ContactsRepository: acceptInvite - SUCCESS")
            receiverContactResult
        } catch (e: Exception) {
            println("ContactsRepository: acceptInvite - Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Reject/delete a received connection invite.
     */
    suspend fun deleteReceivedInvite(senderUserId: String): Result<Unit> {
        return try {
            println("ContactsRepository: deleteReceivedInvite - senderUserId: $senderUserId")
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            println("ContactsRepository: deleteReceivedInvite - currentUserId: $currentUserId")

            val inviteIdResult = findInviteId(senderUserId, currentUserId)
            if (inviteIdResult.isFailure) {
                println("ContactsRepository: deleteReceivedInvite - failed to find invite: ${inviteIdResult.exceptionOrNull()?.message}")
                return Result.failure(inviteIdResult.exceptionOrNull() ?: Exception("Invite not found"))
            }

            val inviteId = inviteIdResult.getOrNull()
                ?: return Result.failure(Exception("Invite not found"))

            println("ContactsRepository: deleteReceivedInvite - deleting invite with id: $inviteId")
            val result = deleteInvite(inviteId)

            if (result.isSuccess) {
                println("ContactsRepository: deleteReceivedInvite - SUCCESS")
            } else {
                println("ContactsRepository: deleteReceivedInvite - delete failed: ${result.exceptionOrNull()?.message}")
            }

            result
        } catch (e: Exception) {
            println("ContactsRepository: deleteReceivedInvite - Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Helper to find an invite ID by sender and receiver.
     */
    private suspend fun findInviteId(senderId: String, receiverId: String): Result<String> {
        return try {
            val response = apolloClient.query(
                com.cerqa.graphql.ListInvitesQuery(
                    filter = com.apollographql.apollo.api.Optional.present(
                        com.cerqa.graphql.type.ModelInviteFilterInput(
                            senderId = com.apollographql.apollo.api.Optional.present(
                                com.cerqa.graphql.type.ModelStringInput(
                                    eq = com.apollographql.apollo.api.Optional.present(senderId)
                                )
                            ),
                            receiverId = com.apollographql.apollo.api.Optional.present(
                                com.cerqa.graphql.type.ModelStringInput(
                                    eq = com.apollographql.apollo.api.Optional.present(receiverId)
                                )
                            )
                        )
                    ),
                    limit = com.apollographql.apollo.api.Optional.present(1),
                    nextToken = com.apollographql.apollo.api.Optional.absent()
                )
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            val inviteId = response.data?.listInvites?.items?.firstOrNull()?.id

            if (inviteId != null) {
                Result.success(inviteId)
            } else {
                Result.failure(Exception("Invite not found"))
            }
        } catch (e: Exception) {
            println("ContactsRepository: findInviteId - Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Helper to delete an invite by ID.
     */
    private suspend fun deleteInvite(inviteId: String): Result<Unit> {
        return try {
            val input = com.cerqa.graphql.type.DeleteInviteInput(
                id = inviteId
            )

            val response = apolloClient.mutation(
                com.cerqa.graphql.DeleteInviteMutation(input = input)
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            println("ContactsRepository: deleteInvite - Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Search users by username using Apollo Client.
     */
    suspend fun searchUsersByUserName(userName: String): Result<List<User>> {
        return try {
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            // Build filter for username contains search, excluding current user
            val filter = com.cerqa.graphql.type.ModelUserFilterInput(
                userName = com.apollographql.apollo.api.Optional.present(
                    com.cerqa.graphql.type.ModelStringInput(
                        contains = com.apollographql.apollo.api.Optional.present(userName)
                    )
                ),
                userId = com.apollographql.apollo.api.Optional.present(
                    com.cerqa.graphql.type.ModelIDInput(
                        ne = com.apollographql.apollo.api.Optional.present(currentUserId)
                    )
                )
            )

            val response = apolloClient.query(
                com.cerqa.graphql.ListUsersQuery(
                    filter = com.apollographql.apollo.api.Optional.present(filter),
                    limit = com.apollographql.apollo.api.Optional.present(50),
                    nextToken = com.apollographql.apollo.api.Optional.absent()
                )
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("ContactsRepository ===== GraphQL Errors: $errors")
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            val items = response.data?.listUsers?.items ?: emptyList()

            // Map Apollo generated types to our User model
            val users = items.mapNotNull { item ->
                item?.let {
                    User(
                        id = it.id,
                        userId = it.userId,
                        firstName = it.firstName ?: "",
                        lastName = it.lastName ?: "",
                        name = it.name,
                        phone = it.phone,
                        userName = it.userName,
                        email = it.email,
                        avatarUri = it.avatarUri
                    )
                }
            }

            Result.success(users)
        } catch (e: Exception) {
            println("ContactsRepository ===== Error searching users: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Check if a contact relationship exists between two users.
     */
    suspend fun contactExists(senderUserId: String, receiverUserId: String): Result<Boolean> {
        return try {
            val query = """
                query ListUserContacts(${"$"}userId: ID!, ${"$"}contactId: ID!) {
                    listUserContacts(filter: {
                        userId: { eq: ${"$"}userId },
                        contactId: { eq: ${"$"}contactId }
                    }) {
                        items {
                            id
                        }
                    }
                }
            """.trimIndent()

            val request = GraphQLRequest(
                query = query,
                variables = mapOf(
                    "userId" to kotlinx.serialization.json.JsonPrimitive(senderUserId),
                    "contactId" to kotlinx.serialization.json.JsonPrimitive(receiverUserId)
                )
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<ListUserContactsData> = response.body()

            if (graphQLResponse.errors != null) {
                return Result.success(false)
            }

            val exists = graphQLResponse.data?.listUserContacts?.items?.isNotEmpty() ?: false
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// GraphQL response data classes
@Serializable
private data class ListUserContactsData(
    val listUserContacts: UserContactsConnection
)

@Serializable
private data class CreateUserContactData(
    val createUserContact: UserContact
)

@Serializable
private data class DeleteUserContactData(
    val deleteUserContact: UserContactIdOnly
)

@Serializable
private data class ListUsersData(
    val listByPhone: UsersConnection? = null,
    val listUsers: UsersConnection? = null
)

@Serializable
private data class UserContactsConnection(
    val items: List<UserContact>
)

@Serializable
private data class UsersConnection(
    val items: List<User>
)

@Serializable
private data class UserContactIdOnly(
    val id: String
)

@Serializable
private data class ListInvitesData(
    val listInvites: InvitesConnection
)

@Serializable
private data class InvitesConnection(
    val items: List<Invite>
)

@Serializable
private data class CreateInviteData(
    val createInvite: Invite
)

@Serializable
private data class DeleteInviteData(
    val deleteInvite: InviteIdOnly
)

@Serializable
private data class InviteIdOnly(
    val id: String
)

@Serializable
private data class CreateUserData(
    val createUser: User
)
