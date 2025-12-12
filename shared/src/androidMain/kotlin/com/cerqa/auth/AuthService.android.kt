package com.cerqa.auth

import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Consumer
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class AuthService {
    actual suspend fun initialize() {
    }

    actual val authState: StateFlow<AuthState>
        get() = TODO("Not yet implemented")

    actual suspend fun signUp(data: SignUpData): AuthResult {
        TODO("Not yet implemented")
    }

    actual suspend fun confirmSignUp(data: ConfirmationData): AuthResult {
        TODO("Not yet implemented")
    }

    actual suspend fun signIn(data: SignInData): AuthResult {
        TODO("Not yet implemented")
    }

    actual suspend fun signOut(): AuthResult = suspendCoroutine { continuation ->
        Amplify.Auth.signOut(
            Consumer { result ->
                println("AuthService ===== Sign out succeeded")
                continuation.resume(
                    AuthResult.Success(
                        AuthUser(
                            userId = "",
                            email = null,
                            username = null
                        )
                    )
                )
            }
        )
    }

    actual suspend fun getCurrentUser(): AuthUser? {
        TODO("Not yet implemented")
    }

    actual suspend fun resendConfirmationCode(email: String): AuthResult {
        TODO("Not yet implemented")
    }

    actual suspend fun resetPassword(email: String): AuthResult {
        TODO("Not yet implemented")
    }

    actual suspend fun confirmResetPassword(
        email: String,
        newPassword: String,
        code: String
    ): AuthResult {
        TODO("Not yet implemented")
    }
}