package com.mccartycarclub.repository

import com.mccartycarclub.pigeon.Chat
import com.mccartycarclub.pigeon.Contact
import com.mccartycarclub.pigeon.Group
import com.mccartycarclub.pigeon.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun fetchChats(): List<Chat>
    suspend fun fetchDirectMessages(): Flow<List<Message>>

    fun fetchContacts(): List<Contact>
    fun fetchDirectConversation()
    fun fetchGroups(): List<Group>
    fun createMessage(channelId: String?, message: String?, receiverUserId: String): Flow<Boolean>
}