package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.api.graphql.GraphQLResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object SearchResultBuilder {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun searchResultOf(response: GraphQLResponse<String>): UsersResponse? {
        val adapter = moshi.adapter(UsersResponse::class.java)

        val listUsers = adapter.fromJson(response.data)?.listUsers?.items?.first()
        val hasContacts = listUsers?.contacts?.items?.isNotEmpty()
        val hasInvites = listUsers?.invites?.items?.isNotEmpty()

        println("SearchResultBuilder ***** HAS CONTACTS ${hasContacts} C ${listUsers?.contacts?.items}")
        println("SearchResultBuilder ***** HAS INVITES ${hasInvites} I ${listUsers?.invites?.items}")

        return adapter.fromJson(response.data)
    }
}

data class UsersResponse(
    val listUsers: UsersWrapper
)

data class UsersWrapper(
    val items: List<UserItem> = emptyList()
)

data class UserItem(
    val id: String,
    val userName: String,
    val email: String,
    val invites: InvitesContainer,
    val contacts: UserContactsContainer,
)

data class InvitesContainer(
    val items: List<InviteItem> = emptyList()
)

data class UserContactsContainer(
    val items: List<UserContactItem> = emptyList()
)

data class InviteItem(
    val id: String,
    val senderId: String,
    val receiverId: String
)

data class UserContactItem(
    val id: String,
    val userId: String,
    val contactId: String,
)