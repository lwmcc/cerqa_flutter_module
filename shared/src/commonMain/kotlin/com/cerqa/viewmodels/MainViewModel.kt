package com.cerqa.viewmodels

import androidx.lifecycle.viewModelScope
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.data.Preferences
import com.cerqa.data.UserRepository
import com.cerqa.notifications.FcmTokenProvider
import com.cerqa.notifications.Notifications
import com.cerqa.realtime.AblyService
import com.cerqa.realtime.RealtimeChannel
import com.cerqa.repository.AblyRepository
import com.cerqa.repository.NotificationRepository
import com.cerqa.repository.RealtimeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO: too many params, do something about that
class MainViewModel(
    private val authTokenProvider: AuthTokenProvider,
    private val preferences: Preferences,
    private val userRepository: UserRepository,
    private val mainDispatcher: CoroutineDispatcher,
    private val ablyService: AblyService, // TODO: move to repository
    private val notifications: Notifications,
    private val notificationRepository: NotificationRepository,
    private val fcmTokenProvider: FcmTokenProvider,
    private val realtimeRepository: RealtimeRepository,
) {
    private val viewModelScope = CoroutineScope(mainDispatcher)

    // TODO: add to data class above
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _ablyMessages = MutableStateFlow<List<String>>(emptyList())
    val ablyMessages: StateFlow<List<String>> = _ablyMessages.asStateFlow()

    private val _unreadNotificationCount = MutableStateFlow(0)
    val unreadNotificationCount: StateFlow<Int> = _unreadNotificationCount.asStateFlow()

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
                    println("MainViewModel ***** User ID: ${user.userId}")
                    println("MainViewModel ***** User Name: ${user.firstName} ${user.lastName}")
                    println("MainViewModel ***** Username: @${user.userName}")
                    println("MainViewModel ***** Email: ${user.email}")
                    println("MainViewModel ***** Phone: ${user.phone}")
                    println("MainViewModel ***** Avatar: ${user.avatarUri}")

                    // Initialize Ably for realtime features using userId (Cognito ID)
                    user.userId?.let { userId ->
                        initAbly(userId)
                        // Store FCM token after user is authenticated, passing the userId
                        storeFcmToken(userId)
                        // Fetch unread notification count
                        fetchUnreadNotificationCount(userId)
                    }
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
                    println("MainViewModel ***** Ably initialized successfully")

                    // Subscribe to a user-specific channel
                    val channelName = "user:$userId"
                    launch {
                        ablyService.subscribeToChannel(channelName).collect { message ->
                            println("MainViewModel ***** Received message on $channelName: $message")
                            // Handle received message
                        }
                    }

                    // Monitor connection state
                    launch {
                        ablyService.getConnectionState().collect { state ->
                            println("MainViewModel ***** Connection state: $state")
                        }
                    }

                    // Subscribe to DM channel after Ably is initialized
                    subscribeToDmChannel()
                }
                .onFailure { error ->
                    println("MainViewModel ***** Ably initialization failed: ${error.message}")
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
                    println("MainViewModel ***** Message sent successfully")
                }
                .onFailure { error ->
                    println("MainViewModel ***** Failed to send message: ${error.message}")
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
     * @param inviteId The ID of the invite that was created
     */
    fun sendConnectionInvite(
        recipientUserId: String,
        senderName: String,
        senderUserName: String,
        inviteId: String
    ) {
        viewModelScope.launch {
            println("MainViewModel ***** Sending connection invite notification to user: $recipientUserId")
            _isLoading.value = true

            notifications.sendConnectionInviteNotification(
                recipientUserId = recipientUserId,
                senderName = senderName,
                senderUserName = senderUserName,
                inviteId = inviteId
            ).onSuccess {
                println("MainViewModel ***** Connection invite notification sent successfully")
            }.onFailure { error ->
                println("MainViewModel ***** Failed to send connection invite notification: ${error.message}")
                _error.value = error.message
            }

            _isLoading.value = false
        }
    }

    fun storeFcmToken(userId: String) {
        viewModelScope.launch {
            println("MainViewModel ***** Getting FCM token for userId: $userId")

            val token = fcmTokenProvider.getToken()
            if (token == null) {
                println("MainViewModel ***** Failed to get FCM token")
                _error.value = "Failed to get FCM token"
                return@launch
            }

            println("MainViewModel ***** Got FCM token: $token")
            val platform = fcmTokenProvider.getPlatform()
            println("MainViewModel ***** Platform: $platform")

            notificationRepository.storeFcmToken(userId, token, platform)
                .onSuccess { success ->
                    println("MainViewModel ***** FCM token stored successfully: $success")
                }
                .onFailure { error ->
                    println("MainViewModel ***** Failed to store FCM token: ${error.message}")
                    _error.value = error.message
                }
        }
    }

    /**
     * Fetch the unread notification count for the current user.
     * Called automatically after user authentication.
     */
    fun fetchUnreadNotificationCount(userId: String) {
        viewModelScope.launch {
            println("MainViewModel ***** Fetching unread notification count for userId: $userId")

            notificationRepository.getUnreadCount(userId)
                .onSuccess { count ->
                    println("MainViewModel ***** Unread notification count: $count")
                    _unreadNotificationCount.value = count
                }
                .onFailure { error ->
                    println("MainViewModel ***** Failed to fetch unread count: ${error.message}")
                    _error.value = error.message
                }
        }
    }

    fun subscribeToDmChannel() {
        viewModelScope.launch {
            val channel = getInviteChannelPath()

            if (channel != null) {
                realtimeRepository.subscribeToChannel(channel).collect { message ->
                    println("MainViewModel ***** Channel: $channel")
                    println("MainViewModel ***** Message: $message")
                }
            } else {
                println("MainViewModel ***** ERROR CREATING CHANNEL $channel")
            }
        }
    }

    fun sendMessage(channelName: String, message: String) {
        viewModelScope.launch {
            realtimeRepository.publishMessage(channelName, message)
                .onSuccess {
                    println("MainViewModel: Message sent successfully to $channelName")
                }
                .onFailure { error ->
                    println("MainViewModel: Failed to send message: ${error.message}")
                    _error.value = error.message
                }
        }
    }

    private suspend fun getInviteChannelPath(): String? {
        val userId = authTokenProvider.getCurrentUserId()
        return userId?.let { RealtimeChannel.InboxNotifications(it).name }
    }
}