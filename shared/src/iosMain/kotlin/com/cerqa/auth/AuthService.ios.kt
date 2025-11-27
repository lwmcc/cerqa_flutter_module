package com.cerqa.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS implementation of AuthService using Amplify Swift SDK
 * This class bridges Kotlin/Native with Swift Amplify code
 */
actual class AuthService {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    actual val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val amplifyBridge = AmplifyAuthBridge()

    /**
     * Initialize Amplify on iOS
     */
    actual suspend fun initialize() {
        try {
            amplifyBridge.configure()
            // Check if user is already signed in
            val currentUser = amplifyBridge.getCurrentUser()
            _authState.value = if (currentUser != null) {
                AuthState.Authenticated(currentUser)
            } else {
                AuthState.Unauthenticated
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Failed to initialize: ${e.message}")
        }
    }

    actual suspend fun signUp(data: SignUpData): AuthResult {
        return try {
            val result = amplifyBridge.signUp(
                email = data.email,
                password = data.password,
                firstName = data.firstName,
                lastName = data.lastName
            )

            when {
                result.isConfirmed -> {
                    AuthResult.Success(
                        AuthUser(
                            userId = result.userId ?: "",
                            email = data.email,
                            username = data.email
                        )
                    )
                }
                else -> AuthResult.RequiresConfirmation
            }
        } catch (e: Exception) {
            AuthResult.Error("Sign up failed: ${e.message}", e)
        }
    }

    actual suspend fun confirmSignUp(data: ConfirmationData): AuthResult {
        return try {
            amplifyBridge.confirmSignUp(email = data.email, code = data.code)
            AuthResult.Success(
                AuthUser(
                    userId = data.email,
                    email = data.email,
                    username = data.email
                )
            )
        } catch (e: Exception) {
            AuthResult.Error("Confirmation failed: ${e.message}", e)
        }
    }

    actual suspend fun signIn(data: SignInData): AuthResult {
        return try {
            val user = amplifyBridge.signIn(email = data.email, password = data.password)
            _authState.value = AuthState.Authenticated(user)
            AuthResult.Success(user)
        } catch (e: Exception) {
            _authState.value = AuthState.Unauthenticated
            AuthResult.Error("Sign in failed: ${e.message}", e)
        }
    }

    actual suspend fun signOut(): AuthResult {
        return try {
            amplifyBridge.signOut()
            _authState.value = AuthState.Unauthenticated
            AuthResult.Success(
                AuthUser(userId = "", email = null, username = null)
            )
        } catch (e: Exception) {
            AuthResult.Error("Sign out failed: ${e.message}", e)
        }
    }

    actual suspend fun getCurrentUser(): AuthUser? {
        return try {
            amplifyBridge.getCurrentUser()
        } catch (e: Exception) {
            null
        }
    }

    actual suspend fun resendConfirmationCode(email: String): AuthResult {
        return try {
            amplifyBridge.resendConfirmationCode(email)
            AuthResult.Success(AuthUser(userId = email, email = email, username = email))
        } catch (e: Exception) {
            AuthResult.Error("Failed to resend code: ${e.message}", e)
        }
    }

    actual suspend fun resetPassword(email: String): AuthResult {
        return try {
            amplifyBridge.resetPassword(email)
            AuthResult.RequiresConfirmation
        } catch (e: Exception) {
            AuthResult.Error("Password reset failed: ${e.message}", e)
        }
    }

    actual suspend fun confirmResetPassword(
        email: String,
        newPassword: String,
        code: String
    ): AuthResult {
        return try {
            amplifyBridge.confirmResetPassword(email, newPassword, code)
            AuthResult.Success(AuthUser(userId = email, email = email, username = email))
        } catch (e: Exception) {
            AuthResult.Error("Password reset confirmation failed: ${e.message}", e)
        }
    }
}

/**
 * Result from sign up operation
 */
data class SignUpResult(
    val isConfirmed: Boolean,
    val userId: String?
)
