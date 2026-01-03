package com.cerqa.data

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthTokenProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Implementation of UserRepository using Apollo GraphQL client
 * TODO: Re-implement once GetUserByUserIdQuery is available in API
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
                val userId = authTokenProvider.getCurrentUserId()

                if (userId.isNullOrEmpty()) {
                    return@withContext Result.failure(IllegalStateException("User is not authenticated"))
                }

                // TODO: Implement actual API call once endpoint is available
                // For now, return stub data
                val userData = UserData(
                    userId = userId
                )

                Result.success(userData)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}
