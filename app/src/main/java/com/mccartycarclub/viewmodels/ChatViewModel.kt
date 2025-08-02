package com.mccartycarclub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mccartycarclub.repository.ChatContacts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatContacts: ChatContacts): ViewModel() {

    fun startChat(receiverUserId: String) {
        viewModelScope.launch {
            chatContacts.startChat()
        }
    }

    fun fetchChats() {
        viewModelScope.launch {
            val chats = chatContacts.fetchChats()
            println("ChatViewModel ***** CHATS ${chats}")
        }
    }
}
