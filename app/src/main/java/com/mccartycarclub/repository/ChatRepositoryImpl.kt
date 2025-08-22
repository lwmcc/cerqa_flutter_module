package com.mccartycarclub.repository

import com.mccartycarclub.pigeon.Chat
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor() : ChatRepository {
    override fun fetchChats(): List<Chat> {
        println("ChatRepositoryImpl ***** fetchChats()")

        // Fake data to tst flutter side
        val chats = mutableListOf<Chat>()
        chats.add(
            Chat(
                userName = "Mlarrym",
                avatarUri = "",
            )
        )

        chats.add(
            Chat(
                userName = "Bron",
                avatarUri = "",
            )
        )

        chats.add(
            Chat(
                userName = "Luka",
                avatarUri = "",
            )
        )
        return chats
    }
}