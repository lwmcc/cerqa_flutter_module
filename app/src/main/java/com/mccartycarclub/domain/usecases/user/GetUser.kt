package com.mccartycarclub.domain.usecases.user

import javax.inject.Inject

interface GetUser {
    fun getUser(userId: String): String
}