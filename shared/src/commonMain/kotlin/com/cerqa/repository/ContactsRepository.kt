package com.cerqa.repository

import com.cerqa.auth.AuthTokenProvider
import com.cerqa.models.*
import com.cerqa.network.GraphQLRequest
import com.cerqa.network.GraphQLResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

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
    private val tokenProvider: AuthTokenProvider
) {
    /**
     * Create a new user in the backend (for testing purposes)
     * Gets the current user ID from auth and uses the provided user details
     */
    suspend fun createCurrentUserWithDetails(
        firstName: String,
        lastName: String,
        name: String?,
        phone: String?,
        userName: String?,
        email: String?,
        avatarUri: String?
    ): Result<User> {
        return try {
            // Get current user ID from auth
            val userId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val mutation = """
                mutation CreateUser(${"$"}input: CreateUserInput!) {
                    createUser(input: ${"$"}input) {
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
            """.trimIndent()

            val request = GraphQLRequest(
                query = mutation,
                variables = mapOf(
                    "input" to kotlinx.serialization.json.buildJsonObject {
                        put("id", kotlinx.serialization.json.JsonPrimitive(userId))
                        put("userId", kotlinx.serialization.json.JsonPrimitive(userId))
                        put("firstName", kotlinx.serialization.json.JsonPrimitive(firstName))
                        put("lastName", kotlinx.serialization.json.JsonPrimitive(lastName))
                        name?.let { put("name", kotlinx.serialization.json.JsonPrimitive(it)) }
                        phone?.let { put("phone", kotlinx.serialization.json.JsonPrimitive(it)) }
                        userName?.let { put("userName", kotlinx.serialization.json.JsonPrimitive(it)) }
                        email?.let { put("email", kotlinx.serialization.json.JsonPrimitive(it)) }
                        avatarUri?.let { put("avatarUri", kotlinx.serialization.json.JsonPrimitive(it)) }
                    }
                )
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<CreateUserData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val user = graphQLResponse.data?.createUser
                ?: return Result.failure(Exception("No user returned from mutation"))

            println("ContactsRepository: User created successfully: ${user.userName}")
            Result.success(user)
        } catch (e: Exception) {
            println("ContactsRepository: createCurrentUserWithDetails failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Fetch all contacts for the current user from AppSync.
     * Queries UserContact where userId = currentUser, then expands the contact field.
     */
    suspend fun fetchContacts(): Result<List<Contact>> {
        return try {
            // Get current user ID first
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val query = """
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
                            createdAt = contact.createdAt,
                            phoneNumber = contact.phone
                        )
                    }
                } ?: emptyList()

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
        return try {
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
        }
    }

    /**
     * Create a contact relationship for a specific user (not necessarily the current user).
     * This is used when accepting invites to create bidirectional relationships.
     */
    private suspend fun createContactForUser(userId: String, contactId: String): Result<UserContact> {
        return try {
            println("ContactsRepository: createContactForUser - userId: $userId, contactId: $contactId")

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
            Result.success(userContact)
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
            val query = """
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
                            }
                            createdAt
                            updatedAt
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

            val graphQLResponse: GraphQLResponse<ListUserContactsData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val contacts = graphQLResponse.data?.listUserContacts?.items
                ?.mapNotNull { userContact ->
                    userContact.contact?.let { contact ->
                        println("ContactsRepository: fetchCurrentContacts - mapping contact:")
                        println("  - userId: ${contact.userId}")
                        println("  - userName: ${contact.userName}")
                        println("  - name: ${contact.name}")
                        println("  - phone: ${contact.phone}")
                        CurrentContact(
                            contactId = userContact.id,
                            userId = contact.userId ?: "",
                            userName = contact.userName,
                            name = contact.name,
                            avatarUri = contact.avatarUri,
                            createdAt = contact.createdAt,
                            phoneNumber = contact.phone
                        )
                    }
                } ?: emptyList()

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
            println("ContactsRepository: fetchSentInvites for userId: $userId")
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
            """.trimIndent()

            val request = GraphQLRequest(
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
            println("ContactsRepository: fetchSentInvites found ${invites.size} invites")

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
                println("ContactsRepository: fetching receiver details for userId: ${invite.receiverId}")
                val userResult = fetchUserById(invite.receiverId)
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
            println("ContactsRepository: fetchReceivedInvites for userId: $userId")
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
/*            val receivedInviteContacts = invites.mapNotNull { invite ->
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
            }*/
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
                    createdAt = invite.createdAt,
                    phoneNumber = user?.phone ?: ""
                )
                println("ContactsRepository: created ReceivedContactInvite - userName: ${receivedInvite.userName}, name: ${receivedInvite.name}")
                receivedInvite
            }

            println("ContactsRepository: fetchReceivedInvites returning ${receivedInviteContacts.size} received invite contacts")
            Result.success(receivedInviteContacts)
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

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            // Get raw response text for debugging
            val responseText: String = response.body()
            println("ContactsRepository ***** sendInviteToConnect response: $responseText")

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

            println("ContactsRepository: acceptInvite - currentUserId: $currentUserId")

            // Find the invite ID
            val inviteIdResult = findInviteId(senderUserId, currentUserId)
            if (inviteIdResult.isFailure) {
                println("ContactsRepository: acceptInvite - failed to find invite: ${inviteIdResult.exceptionOrNull()?.message}")
                return Result.failure(inviteIdResult.exceptionOrNull() ?: Exception("Invite not found"))
            }

            val inviteId = inviteIdResult.getOrNull()
                ?: return Result.failure(Exception("Invite not found"))

            println("ContactsRepository: acceptInvite - found inviteId: $inviteId")

            // Create bidirectional contact relationships
            // 1. Current user (receiver) has sender as contact
            println("ContactsRepository: acceptInvite - creating contact relationship (receiver -> sender)")
            val receiverContactResult = addContact(senderUserId)
            if (receiverContactResult.isFailure) {
                println("ContactsRepository: acceptInvite - failed to create receiver contact: ${receiverContactResult.exceptionOrNull()?.message}")
                return Result.failure(receiverContactResult.exceptionOrNull() ?: Exception("Failed to create contact"))
            }

            // 2. Sender has current user (receiver) as contact
            println("ContactsRepository: acceptInvite - creating reciprocal contact relationship (sender -> receiver)")
            val senderContactResult = createContactForUser(senderUserId, currentUserId)
            if (senderContactResult.isFailure) {
                println("ContactsRepository: acceptInvite - failed to create sender contact: ${senderContactResult.exceptionOrNull()?.message}")
                // Note: receiver contact was created, but sender contact failed
                // You might want to delete the receiver contact here to maintain consistency
            }

            println("ContactsRepository: acceptInvite - both contact relationships created, deleting invite")

            // Delete the invite
            val deleteResult = deleteInvite(inviteId)
            if (deleteResult.isFailure) {
                println("ContactsRepository: acceptInvite - WARNING: failed to delete invite: ${deleteResult.exceptionOrNull()?.message}")
                // Continue anyway since the contact relationships were created successfully
            } else {
                println("ContactsRepository: acceptInvite - invite deleted successfully")
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
            println("ContactsRepository: findInviteId - searching for invite")
            println("  - senderId: $senderId")
            println("  - receiverId: $receiverId")

            val query = """
                query ListInvites(${"$"}senderId: String!, ${"$"}receiverId: String!) {
                    listInvites(filter: {
                        senderId: { eq: ${"$"}senderId },
                        receiverId: { eq: ${"$"}receiverId }
                    }) {
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
                variables = mapOf(
                    "senderId" to kotlinx.serialization.json.JsonPrimitive(senderId),
                    "receiverId" to kotlinx.serialization.json.JsonPrimitive(receiverId)
                )
            )

            val response = httpClient.post {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<ListInvitesData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                println("ContactsRepository: findInviteId - GraphQL errors: $errorMessages")
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val invites = graphQLResponse.data?.listInvites?.items
            println("ContactsRepository: findInviteId - found ${invites?.size ?: 0} invites")
            invites?.forEach { invite ->
                println("  - Invite id: ${invite.id}, senderId: ${invite.senderId}, receiverId: ${invite.receiverId}")
            }

            val inviteId = invites?.firstOrNull()?.id
            if (inviteId == null) {
                println("ContactsRepository: findInviteId - No invite found with senderId=$senderId and receiverId=$receiverId")
                return Result.failure(Exception("Invite not found"))
            }

            println("ContactsRepository: findInviteId - SUCCESS - found inviteId: $inviteId")
            Result.success(inviteId)
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
            println("ContactsRepository: deleteInvite - deleting invite with id: $inviteId")

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
                println("ContactsRepository: deleteInvite - GraphQL errors: $errorMessages")
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            println("ContactsRepository: deleteInvite - SUCCESS")
            Result.success(Unit)
        } catch (e: Exception) {
            println("ContactsRepository: deleteInvite - Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Search users by username.
     */
    suspend fun searchUsersByUserName(userName: String): Result<List<User>> {
        return try {
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val query = """
                query ListUsers(${"$"}userName: String!, ${"$"}currentUserId: ID!) {
                    listUsers(filter: {
                        userName: { contains: ${"$"}userName },
                        userId: { ne: ${"$"}currentUserId }
                    }) {
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
                variables = mapOf(
                    "userName" to kotlinx.serialization.json.JsonPrimitive(userName),
                    "currentUserId" to kotlinx.serialization.json.JsonPrimitive(currentUserId)
                )
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

            val users = graphQLResponse.data?.listUsers?.items ?: emptyList()
            Result.success(users)
        } catch (e: Exception) {
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
