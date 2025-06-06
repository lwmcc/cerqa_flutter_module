package com.mccartycarclub.domain.helpers

import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.domain.model.UserSearchResult

interface SearchResult {
    fun searchResultOf(
        loggedInUserId: String?,
        user: User?,
        relatedUserData: User,
        response: GraphQLResponse<PaginatedResult<Invite>>
    ): UserSearchResult?
}