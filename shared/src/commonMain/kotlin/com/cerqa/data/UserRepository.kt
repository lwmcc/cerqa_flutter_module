package com.cerqa.data

import com.cerqa.graphql.GetUserQuery

interface UserRepository {
    suspend fun getUser(): Result<GetUserQuery.GetUser>
}
