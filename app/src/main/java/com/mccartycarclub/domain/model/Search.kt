package com.mccartycarclub.domain.model

open class UserSearchResult(
    open val rowId: String,
    open val userId: String,
    open val userName: String,
    open val avatarUri: String
)

data class SentInviteToUser(
    override val rowId: String,
    override val userId: String,
    override val userName: String,
    override val avatarUri: String
) : UserSearchResult(rowId, userId, userName, avatarUri)

data class ReceivedInviteFromUser(
    override val rowId: String,
    override val userId: String,
    override val userName: String,
    override val avatarUri: String
) : UserSearchResult(rowId, userId, userName, avatarUri)

data class ConnectedSearch(
    override val rowId: String,
    override val userId: String,
    override val userName: String,
    override val avatarUri: String
) : UserSearchResult(rowId, userId, userName, avatarUri)
