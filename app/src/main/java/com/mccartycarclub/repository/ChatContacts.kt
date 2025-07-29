package com.mccartycarclub.repository

interface ChatContacts {
    fun fetchChats()
    fun fetchGroupsChats()
    fun createMessage()
    fun deleteMessage()
    fun startChat()
}