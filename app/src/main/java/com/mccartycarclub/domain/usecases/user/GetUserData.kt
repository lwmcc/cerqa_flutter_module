package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.repository.DbRepo
import javax.inject.Inject

class GetUserData @Inject constructor(private val dbRepo: DbRepo) : GetUser {

    override fun getUserGroups(userId: String) {
        val groups = dbRepo.fetchUserGroups(userId)
    }

    override fun getUsers() {
        dbRepo.fetchUsers()
    }

    override fun getUser(userId: String, user: (User) -> Unit) {
        dbRepo.fetchUser(
            userId,
            user = { user ->
                user(user)
            },
        )
    }

    override fun fetchUserByUserName(userName: String) {
        //dbRepo.fetchUserByUserName(userName)
    }

    override fun fetchUserIdFromSentInvite(rowId: String, userId: (String?) -> Unit) {
        dbRepo.fetchUserIdFromSentInvite(rowId, userId = {
            userId(it)
        })
    }
}
