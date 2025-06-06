package com.mccartycarclub.domain.usecases.user

import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.core.model.LoadedModelList
import com.amplifyframework.core.model.LoadedModelReference
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.mccartycarclub.domain.model.ReceivedInviteFromUser
import com.mccartycarclub.domain.model.SentInviteToUser
import com.mccartycarclub.domain.model.UserSearchResult

object SearchResultBuilder {

    // TODO: add interface
    fun searchResultOf(
        loggedInUserId: String?,
        user: User?,
        relatedUserData: User,
        response: GraphQLResponse<PaginatedResult<Invite>>
    ): UserSearchResult? {

        var searchUser: UserSearchResult? = null

        val userName = user?.userName ?: ""
        val userId = user?.userId ?: ""
        val userRowId = user?.id ?: ""
        val avatarUri = user?.avatarUri ?: ""
        val contacts =
            (relatedUserData.contacts as? LoadedModelList<UserContact>)?.items ?: emptyList()
        val invites = (relatedUserData.invites as? LoadedModelList<Invite>)?.items ?: emptyList()
        val receivedInviteFromSearchedUser =
            response.data.any { it.receiverId == loggedInUserId && it.senderId == userId }

        if (contacts.isNotEmpty()) {
            // TODO: refactor if not needed, I don't think it is
            println("SearchResultBuilder ***** searchResultOf HAS CONTACTS")
            /*contacts.forEach {
                val u = (it.user as LoadedModelReference<User>)
                println("SearchResultBuilder ***** USERS ARE CONTACTS ${u.value?.userName}")
            }*/
        } else { // Users are not contacts
            if (invites.isNotEmpty()) {
                val sentToSearchResultUser =
                    invites.any { it.receiverId == userId && it.senderId == loggedInUserId }

                val receivedFromSearchResultUser =
                    invites.any { it.receiverId == loggedInUserId && it.senderId == userId }

                if (sentToSearchResultUser) {
                    // TODO: sent to user
                    println("SearchResultBuilder ***** SENT AN INVITE")
                    searchUser = SentInviteToUser(
                        rowId = userRowId,
                        userId = userId,
                        userName = userName,
                        avatarUri = avatarUri,
                    )
                } else if (receivedFromSearchResultUser) {
                    // TODO: received from user
                    println("SearchResultBuilder ***** RECEIVED AN INVITE")
                    searchUser = ReceivedInviteFromUser(
                        rowId = userRowId,
                        userId = userId,
                        userName = userName,
                        avatarUri = avatarUri,
                    )
                }
            } else {
                // In the schema, invites belongs to user. A sent invite will belong to the loggedIdUser
                // If the user invite return empty, then we should also query the invite table to see if
                // loggedInUser has received an invite from the user returned from the search result. This is
                // because if the loggedInUser has received an invite from the search user, then that invite belongs
                // to the searched user and not the loggedInUser
                println("SearchResultBuilder ***** RECEIVED P 2 $receivedInviteFromSearchedUser")
                if (receivedInviteFromSearchedUser) {
                    println("SearchResultBuilder ***** RECEIVED AN INVITE FROM SEARCHED USER")
                    searchUser = ReceivedInviteFromUser(
                        rowId = userRowId,
                        userId = userId,
                        userName = userName,
                        avatarUri = avatarUri,
                    )
                } else {
                    println("SearchResultBuilder ***** REGULAR USER")
                    searchUser = UserSearchResult(
                        rowId = userRowId,
                        userId = userId,
                        userName = userName,
                        avatarUri = avatarUri,
                    )
                }
            }
        }

        return searchUser
    }
}

