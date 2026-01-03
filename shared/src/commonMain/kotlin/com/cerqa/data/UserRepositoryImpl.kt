package com.cerqa.data

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.ListUsersQuery
import com.cerqa.graphql.type.ModelStringInput
import com.cerqa.graphql.type.ModelUserFilterInput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Implementation of UserRepository using Apollo GraphQL client
 * Uses ListUsers query with filter since GetUserByUserId is not available
 */
class UserRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val authTokenProvider: AuthTokenProvider,
    private val preferences: Preferences,
) : UserRepository {

    override suspend fun getUser(): Result<UserData> {
        return withContext(ioDispatcher) {
            try {
                val cognitoUserId = authTokenProvider.getCurrentUserId()

                if (cognitoUserId.isNullOrEmpty()) {
                    return@withContext Result.failure(IllegalStateException("User is not authenticated"))
                }

                println("UserRepositoryImpl: Fetching user data for userId: $cognitoUserId")

                // Use ListUsers query with filter on userId field
                val filter = ModelUserFilterInput(
                    userId = com.apollographql.apollo.api.Optional.presentIfNotNull(
                        com.cerqa.graphql.type.ModelIDInput(eq = com.apollographql.apollo.api.Optional.presentIfNotNull(cognitoUserId))
                    )
                )

                val response = apolloClient
                    .query(ListUsersQuery(filter = com.apollographql.apollo.api.Optional.presentIfNotNull(filter), limit = com.apollographql.apollo.api.Optional.presentIfNotNull(1)))
                    .execute()

                if (response.hasErrors()) {
                    val errors = response.errors?.joinToString { it.message }
                    println("UserRepositoryImpl: GraphQL errors: $errors")
                    return@withContext Result.failure(Exception(errors ?: "Unknown GraphQL error"))
                }

                val user = response.data?.listUsers?.items?.filterNotNull()?.firstOrNull()
                if (user == null) {
                    println("UserRepositoryImpl: No user found with userId: $cognitoUserId")
                    return@withContext Result.failure(Exception("User not found"))
                }

                println("UserRepositoryImpl: Found user: userName=${user.userName}, email=${user.email}, name=${user.name}")

                // Map GraphQL response to UserData
                val userData = UserData(
                    id = user.id,
                    userId = user.userId,
                    userName = user.userName,
                    email = user.email,
                    avatarUri = user.avatarUri,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    name = user.name,
                    phone = user.phone
                )

                // Cache user data in preferences for offline access
                if (userData.userId != null && userData.userName != null && userData.email != null) {
                    preferences.setUserData(
                        userId = userData.userId,
                        userName = userData.userName,
                        userEmail = userData.email,
                        createdAt = user.createdAt ?: "",
                        avatarUri = userData.avatarUri ?: ""
                    )
                    println("UserRepositoryImpl: Cached user data in preferences")
                }

                Result.success(userData)
            } catch (e: Exception) {
                println("UserRepositoryImpl: Exception: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}
