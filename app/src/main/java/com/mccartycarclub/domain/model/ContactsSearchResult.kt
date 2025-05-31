package com.mccartycarclub.domain.model

sealed class ContactsSearchResult {
    data class SearchResult(
        val rowId: String,
        val userName: String,
        val avatarUri: String,
    ) : ContactsSearchResult()

    data class PendingSearchResult(
        val rowId: String,
        val userName: String,
        val avatarUri: String,
    ) : ContactsSearchResult()

    data class ConnectionSearchResult(
        val rowId: String,
        val userName: String,
        val avatarUri: String,
    ) : ContactsSearchResult()
}

