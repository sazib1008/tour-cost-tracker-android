package com.example.tripzyfrontend.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.data.auth.GoogleAuthManager
import com.example.tripzyfrontend.data.auth.GoogleSignInResult
import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.usecase.auth.GoogleLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Initiates Google Sign-In via Credential Manager API and authenticates with backend.
     */
    fun launchSignIn(context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = googleAuthManager.signIn(context)) {
                is GoogleSignInResult.Success -> {
                    authenticateWithBackend(result.idToken)
                }
                is GoogleSignInResult.Cancelled -> {
                    _uiState.value = AuthUiState.Idle
                }
                is GoogleSignInResult.Failure -> {
                    _uiState.value = AuthUiState.Error(result.userFacingMessage)
                }
            }
        }
    }

    /**
     * Authenticates a valid Google ID token with our backend session API.
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authenticateWithBackend(idToken)
        }
    }

    private suspend fun authenticateWithBackend(idToken: String) {
        googleLoginUseCase(idToken)
            .onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }
            .onFailure { error ->
                val message = error.localizedMessage ?: "Sign in failed. Please check your connection and try again."
                _uiState.value = AuthUiState.Error(message)
            }
    }

    fun resetError() {
        _uiState.value = AuthUiState.Idle
    }
}
