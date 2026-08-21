package com.example.tripzyfrontend.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashNavigationEvent {
    object NavigateToHome : SplashNavigationEvent()
    object NavigateToAuth : SplashNavigationEvent()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            // Small delay for branding appearance
            delay(800)
            val isLoggedIn = checkSessionUseCase.isLoggedIn().first()
            if (isLoggedIn) {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToHome)
            } else {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToAuth)
            }
        }
    }
}
