package com.cerqa.data

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.GetUserQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

/**
 * Implementation of UserRepository using Apollo GraphQL client
 */
class UserRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val authTokenProvider: AuthTokenProvider,
    private val preferences: Preferences,
) : UserRepository {

    override suspend fun getUser(): Result<GetUserQuery.GetUser> {
        return withContext(ioDispatcher) {
            try {
                val userId = authTokenProvider.getCurrentUserId()

                if (userId.isNullOrEmpty()) {
                    return@withContext Result.failure(IllegalStateException("User is not authenticated"))
                }

                println("UserRepositoryImpl ===== Fetching user with ID: $userId")

                val response = apolloClient.query(
                    GetUserQuery(id = userId)
                ).execute()

                if (response.hasErrors()) {
                    val errors = response.errors?.joinToString { it.message }
                    println("UserRepositoryImpl ===== GraphQL errors: $errors")
                    return@withContext Result.failure(Exception("GraphQL errors: $errors"))
                }

                val user = response.data?.getUser

                if (user == null) {
                    println("UserRepositoryImpl ===== No user data returned")
                    return@withContext Result.failure(Exception("User not found"))
                }

                // Save username to preferences
                user.userName?.let { userName ->
                    preferences.setUserData(userId = userId, userName = userName)
                    println("UserRepositoryImpl ===== Saved username to preferences: $userName")
                }

                println("UserRepositoryImpl ===== Successfully fetched user: ${user.userName}")
                Result.success(user)
            } catch (e: Exception) {
                println("UserRepositoryImpl ===== Error: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}
