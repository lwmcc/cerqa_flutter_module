package com.cerqa.data

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthService
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.HasUserCreatedProfileQuery

/**
 * Repository for checking user profile completion status
 */
// TODO: us an interface
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

            println("UserProfileRepository ===== Checking profile for userId: $userId")

            val response = apolloClient.query(
                HasUserCreatedProfileQuery(userId = userId)
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("UserProfileRepository ===== GraphQL errors: $errors")
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            val data = response.data?.hasUserCreatedProfile

            if (data == null) {
                println("UserProfileRepository ===== No data returned from query")
                return Result.failure(Exception("No profile data returned"))
            }

            val isComplete = data.isProfileComplete
            println("UserProfileRepository ===== Profile complete: $isComplete")

            Result.success(isComplete)

        } catch (e: Exception) {
            println("UserProfileRepository ===== Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
