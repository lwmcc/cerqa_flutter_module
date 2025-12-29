package com.cerqa.repository

import com.cerqa.graphql.FetchAblyJwtQuery

interface AblyRepository {
    suspend fun fetchAblyToken(userId: String): Result<FetchAblyJwtQuery.FetchAblyJwt>
}
