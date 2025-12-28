package com.cerqa.data

import com.cerqa.graphql.GetUserByUserIdQuery

interface UserRepository {
    suspend fun getUser(): Result<GetUserByUserIdQuery.GetUserByUserId>
}
