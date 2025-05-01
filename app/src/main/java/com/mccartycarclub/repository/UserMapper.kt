package com.mccartycarclub.repository

import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.datastore.generated.model.User
import kotlin.reflect.KClass

object UserMapper {

    fun <T : Contact> toUserList(
        response: GraphQLResponse<PaginatedResult<User>>,
        inviteType: KClass<T>,
    ): List<Contact> {

        val invites = mutableListOf<Contact>()

        when (inviteType) {
            SentContactInvite::class -> {

                response.data.items.forEach { item ->
                    invites.add(
                        SentContactInvite(
                            avatarUri = item.avatarUri,
                            name = item.name,
                            userId = item.userId,
                            userName = item.userName,
                            createdAt = item.createdAt,
                            rowId = item.id,
                            senderUserId = item.userId,
                            sentDate = item.createdAt.toDate(), // TODO: format date for display
                        )
                    )
                }
            }

            ReceivedContactInvite::class -> {
                response.data.items.forEach { item ->
                    invites.add(
                        ReceivedContactInvite(
                            avatarUri = item.avatarUri,
                            name = item.name,
                            receivedDate = item.createdAt.toDate(), // TODO: format date for display
                            receiverUserId = "",
                            userId = item.userId,
                            userName = item.userName,
                            createdAt = item.createdAt,
                            rowId = item.id,
                        )
                    )
                }
            }

            CurrentContact::class -> {
                response.data.items.forEach { item ->
                    invites.add(
                        CurrentContact(
                            avatarUri = item.avatarUri,
                            name = item.name,
                            userId = item.userId,
                            userName = item.userName,
                            createdAt = item.createdAt,
                            rowId = item.id,
                            senderUserId = item.userId
                        )
                    )
                }
            }
        }

        return invites
    }
}