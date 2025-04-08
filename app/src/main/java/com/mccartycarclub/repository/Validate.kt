package com.mccartycarclub.repository

import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.core.model.Model

fun <T : Model> validate(
    response: GraphQLResponse<PaginatedResult<T>>,
    data: (NetResult<T>) -> Unit,
) {
    if (response.hasData()) {
        data(NetResult.Success(response.data.firstOrNull()))
    } else {
        data(NetResult.Success(null))
    }
}