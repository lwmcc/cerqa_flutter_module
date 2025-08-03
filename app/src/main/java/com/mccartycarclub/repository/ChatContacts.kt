package com.mccartycarclub.repository

import com.amplifyframework.datastore.generated.model.UserChannel

interface ChatContacts {
    suspend fun fetchChats(): List<UserChannel>
    fun fetchGroupsChats()
    fun createMessage()
    fun deleteMessage()
    fun startChat()
}