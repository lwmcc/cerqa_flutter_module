package com.mccartycarclub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mccartycarclub.domain.model.LocalContact
import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.domain.websocket.RealTime
import com.mccartycarclub.repository.LocalRepo
import com.mccartycarclub.repository.RemoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userContacts: GetContacts,
    private val realTime: RealTime,
    private val repo: RemoteRepo,
    private val localRepo: LocalRepo,
) : ViewModel() {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _localContacts = MutableStateFlow(emptyList<LocalContact>())
    val localContacts = _localContacts.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private var _loggedUserId: String? = null
    val loggedUserId: String?
        get() = _loggedUserId

    init {
        viewModelScope.launch {
            _userId.value = localRepo.getUserId().first()
        }
    }

    fun getDeviceContacts() = userContacts.getDeviceContacts(localContacts = { contacts ->
        _localContacts.update { contacts }
    })

    fun inviteContact(userId: String, rowId: (String) -> Unit) {
        userContacts.addNewContact(userId, rowId = {
            it?.let {
                rowId(it)
            }
        })
    }

    fun setLoggedInUserId(loggedInUserId: String) {
        _loggedUserId = loggedInUserId
    }

    fun setUserId(userId: String) {
        viewModelScope.launch {
            localRepo.setUserId(userId)
        }
    }

    fun acceptContactInvite() {
        userContacts.acceptContactInvite()
    }

    fun subscribeToNotifications(channelName: String) {
        viewModelScope.launch {
            realTime.subscribeToInviteNotifications(channelName).catch { error ->
                // TODO: log this
            }.collect { message ->
                message.name
                message.data
                println("MainViewModel ***** NAME ${message.name} DATA ${message.data}")
            }
        }
    }

    fun fetchAblyToken(userId: String) {
        viewModelScope.launch {
            repo.fetchAblyToken(userId).collect {
                _token.value = it
            }
        }
    }
}