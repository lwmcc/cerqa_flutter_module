package com.cerqa.auth

import kotlinx.coroutines.flow.StateFlow

/**
 * Authentication service interface using expect/actual pattern
 * This will be implemented differently for iOS and Android
 */
expect class AuthService {
    /**
     * Initialize the authentication service
     * Must be called before using any other methods
     */
    suspend fun initialize()

    /**
     * Current authentication state
     */
    val authState: StateFlow<AuthState>

    /**
     * Sign up a new user with email and password
     */
    suspend fun signUp(data: SignUpData): AuthResult

    /**
     * Confirm user sign up with verification code
     */
    suspend fun confirmSignUp(data: ConfirmationData): AuthResult

    /**
     * Sign in an existing user
     */
    suspend fun signIn(data: SignInData): AuthResult

    /**
     * Sign out the current user
     */
    suspend fun signOut(): AuthResult

    /**
     * Get the currently authenticated user
     */
    suspend fun getCurrentUser(): AuthUser?

    /**
     * Resend confirmation code
     */
    suspend fun resendConfirmationCode(email: String): AuthResult

    /**
     * Reset password
     */
    suspend fun resetPassword(email: String): AuthResult

    /**
     * Confirm password reset with code
     */
    suspend fun confirmResetPassword(email: String, newPassword: String, code: String): AuthResult
}
