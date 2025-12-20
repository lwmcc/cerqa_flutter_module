package com.cerqa.viewmodels

import androidx.lifecycle.viewModelScope
import com.cerqa.data.Preferences
import com.cerqa.data.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val preferences: Preferences,
    private val userRepository: UserRepository,
    private val mainDispatcher: CoroutineDispatcher,
) {
    private val viewModelScope = CoroutineScope(mainDispatcher)

    // TODO: add to data class above
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setUserData(
        userId: String,
        userName: String,
        userEmail: String,
        createdAt: String,
        avatarUri: String
    ) {
        viewModelScope.launch {
            preferences.setUserData(
                userId = userId,
                userName = userName,
                userEmail = userEmail,
                createdAt = createdAt,
                avatarUri = avatarUri
            )
        }
    }

    fun getUserData() {
        preferences.getUserData()
    }

    fun fetchUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            userRepository.getUser()
                .onSuccess { user ->
                    println("MainViewModel ***** SUCCESS! Got user data:")
                    println("MainViewModel ***** User ID: ${user.id}")
                    println("MainViewModel ***** User Name: ${user.firstName} ${user.lastName}")
                    println("MainViewModel ***** Username: @${user.userName}")
                    println("MainViewModel ***** Email: ${user.email}")
                    println("MainViewModel ***** Phone: ${user.phone}")
                    println("MainViewModel ***** Avatar: ${user.avatarUri}")
                }
                .onFailure { exception ->
                    println("MainViewModel ***** ERROR: ${exception.message}")
                    _error.value = exception.message
                }

            _isLoading.value = false
        }
    }

    fun initAbly() {
       /* viewModelScope.launch {
            val id = localRepo.getUserId().first()
            _userId.value = id
            fetchAblyToken(id)
        }*/
    }

/*    private fun fetchAblyToken(userId: String?) {
        viewModelScope.launch {
            // TODO: connections refused right now over limit from testing, will get back to this
                        if (userId != null) {
                            val ablyRequestToken: Deferred<Auth.TokenRequest?> = async {
                                repo.fetchAblyToken(userId).firstOrNull()
                            }

                            realTime.initAbly(
                                userId = userId,
                                channelName = channelName(userId),
                                tokenRequest = ablyRequestToken.await(),
                            )
                        }
        }
    }*/
}