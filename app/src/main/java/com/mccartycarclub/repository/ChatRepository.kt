package com.mccartycarclub.repository

import com.mccartycarclub.pigeon.Chat

interface ChatRepository {
    fun fetchChats(): List<Chat>
}