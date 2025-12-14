package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.ListUserContactsQuery
import com.cerqa.graphql.type.ModelUserContactFilterInput
import com.cerqa.graphql.type.ModelIDInput
import com.cerqa.models.Contact
import com.cerqa.models.CurrentContact

/**
 * Repository for fetching contacts using Apollo GraphQL client.
 * Uses Apollo-generated code for type-safe GraphQL queries.
 * Fetches from the UserContact join table to get the current user's contacts.
 */
class ApolloContactsRepository(
    private val apolloClient: ApolloClient,
    private val tokenProvider: AuthTokenProvider
) {
    /**
     * Fetch all contacts for the current user using Apollo client.
     * Queries the UserContact join table filtered by the current user's ID.
     */
    suspend fun fetchContacts(limit: Int = 100): Result<List<Contact>> {
        return try {
            // Get current user ID for filtering
            val currentUserId = tokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            // Create filter to get only this user's contacts
            val filter = ModelUserContactFilterInput(
                userId = com.apollographql.apollo.api.Optional.present(
                    ModelIDInput(
                        eq = com.apollographql.apollo.api.Optional.present(currentUserId)
                    )
                )
            )

            val response = apolloClient.query(
                ListUserContactsQuery(
                    filter = com.apollographql.apollo.api.Optional.present(filter),
                    limit = com.apollographql.apollo.api.Optional.present(limit),
                    nextToken = com.apollographql.apollo.api.Optional.absent()
                )
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            val userContacts = response.data?.listUserContacts?.items ?: emptyList()

            val contacts = userContacts.mapNotNull { userContact ->
                userContact?.contact?.let { contact ->
                    CurrentContact(
                        contactId = userContact.id,
                        userId = contact.userId ?: "",
                        userName = contact.userName,
                        name = contact.name,
                        phoneNumber = contact.phone,
                        avatarUri = contact.avatarUri,
                        //createdAt = userContact.createdA
                    )
                }
            }

            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
