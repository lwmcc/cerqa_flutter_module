package com.mccartycarclub.repository

import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.datastore.generated.model.User

object UserMapper {

    fun toUserList(
        response: GraphQLResponse<PaginatedResult<User>>
    ): List<ReceivedContactInvite> {

        val invites = mutableListOf<ReceivedContactInvite>()

        response.data.items.forEach { item ->
            invites.add(
                ReceivedContactInvite(
                    avatarUri = item.avatarUri,
                    name = item.name,
                    receivedDate = item.createdAt.toDate(),
                    receiverUserId = "",
                    userId = item.userId,
                    userName = item.userName,
                    rowId = item.id,
                )
            )
        }

        return invites
    }
}