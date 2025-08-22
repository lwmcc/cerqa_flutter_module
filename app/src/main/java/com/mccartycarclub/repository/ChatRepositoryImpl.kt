package com.mccartycarclub.repository

import com.mccartycarclub.pigeon.Chat
import com.mccartycarclub.pigeon.Group
import com.mccartycarclub.pigeon.Contact
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor() : ChatRepository {
    override fun fetchChats(): List<Chat> {

        // Fake data to test flutter side
        return mutableListOf(
            Chat(
                userName = "Mlarrym",
                avatarUri = "",
            ),
            Chat(
                userName = "Bron",
                avatarUri = "",
            ),
            Chat(
                userName = "Luka",
                avatarUri = "",
            )
        )
    }

    override fun fetchContacts(): List<Contact> {

        // Fake data to test flutter side
        return mutableListOf(
            Contact(
                userName = "MLarryM",
                phoneNumber = "",
                userId = "11111",
                avatarUri = "",
            ),
            Contact(
                userName = "Bron",
                phoneNumber = "",
                userId = "22222",
                avatarUri = "",
            ),
            Contact(
                userName = "Luka",
                phoneNumber = "",
                userId = "33333",
                avatarUri = "",
            ),
        )
    }

    override fun fetchDirectConversation() {
        TODO("Not yet implemented")
    }

    override fun fetchGroups(): List<Group> {
        // Fake data to tst flutter side
        return mutableListOf(
            Group(
                groudId = "12345",
                groupName = "CarClub",
                groupAvatarUri = "",
            ),
            Group(
                groudId = "12121",
                groupName = "Nav Club",
                groupAvatarUri = "",
            ),
            Group(
                groudId = "12312",
                groupName = "Day Trippers",
                groupAvatarUri = "",
            ),
        )
    }
}