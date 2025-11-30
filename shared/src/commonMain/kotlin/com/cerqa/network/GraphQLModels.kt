package com.cerqa.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * GraphQL request wrapper for AppSync.
 */
@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, JsonElement>? = null
)

/**
 * GraphQL response wrapper from AppSync.
 */
@Serializable
data class GraphQLResponse<T>(
    val data: T? = null,
    val errors: List<GraphQLError>? = null
)

/**
 * GraphQL error structure.
 */
@Serializable
data class GraphQLError(
    val message: String,
    val path: List<String>? = null,
    val extensions: Map<String, JsonElement>? = null
)
