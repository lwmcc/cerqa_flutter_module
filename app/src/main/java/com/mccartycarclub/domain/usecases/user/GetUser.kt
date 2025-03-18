package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.datastore.generated.model.User
import javax.inject.Inject

interface GetUser {
    fun getUserGroups(userId: String)
    fun getUsers()
    fun getUser(userId: String, user: (User) -> Unit)
    fun fetchUserIdFromSentInvite(rowId: String, userId: (String?) -> Unit)
}