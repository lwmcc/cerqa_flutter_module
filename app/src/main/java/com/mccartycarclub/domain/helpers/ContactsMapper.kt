package com.mccartycarclub.domain.helpers

import com.mccartycarclub.repository.Contact as RepoContact
import com.mccartycarclub.pigeon.Contact as PigeonContact

fun toPigeonContact(contacts: List<RepoContact>?): List<PigeonContact> {
    return contacts?.map { contact ->
        PigeonContact(
            userName = contact.userName,
            phoneNumber = contact.phoneNumber,
            userId = contact.userId,
            avatarUri = contact.avatarUri,
        )
    } ?: emptyList()
}