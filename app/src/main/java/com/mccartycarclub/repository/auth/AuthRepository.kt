package com.mccartycarclub.repository.auth

interface AuthRepository {
    fun logout(userId: String)
}