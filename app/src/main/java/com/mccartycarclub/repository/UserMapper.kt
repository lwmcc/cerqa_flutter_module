package com.mccartycarclub.repository

import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.core.model.LazyModelList
import com.amplifyframework.core.model.LoadedModelList
import com.amplifyframework.core.model.ModelList
import com.amplifyframework.core.model.ModelReference
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import kotlin.reflect.KClass

object UserMapper {

    suspend fun <T : Contact> toUserList(
        inviteReceiver: String,
        response: GraphQLResponse<PaginatedResult<User>>,
        inviteType: KClass<T>,
    ): List<Contact> {

        val invites = mutableListOf<Contact>()

        when (inviteType) {
            SentContactInvite::class -> {

                response.data.items.forEach { item ->
                    invites.add(
                        SentContactInvite(
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
                        CurrentContact(
                            contactId = item.id,
                            avatarUri = item.avatarUri,
                            name = item.name,
                            userId = item.userId,
                            userName = item.userName,
                            createdAt = item.createdAt,
                            senderUserId = item.userId
                        )
                    )
                }
            }
        }

        return invites
    }
}