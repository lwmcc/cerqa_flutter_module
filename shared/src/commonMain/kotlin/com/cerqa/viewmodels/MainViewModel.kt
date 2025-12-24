package com.cerqa.viewmodels

import androidx.lifecycle.viewModelScope
import com.cerqa.data.Preferences
import com.cerqa.data.UserRepository
import com.cerqa.notifications.Notifications
import com.cerqa.realtime.AblyService
import com.cerqa.repository.AblyRepository
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
    private val ablyService: AblyService,
    private val notifications: Notifications,
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

                    // Initialize Ably for realtime features
                    initAbly(user.id)
                }
                .onFailure { exception ->
                    println("MainViewModel ***** ERROR: ${exception.message}")
                    _error.value = exception.message
                }

            _isLoading.value = false
        }
    }

    /**
     * Initialize Ably for the current user.
     * Called automatically after successful user fetch.
     * Can also be called manually if needed.
     */
    fun initAbly(userId: String) {
        viewModelScope.launch {
            println("MainViewModel: Initializing Ably for user: $userId")

            ablyService.initialize(userId)
                .onSuccess {
                    println("MainViewModel: Ably initialized successfully")

                    // Subscribe to a user-specific channel
                    val channelName = "user:$userId"
                    launch {
                        ablyService.subscribeToChannel(channelName).collect { message ->
                            println("MainViewModel: Received message on $channelName: $message")
                            // Handle received message
                        }
                    }

                    // Subscribe to test channel
                    val testChannelName = "cerqa-test-channel:$userId"
                    launch {
                        ablyService.subscribeToChannel(testChannelName).collect { message ->
                            println("MainViewModel: [TEST CHANNEL] Received: $message")
                            // Handle test channel message
                        }
                    }

                    // Monitor connection state
                    launch {
                        ablyService.getConnectionState().collect { state ->
                            println("MainViewModel: Connection state: $state")
                        }
                    }
                }
                .onFailure { error ->
                    println("MainViewModel: Ably initialization failed: ${error.message}")
                    _error.value = error.message
                }
        }
    }

    /**
     * Send a message via Ably to a specific channel.
     */
    fun sendAblyMessage(channelName: String, message: String) {
        viewModelScope.launch {
            ablyService.publishMessage(channelName, message)
                .onSuccess {
                    println("MainViewModel: Message sent successfully")
                }
                .onFailure { error ->
                    println("MainViewModel: Failed to send message: ${error.message}")
                    _error.value = error.message
                }
        }
    }

    /**
     * Send a connection invite notification to a user.
     * This triggers an FCM push notification on the recipient's device.
     *
     * @param recipientUserId The user ID of the person receiving the invite
     * @param senderName The full name of the person sending the invite
     * @param senderUserName The username of the person sending the invite
     */
    fun sendConnectionInvite(
        recipientUserId: String,
        senderName: String,
        senderUserName: String
    ) {
        viewModelScope.launch {
            println("MainViewModel: Sending connection invite to user: $recipientUserId")
            _isLoading.value = true

            notifications.sendConnectionInviteNotification(
                recipientUserId = recipientUserId,
                senderName = senderName,
                senderUserName = senderUserName
            ).onSuccess {
                println("MainViewModel: Connection invite sent successfully")
            }.onFailure { error ->
                println("MainViewModel: Failed to send connection invite: ${error.message}")
                _error.value = error.message
            }

            _isLoading.value = false
        }
    }
}