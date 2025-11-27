package com.cerqa.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for authentication UI
 */
class AuthViewModel(
    private val authService: AuthService
) : ViewModel() {

    val authState: StateFlow<AuthState> = authService.authState

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.SignIn)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            authService.initialize()
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = authService.signIn(SignInData(email, password))) {
                is AuthResult.Success -> {
                    // Success - state will update via authState flow
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
                is AuthResult.RequiresConfirmation -> {
                    _uiState.value = AuthUiState.ConfirmSignUp(email)
                }
            }

            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, firstName: String?, lastName: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = authService.signUp(
                SignUpData(email, password, firstName, lastName)
            )) {
                is AuthResult.Success -> {
                    // Success - go to sign in
                    _uiState.value = AuthUiState.SignIn
                }
                is AuthResult.RequiresConfirmation -> {
                    _uiState.value = AuthUiState.ConfirmSignUp(email)
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
            }

            _isLoading.value = false
        }
    }

    fun confirmSignUp(email: String, code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = authService.confirmSignUp(ConfirmationData(email, code))) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState.SignIn
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    fun resendCode(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = authService.resendConfirmationCode(email)) {
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    // Code resent successfully
                }
            }

            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authService.signOut()
        }
    }

    fun showSignUp() {
        _uiState.value = AuthUiState.SignUp
        _errorMessage.value = null
    }

    fun showSignIn() {
        _uiState.value = AuthUiState.SignIn
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

/**
 * UI state for authentication screens
 */
sealed class AuthUiState {
    data object SignIn : AuthUiState()
    data object SignUp : AuthUiState()
    data class ConfirmSignUp(val email: String) : AuthUiState()
}
