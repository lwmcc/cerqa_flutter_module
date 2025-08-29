package com.mccartycarclub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mccartycarclub.domain.ChannelModel
import com.mccartycarclub.domain.model.LocalContact
import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.domain.websocket.RealTime
import com.mccartycarclub.domain.websocket.RealtimeService
import com.mccartycarclub.repository.LocalRepository
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.repository.realtime.RealtimePublishRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ably.lib.rest.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userContacts: GetContacts,
    private val realTime: RealTime,
    private val repo: RemoteRepo,
    private val localRepo: LocalRepository,
    private val realtimePublishRepo: RealtimePublishRepo,
    private val realtimeService: RealtimeService,
) : ViewModel() {

    private val _token = MutableStateFlow<Auth.TokenRequest?>(null)
    val token: StateFlow<Auth.TokenRequest?> = _token

    private val _localContacts = MutableStateFlow(emptyList<LocalContact>())
    val localContacts = _localContacts.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private var _loggedUserId: String? = null
    val loggedUserId: String?
        get() = _loggedUserId

    fun subscribeToNotifications(channelName: String) {
        viewModelScope.launch {
            realtimePublishRepo.subscribeToInviteNotifications(channelName).catch { error ->
                // TODO: log this
            }.collect { message ->
                message.name
                message.data
                println("MainViewModel ***** NAME ${message.name} DATA ${message.data}")
            }
        }
    }

    fun createReceiverInviteSubscription() {
        realtimePublishRepo.createReceiverInviteSubscription("", "")
    }

    private fun fetchAblyToken(userId: String?) {
        viewModelScope.launch {
            // TODO: connections refused right now over limit from testing, will get back to this
/*            if (userId != null) {
                val ablyRequestToken: Deferred<Auth.TokenRequest?> = async {
                    repo.fetchAblyToken(userId).firstOrNull()
                }

                realTime.initAbly(
                    userId = userId,
                    channelName = channelName(userId),
                    tokenRequest = ablyRequestToken.await(),
                )
            }*/
        }
    }

    fun initAbly() {
        viewModelScope.launch {
            // TODO: when logging in on a different device, userId is null
            // because the app stores userId locally,
            // think about pulling it from AWS
            val id = localRepo.getUserId().first()
            _userId.value = id
            fetchAblyToken(id)
        }
    }

    fun setLoggedInUserId(userId: String) {
        viewModelScope.launch {
            localRepo.setLocalUserId(userId)
            _userId.value = userId
        }
    }

    fun createPrivateChannel(channelName: String) {
        realtimePublishRepo.createPrivateChannel(channelName)
    }

    private fun channelName(userId: String) = ChannelModel.NotificationsDirect.getName(userId)
}
