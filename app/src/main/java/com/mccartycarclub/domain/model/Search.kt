package com.mccartycarclub.domain.model

sealed class UserSearchResult() {
    abstract val userId: String
    abstract val userName: String
    abstract val avatarUri: String

    data class User(
        override val userId: String,
        override val userName: String,
        override val avatarUri: String
    ) : UserSearchResult()

    data class SentUser(
        override val userId: String,
        override val userName: String,
        override val avatarUri: String
    ) : UserSearchResult()

    data class ReceivedUser(
        override val userId: String,
        override val userName: String,
        override val avatarUri: String
    ) : UserSearchResult()

    data class ConnectedSearch(
        override val userId: String,
        override val userName: String,
        override val avatarUri: String
    ) : UserSearchResult()
}
