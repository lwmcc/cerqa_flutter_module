package com.mccartycarclub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.datastore.generated.model.UserChannel
import com.mccartycarclub.repository.ChatContacts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatContacts: ChatContacts) : ViewModel() {

    private val _chats = MutableStateFlow<List<UserChannel>>(emptyList())
    val chats: StateFlow<List<UserChannel>> = _chats

    fun startChat(receiverUserId: String) {
        viewModelScope.launch {
            chatContacts.startChat()
        }
    }

    fun fetchChats() {
        viewModelScope.launch {
            _chats.value = chatContacts.fetchChats()
        }
    }
}
