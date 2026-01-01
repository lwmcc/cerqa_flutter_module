package com.cerqa.viewmodels

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthResult
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.data.Preferences
import com.cerqa.data.UserProfileRepository
import com.cerqa.graphql.CreateUserMutation
import com.cerqa.graphql.HasUserCreatedProfileQuery
import com.cerqa.graphql.type.CreateUserInput
import com.cerqa.models.UserData
import com.cerqa.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isProfileComplete: Boolean = false,
    val username: String? = null,
    val avatarUri: String? = null,
    val email: String? = null
)

class ProfileViewModel(
    private val apolloClient: ApolloClient,
    private val authTokenProvider: AuthTokenProvider,
    private val authRepository: AuthRepository,
    private val preferences: Preferences,
) {
    private val viewModelJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _isProfileComplete = MutableStateFlow<Boolean?>(null)
    val isProfileComplete: StateFlow<Boolean?> = _isProfileComplete.asStateFlow()

    private val _missingFields = MutableStateFlow<List<String>>(emptyList())
    val missingFields: StateFlow<List<String>> = _missingFields.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()


    fun checkProfileComplete() {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Load user data from preferences
                println("ProfileViewModel ===== Loading user data from preferences")
                val savedUserData = preferences.getUserData()
                _userData.value = savedUserData
                println("ProfileViewModel ===== Saved user data: userName=${savedUserData?.userName}, email=${savedUserData?.userEmail}")

                println("ProfileViewModel ===== Getting current user ID from auth provider")
                val userId = authTokenProvider.getCurrentUserId()
                println("ProfileViewModel ===== Current user ID: $userId")

                if (userId != null) {
                    println("ProfileViewModel ===== Querying hasUserCreatedProfile for userId: $userId")
                    val response = apolloClient.query(
                        HasUserCreatedProfileQuery(userId = userId)
                    )
                    .addHttpHeader("x-api-key", "da2-mjgfdw4g6zfv5jgzxsytr4mupa")
                    .execute()

                    println("ProfileViewModel ===== Response received, hasErrors: ${response.hasErrors()}")

                    if (response.hasErrors()) {
                        val errors = response.errors?.joinToString { it.message }
                        println("ProfileViewModel ===== GraphQL Errors: $errors")
                        _error.value = "GraphQL errors: $errors"
                    } else {
                        val data = response.data?.hasUserCreatedProfile
                        _isProfileComplete.value = data?.isProfileComplete
                        _missingFields.value = data?.missingFields?.filterNotNull() ?: emptyList()

                        println("ProfileViewModel ===== isProfileComplete: ${data?.isProfileComplete}")
                        println("ProfileViewModel ===== missingFields: ${data?.missingFields}")
                    }
                } else {
                    println("ProfileViewModel ===== No user ID found - user not authenticated")
                    _error.value = "User not authenticated"
                }
            } catch (e: Exception) {
                println("ProfileViewModel ===== Error calling checkProfileComplete: ${e.message}")
                e.printStackTrace()
                _error.value = e.message ?: "Failed to check profile"
            }

            _isLoading.value = false
        }
    }

    fun createUser(
        userName: String,
        firstName: String,
        lastName: String,
        phone: String,
        email: String
    ) {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val userId = authTokenProvider.getCurrentUserId()
                if (userId == null) {
                    println("ProfileViewModel ===== No user ID found - user not authenticated")
                    _error.value = "User not authenticated"
                    _isLoading.value = false
                    return@launch
                }

                val input = CreateUserInput(
                    userId = com.apollographql.apollo.api.Optional.presentIfNotNull(userId),
                    userName = com.apollographql.apollo.api.Optional.present(userName),
                    firstName = firstName,
                    lastName = lastName,
                    name = com.apollographql.apollo.api.Optional.present("$firstName $lastName"),
                    phone = com.apollographql.apollo.api.Optional.present(phone),
                    email = com.apollographql.apollo.api.Optional.present(email)
                )

                val response = apolloClient.mutation(CreateUserMutation(input)).execute()

                if (response.hasErrors()) {
                    val errors = response.errors?.joinToString { it.message }
                    println("ProfileViewModel ===== GraphQL Errors creating user: $errors")
                    _error.value = "Failed to create user: $errors"
                } else {
                    println("ProfileViewModel ===== User created successfully")
                    // Refresh profile completion status
                    checkProfileComplete()
                }
            } catch (e: Exception) {
                println("ProfileViewModel ===== Error creating user: ${e.message}")
                e.printStackTrace()
                _error.value = e.message ?: "Failed to create user"
            }

            _isLoading.value = false
        }
    }

    fun logout() {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = authRepository.logout()
                when (result) {
                    is AuthResult.Success -> {
                        println("ProfileViewModel ===== User logged out successfully")
                        // Reset profile state
                        _isProfileComplete.value = null
                        _missingFields.value = emptyList()
                    }
                    is AuthResult.Error -> {
                        println("ProfileViewModel ===== Logout failed: ${result.message}")
                        _error.value = result.message
                    }
                    is AuthResult.RequiresConfirmation -> {
                        println("ProfileViewModel ===== Unexpected confirmation required during logout")
                        _error.value = "Unexpected state during logout"
                    }
                }
            } catch (e: Exception) {
                println("ProfileViewModel ===== Error during logout: ${e.message}")
                e.printStackTrace()
                _error.value = e.message ?: "Failed to logout"
            }

            _isLoading.value = false
        }
    }

    fun onCleared() {
        viewModelJob.cancel()
    }
}