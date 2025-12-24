package com.cerqa.repository

import com.cerqa.graphql.FetchAblyJwtQuery

/**
 * Repository for fetching Ably authentication tokens.
 * Provides token requests needed to connect to Ably Realtime.
 */
interface AblyRepository {
    /**
     * Fetch an Ably JWT token request for the given user.
     *
     * @param userId The user ID to authenticate with Ably
     * @return Result containing the token data or an error
     */
    suspend fun fetchAblyToken(userId: String): Result<FetchAblyJwtQuery.FetchAblyJwt>
}
