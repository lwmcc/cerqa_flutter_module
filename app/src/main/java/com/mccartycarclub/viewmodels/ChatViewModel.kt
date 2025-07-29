package com.mccartycarclub.viewmodels

import androidx.lifecycle.ViewModel
import com.mccartycarclub.repository.ChatContacts
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatContacts: ChatContacts): ViewModel() {

    fun fetchChats() {}
}