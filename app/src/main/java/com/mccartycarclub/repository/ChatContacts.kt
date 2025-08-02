package com.mccartycarclub.repository

interface ChatContacts {
    suspend fun fetchChats()
    fun fetchGroupsChats()
    fun createMessage()
    fun deleteMessage()
    fun startChat()
}