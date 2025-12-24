package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.cerqa.graphql.FetchAblyJwtQuery

/**
 * Implementation of AblyRepository using Apollo GraphQL client.
 * Fetches Ably authentication tokens from the backend via AppSync.
 */
class AblyRepositoryImpl(
    private val apolloClient: ApolloClient
) : AblyRepository {

    override suspend fun fetchAblyToken(userId: String): Result<FetchAblyJwtQuery.FetchAblyJwt> {
        return try {
            println("AblyRepository: Fetching Ably token for user: $userId")

            val response = apolloClient.query(
                FetchAblyJwtQuery(userId = userId)
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("AblyRepository: GraphQL errors: $errors")
                return Result.failure(Exception("GraphQL errors: $errors"))
            }

            val tokenData = response.data?.fetchAblyJwt
            if (tokenData == null) {
                println("AblyRepository: No token data returned")
                return Result.failure(Exception("No token data returned"))
            }

            println("AblyRepository: Successfully fetched token")
            println("AblyRepository: keyName: ${tokenData.keyName}")
            println("AblyRepository: clientId: ${tokenData.clientId}")

            Result.success(tokenData)
        } catch (e: Exception) {
            println("AblyRepository: Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
