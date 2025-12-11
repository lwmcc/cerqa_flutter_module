package com.cerqa.viewmodels

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.data.UserProfileRepository
import com.cerqa.graphql.HasUserCreatedProfileQuery
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
    private val authTokenProvider: AuthTokenProvider
) { // TODO: inject this into repository
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


    fun checkProfileComplete() {
        scope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val userId = authTokenProvider.getCurrentUserId()
                if (userId != null) {
                    val response = apolloClient.query(
                        HasUserCreatedProfileQuery(userId = userId)
                    ).execute()

                    if (response.hasErrors()) {
                        val errors = response.errors?.joinToString { it.message }
                        println("ProfileViewModel ===== GraphQL Errors: $errors")
                        _error.value = "GraphQL errors: $errors"
                    } else {
                        val data = response.data?.hasUserCreatedProfile
                        _isProfileComplete.value = data?.isProfileComplete
                        _missingFields.value = data?.missingFields ?: emptyList()

                        println("ProfileViewModel ===== isProfileComplete: ${data?.isProfileComplete}")
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

    fun onCleared() {
        viewModelJob.cancel()
    }
}