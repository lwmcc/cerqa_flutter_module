package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.datastore.generated.model.User

interface GetUser {
    fun getUserGroups(userId: String)
    fun getUsers()
    fun getUser(userId: String, user: (User) -> Unit)
    fun fetchUserByUserName(userId: String)
    fun fetchUserIdFromSentInvite(rowId: String, userId: (String?) -> Unit)
}
