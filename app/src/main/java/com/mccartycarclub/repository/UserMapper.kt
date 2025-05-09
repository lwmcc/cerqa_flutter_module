package com.mccartycarclub.repository

import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.ui.components.ConnectionAccepted
import kotlin.reflect.KClass

object UserMapper {

    fun <T : Contact> toUserList(
        inviteReceiver: String,
        response: GraphQLResponse<PaginatedResult<User>>,
        inviteType: KClass<T>,
    ): List<Contact> {

        val invites = mutableListOf<Contact>()

        when (inviteType) {
            SentInviteContactInvite::class -> {

                response.data.items.forEach { item ->
                    invites.add(
                        SentInviteContactInvite(
                            contactId = item.id,
                            avatarUri = item.avatarUri,
                            name = item.name,
                            userId = item.userId,
                            userName = item.userName,
                            createdAt = item.createdAt,
                            //rowId = item.id, // TODO: this is acutally the user id for receiver
                            senderUserId = item.userId,
                            sentDate = item.createdAt.toDate(), // TODO: format date for display
                        )
                    )
                }
            }

            ReceivedContactInvite::class -> {
                response.data.items.forEach { user ->
                    invites.add(
                        ReceivedContactInvite(
                            contactId = user.id,
                            avatarUri = user.avatarUri,
                            name = user.name,
                            receivedDate = user.createdAt.toDate(), // TODO: format date for display
                            receiverUserId = inviteReceiver,
                            userId = user.userId,
                            userName = user.userName,
                            createdAt = user.createdAt,
                        )
                    )
                }
            }

            CurrentContact::class -> {
                response.data.items.forEach { item ->
                    invites.add(
                        currentContactFrom(
                            contactId = item.id,
                            avatarUri = item.avatarUri,
                            name = item.name,
                            userId = item.userId,
                            userName = item.userName,
                            createdAt = item.createdAt,
                        )
                    )
                }
            }
        }

        return invites
    }

    // TODO: should I keep this?
    private fun currentContactFrom(
        userId: String, contactId: String, userName: String,
        name: String, avatarUri: String, createdAt: Temporal.DateTime,
    ): CurrentContact {
        return CurrentContact(
            contactId = contactId,
            avatarUri = avatarUri,
            name = name,
            userId = userId,
            userName = userName,
            createdAt = createdAt,
        )
    }

    fun currentContactFrom(connectionAccepted: ConnectionAccepted): CurrentContact {
        return CurrentContact(
            contactId = connectionAccepted.userId,
            avatarUri = connectionAccepted.avatarUri,
            name = connectionAccepted.name ?: "",
            userId = connectionAccepted.userId,
            userName = connectionAccepted.userName,
            createdAt = connectionAccepted.createdAt ?: Temporal.DateTime(""),
        )
    }
}