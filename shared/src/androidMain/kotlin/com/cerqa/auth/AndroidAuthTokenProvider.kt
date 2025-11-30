package com.cerqa.auth

import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Android implementation of AuthTokenProvider using Amplify Android SDK.
 * This bridges your existing native Amplify Auth to the shared KMP code.
 */
class AndroidAuthTokenProvider : AuthTokenProvider {

    override suspend fun getAccessToken(): String = suspendCoroutine { continuation ->
        Amplify.Auth.fetchAuthSession(
            { session ->
                try {
                    val cognitoSession = session as? AWSCognitoAuthSession
                    if (cognitoSession == null) {
                        continuation.resumeWithException(
                            AuthenticationException("Session is not a Cognito session")
                        )
                        return@fetchAuthSession
                    }

                    val tokens = cognitoSession.userPoolTokensResult.value
                    val accessToken = tokens?.accessToken
                    if (accessToken != null) {
                        continuation.resume(accessToken)
                    } else {
                        continuation.resumeWithException(
                            AuthenticationException("No access token available")
                        )
                    }
                } catch (e: Exception) {
                    continuation.resumeWithException(
                        AuthenticationException("Error getting access token", e)
                    )
                }
            },
            { error ->
                continuation.resumeWithException(
                    AuthenticationException("Failed to fetch auth session", error)
                )
            }
        )
    }

    override suspend fun isAuthenticated(): Boolean = suspendCoroutine { continuation ->
        Amplify.Auth.fetchAuthSession(
            { session ->
                continuation.resume(session.isSignedIn)
            },
            {
                continuation.resume(false)
            }
        )
    }

    override suspend fun getCurrentUserId(): String? = suspendCoroutine { continuation ->
        Amplify.Auth.getCurrentUser(
            { user ->
                continuation.resume(user.userId)
            },
            {
                continuation.resume(null)
            }
        )
    }
}
