package com.cerqa.auth

/**
 * Represents the result of an authentication operation
 */
sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Error(val message: String, val exception: Exception? = null) : AuthResult()
    data object RequiresConfirmation : AuthResult()
}

/**
 * Represents an authenticated user
 */
data class AuthUser(
    val userId: String,
    val email: String?,
    val username: String?
)

/**
 * Represents the current authentication state
 */
sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: AuthUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * Sign-up data
 */
data class SignUpData(
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

/**
 * Sign-in data
 */
data class SignInData(
    val email: String,
    val password: String
)

/**
 * Confirmation data for email verification
 */
data class ConfirmationData(
    val email: String,
    val code: String
)
