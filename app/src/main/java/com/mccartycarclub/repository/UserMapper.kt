package com.mccartycarclub.repository

import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.ui.components.ConnectionAccepted
import com.mccartycarclub.utils.formatDateTimeForDisplay
import kotlin.reflect.KClass

object UserMapper {

    fun <T : Contact> toUserList(
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
                            createdAt = formatDateTimeForDisplay(item.createdAt).orEmpty(),
                            senderUserId = item.userId,
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
                            userId = user.userId,
                            userName = user.userName,
                            createdAt = formatDateTimeForDisplay(user.createdAt).orEmpty(),
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
                            createdAt = formatDateTimeForDisplay(item.createdAt).orEmpty(),
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
        name: String, avatarUri: String, createdAt: String,
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
            createdAt = connectionAccepted.createdAt,
        )
    }
}
