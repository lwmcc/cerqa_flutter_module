package com.mccartycarclub.domain.usecases.user

import com.mccartycarclub.repository.DbRepo
import javax.inject.Inject

class GetUserData @Inject constructor(private val dbRepo: DbRepo): GetUser {
    override fun getUser(userId: String): String {

        val userData = dbRepo.getUser(userId)

        return ""
    }
}