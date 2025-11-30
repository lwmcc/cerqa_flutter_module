package com.cerqa.auth

/**
 * Interface for providing authentication tokens from platform-specific implementations.
 * Android implementation uses Amplify Android SDK.
 * iOS implementation uses Amplify Swift SDK.
 */
interface AuthTokenProvider {
    /**
     * Get the current access token from the authentication session.
     * This will automatically refresh the token if needed (handled by Amplify).
     * @throws AuthenticationException if user is not authenticated
     */
    suspend fun getAccessToken(): String

    /**
     * Check if the user is currently authenticated.
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Get the current user ID if authenticated.
     */
    suspend fun getCurrentUserId(): String?
}

/**
 * Exception thrown when authentication fails or token cannot be retrieved.
 */
class AuthenticationException(message: String, cause: Throwable? = null) : Exception(message, cause)
