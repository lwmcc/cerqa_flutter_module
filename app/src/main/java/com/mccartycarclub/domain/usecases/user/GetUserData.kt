package com.mccartycarclub.domain.usecases.user

import com.mccartycarclub.repository.DbRepo
import javax.inject.Inject

class GetUserData @Inject constructor(private val dbRepo: DbRepo): GetUser {

    override fun getUserGroups(userId: String) {
        val groups = dbRepo.fetchUserGroups(userId)
    }
}