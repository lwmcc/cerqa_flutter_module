package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.api.graphql.GraphQLResponse
import com.mccartycarclub.repository.FetchAblyJwtResponse
import com.mccartycarclub.repository.UserContact
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object SearchResultBuilder {
    fun searchResultOf(response: GraphQLResponse<String>) {


        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())  // <-- add this
            .build()
        val adapter = moshi.adapter(ListUsersResponse::class.java)

        val parsedResponse = adapter.fromJson(response.data)

        parsedResponse?.listUsers?.items?.forEach { user ->
            println("SearchResultBuilder: ${user.userName} (${user.email})")
            user.invites?.items?.forEach { invite ->
                println("SearchResultBuilder ID: ${invite.id}, Sender: ${invite.senderId}, Receiver: ${invite.receiverId}")
            }

            user.contacts?.items?.forEach { contact ->
                println("SearchResultBuilder Contact UID: ${contact.userId}, CID: ${contact.contactId}")
            }
        }
    }
}

@JsonClass(generateAdapter = true)
data class ListUsersResponse(
    @Json(name = "listUsers") val listUsers: ListUsersContainer
)

@JsonClass(generateAdapter = true)
data class ListUsersContainer(
    @Json(name = "items") val items: List<UserItem>
)

@JsonClass(generateAdapter = true)
data class UserItem(
    val id: String,
    val userName: String,
    val email: String,
    val invites: InvitesContainer?,
    val contacts: UserContactsContainer?,
)

@JsonClass(generateAdapter = true)
data class InvitesContainer(
    @Json(name = "items") val items: List<InviteItem>
)

@JsonClass(generateAdapter = true)
data class UserContactsContainer(
    @Json(name = "items") val items: List<UserContactItem>
)

@JsonClass(generateAdapter = true)
data class InviteItem(
    val id: String,
    val senderId: String,
    val receiverId: String
)

@JsonClass(generateAdapter = true)
data class UserContactItem(
    val userId: String,
    val contactId: String,
)