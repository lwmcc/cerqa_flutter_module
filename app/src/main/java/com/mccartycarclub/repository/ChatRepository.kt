package com.mccartycarclub.repository

import com.mccartycarclub.pigeon.Chat
import com.mccartycarclub.pigeon.Contact
import com.mccartycarclub.pigeon.Group

interface ChatRepository {
    fun fetchChats(): List<Chat>
    fun fetchContacts(): List<Contact>
    fun fetchDirectConversation()
    fun fetchGroups(): List<Group>
}