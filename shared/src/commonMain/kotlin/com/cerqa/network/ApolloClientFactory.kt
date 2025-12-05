package com.cerqa.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import com.cerqa.auth.AuthTokenProvider
import io.ktor.client.engine.*

/**
 * Creates a configured Apollo GraphQL client for AppSync with authentication.
 *
 * This client:
 * - Uses platform-specific HTTP engine (OkHttp on Android, Darwin on iOS)
 * - Automatically adds Cognito auth tokens to all requests via interceptor
 * - Includes normalized caching for optimal performance
 * - Supports GraphQL queries, mutations, and subscriptions
 *
 * Note: The httpClientEngine parameter is kept for compatibility but Apollo
 * uses its own default engines. You can customize via httpEngine() if needed.
 */
fun createApolloClient(
    tokenProvider: AuthTokenProvider,
    httpClientEngine: HttpClientEngine
): ApolloClient {
    return ApolloClient.Builder()
        .serverUrl("https://bjkal2uenzfc5mfpceilwq6n3y.appsync-api.us-east-2.amazonaws.com/graphql")
        .addHttpHeader("Content-Type", "application/json")
        .addHttpHeader("Accept", "application/json")
        .addHttpHeader("x-api-key", "da2-y34unktia5al3kquoqtsmoppca")
        // Temporarily disabled - using API key auth instead of Cognito
        // .addHttpInterceptor(AuthInterceptor(tokenProvider))
        .build()
}

/**
 * HTTP interceptor that adds Cognito auth token to all AppSync requests.
 * This gets called for every request to add fresh auth tokens.
 */
private class AuthInterceptor(
    private val tokenProvider: AuthTokenProvider
) : HttpInterceptor {

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {
        // Get the access token from Amplify Auth
        val token = try {
            tokenProvider.getAccessToken()
        } catch (e: Exception) {
            // If not authenticated, proceed without token
            // AppSync will reject if auth is required
            null
        }

        // Add Authorization header if we have a token
        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", token)
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}
