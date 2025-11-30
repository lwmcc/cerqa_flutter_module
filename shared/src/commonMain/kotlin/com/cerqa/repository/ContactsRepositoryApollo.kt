package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.models.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Example repository using Apollo Kotlin instead of manual GraphQL.
 *
 * Benefits of Apollo approach:
 * - Type-safe queries with generated code
 * - Automatic response parsing
 * - Built-in caching and normalization
 * - Real-time subscriptions support
 * - Better error handling
 *
 * Once you've generated your GraphQL operations with Apollo,
 * this is how you would use them instead of manual string queries.
 *
 * To use this:
 * 1. Download your actual schema from AppSync
 * 2. Write your .graphql query/mutation files
 * 3. Run ./gradlew :shared:generateApolloSources
 * 4. Apollo will generate type-safe Kotlin classes
 * 5. Use them like shown below (uncomment when ready)
 */
class ContactsRepositoryApollo(
    private val apolloClient: ApolloClient,
    private val tokenProvider: AuthTokenProvider
) {
    /**
     * Fetch contacts using Apollo-generated query.
     * Apollo handles serialization, caching, and type safety.
     *
     * Example usage (uncomment when you have real schema/queries):
     *
     * suspend fun fetchContacts(): Result<List<Contact>> {
     *     return try {
     *         val currentUserId = tokenProvider.getCurrentUserId()
     *             ?: return Result.failure(Exception("User not authenticated"))
     *
     *         // Apollo-generated query class with type-safe variables
     *         val response = apolloClient
     *             .query(ListContactsQuery(userId = currentUserId))
     *             .execute()
     *
     *         // Type-safe error handling
     *         if (response.hasErrors()) {
     *             return Result.failure(Exception(response.errors?.first()?.message))
     *         }
     *
     *         // Type-safe data access - Apollo generates these classes
     *         val contacts = response.data?.listContacts?.items?.map { it.toContact() }
     *             ?: emptyList()
     *
     *         Result.success(contacts)
     *     } catch (e: Exception) {
     *         Result.failure(e)
     *     }
     * }
     */

    /**
     * Watch contacts with real-time updates using subscriptions.
     * Apollo's Flow-based API makes this very clean.
     *
     * Example (uncomment when ready):
     *
     * fun watchContactUpdates(): Flow<Contact> {
     *     return apolloClient
     *         .subscription(OnCreateContactSubscription())
     *         .toFlow()
     *         .map { response ->
     *             response.data?.onCreateContact?.toContact()
     *                 ?: throw Exception("Invalid subscription data")
     *         }
     * }
     */

    /**
     * Extension function to convert Apollo-generated type to domain model.
     * Apollo generates data classes for your GraphQL types.
     *
     * Example (uncomment when ready):
     *
     * private fun ListContactsQuery.Item.toContact(): Contact {
     *     return Contact(
     *         id = id,
     *         userId = userId,
     *         firstName = firstName,
     *         lastName = lastName,
     *         name = name,
     *         phone = phone,
     *         userName = userName,
     *         email = email,
     *         avatarUri = avatarUri,
     *         createdAt = createdAt,
     *         updatedAt = updatedAt
     *     )
     * }
     */
}

/**
 * NOTE: To start using this Apollo-based repository:
 *
 * 1. Download your AppSync schema:
 *    aws appsync get-introspection-schema \
 *      --api-id YOUR_API_ID \
 *      --format SDL \
 *      schema.graphqls
 *
 * 2. Replace the example schema in shared/src/commonMain/graphql/schema.graphqls
 *
 * 3. Update the .graphql files with your actual queries/mutations/subscriptions
 *
 * 4. Run code generation:
 *    ./gradlew :shared:generateApolloSources
 *
 * 5. Apollo will generate classes in:
 *    shared/build/generated/source/apollo/commonMain/com/cerqa/graphql/
 *
 * 6. Import and use the generated classes (they'll match your .graphql file names)
 *
 * 7. Update CommonModule to use ContactsRepositoryApollo instead of ContactsRepository
 */
