package com.cerqa.repository

import com.cerqa.auth.AuthResult

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    /**
     * Log out the current user
     */
    suspend fun logout(): AuthResult
}
