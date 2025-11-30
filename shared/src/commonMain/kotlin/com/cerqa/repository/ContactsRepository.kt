package com.cerqa.repository

import com.cerqa.auth.AuthTokenProvider
import com.cerqa.models.Contact
import com.cerqa.models.UserContact
import com.cerqa.network.GraphQLRequest
import com.cerqa.network.GraphQLResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

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
                bearerAuth(tokenProvider.getAccessToken())
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<ListUserContactsData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            // Extract contacts from the UserContact join table
            val contacts = graphQLResponse.data?.listUserContacts?.items
                ?.mapNotNull { it.contact } // Get the contact User from each UserContact
                ?: emptyList()

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
                contact.firstName.contains(query, ignoreCase = true) ||
                contact.lastName.contains(query, ignoreCase = true) ||
                contact.name?.contains(query, ignoreCase = true) == true ||
                contact.phone?.contains(query) == true ||
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
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

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

            val response = httpClient.post {
                bearerAuth(tokenProvider.getAccessToken())
                setBody(request)
            }

            val graphQLResponse: GraphQLResponse<CreateUserContactData> = response.body()

            if (graphQLResponse.errors != null) {
                val errorMessages = graphQLResponse.errors.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errorMessages"))
            }

            val userContact = graphQLResponse.data?.createUserContact
                ?: return Result.failure(Exception("No UserContact returned from mutation"))

            Result.success(userContact)
        } catch (e: Exception) {
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
                bearerAuth(tokenProvider.getAccessToken())
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
    suspend fun findUserByPhone(phone: String): Result<Contact?> {
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
                bearerAuth(tokenProvider.getAccessToken())
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
    val listByPhone: UsersConnection
)

@Serializable
private data class UserContactsConnection(
    val items: List<UserContact>
)

@Serializable
private data class UsersConnection(
    val items: List<Contact>
)

@Serializable
private data class UserContactIdOnly(
    val id: String
)
