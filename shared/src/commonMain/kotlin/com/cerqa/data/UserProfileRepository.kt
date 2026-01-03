package com.cerqa.data

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthTokenProvider

/**
 * Repository for checking user profile completion status
 * TODO: Re-implement once HasUserCreatedProfileQuery is available in API
 */
class UserProfileRepository(
    private val apolloClient: ApolloClient,
    private val authTokenProvider: AuthTokenProvider,
) {

    /**
     * Checks if user profile is complete
     * @return Result containing Boolean (true if complete, false if incomplete)
     */
    suspend fun isProfileComplete(): Result<Boolean> {
        return try {
            val userId = authTokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            // TODO: Implement actual API call once endpoint is available
            // For now, assume profile is complete
            Result.success(true)

        } catch (e: Exception) {
            println("UserProfileRepository ===== Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
