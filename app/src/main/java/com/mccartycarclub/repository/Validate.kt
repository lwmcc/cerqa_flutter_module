package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.core.model.Model

fun <T : Model> validate(
    response: GraphQLResponse<PaginatedResult<T>>,
    data: (NetResult<T>) -> Unit,
) {
    try {
        if (response.hasData()) {
            data(NetResult.Success(response.data.firstOrNull()))
        } else {
            data(NetResult.Success(null))
        }
    } catch (e: ApiException) {
        when (val cause = e.cause) {
            is java.net.UnknownHostException -> {
                println("validate ***** NO INTERNET")
                data(NetResult.Error(cause!!))
            }
        }
    }
}
