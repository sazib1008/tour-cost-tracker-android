package com.example.tripzyfrontend.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import com.example.tripzyfrontend.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    checkSessionUseCase: CheckSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    val currentUser: StateFlow<User?> = checkSessionUseCase.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _autoRemindEnabled = MutableStateFlow(true)
    val autoRemindEnabled: StateFlow<Boolean> = _autoRemindEnabled.asStateFlow()

    private val _pushAlertsEnabled = MutableStateFlow(true)
    val pushAlertsEnabled: StateFlow<Boolean> = _pushAlertsEnabled.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    fun toggleAutoRemind(enabled: Boolean) {
        _autoRemindEnabled.value = enabled
    }

    fun togglePushAlerts(enabled: Boolean) {
        _pushAlertsEnabled.value = enabled
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _logoutEvent.emit(Unit)
        }
    }
}
