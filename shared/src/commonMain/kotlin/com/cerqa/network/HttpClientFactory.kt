package com.cerqa.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.cerqa.auth.AuthTokenProvider

/**
 * Creates a configured HTTP client for API calls with JWT authentication.
 */
fun createHttpClient(tokenProvider: AuthTokenProvider): HttpClient {
    return HttpClient {
        // JSON serialization/deserialization
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }

        // Temporarily disabled - using API key auth instead of Cognito
        // install(Auth) {
        //     bearer {
        //         loadTokens {
        //             // Get token from platform-specific Amplify Auth
        //             val token = tokenProvider.getAccessToken()
        //             BearerTokens(accessToken = token, refreshToken = "")
        //         }
        //
        //         refreshTokens {
        //             // Amplify handles token refresh automatically
        //             val token = tokenProvider.getAccessToken()
        //             BearerTokens(accessToken = token, refreshToken = "")
        //         }
        //     }
        // }

        // Logging
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

        // Timeout configuration
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }

        // Default request configuration for AppSync GraphQL
        install(DefaultRequest) {
            // Using production API: wtl5wlqxxvb6lp2nenxcjpvpwq
            url("https://qtjqzunv4rgbtkphmdz2f2ybrq.appsync-api.us-east-2.amazonaws.com/graphql")
            headers.append("x-api-key", "da2-mjgfdw4g6zfv5jgzxsytr4mupa")
        }
    }
}
