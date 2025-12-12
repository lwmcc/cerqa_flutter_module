package com.cerqa.repository

import com.cerqa.auth.AuthResult
import com.cerqa.auth.AuthService

/**
 * Implementation of AuthRepository using AuthService
 */
class AuthRepositoryImpl(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun logout(): AuthResult {
        return authService.signOut()
    }
}
