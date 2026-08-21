package com.example.tripzyfrontend.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.model.TourSummary
import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetMyToursUseCase
import com.example.tripzyfrontend.domain.usecase.tour.JoinTourUseCase
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

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val tours: List<TourSummary>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyToursUseCase: GetMyToursUseCase,
    private val joinTourUseCase: JoinTourUseCase,
    checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    val currentUser: StateFlow<User?> = checkSessionUseCase.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    init {
        loadTours()
    }

    fun loadTours() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getMyToursUseCase()
                .onSuccess { tours ->
                    _uiState.value = HomeUiState.Success(tours)
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(error.localizedMessage ?: "Failed to load tours")
                }
        }
    }

    fun joinTour(inviteCode: String, onJoined: (String) -> Unit) {
        viewModelScope.launch {
            joinTourUseCase(inviteCode)
                .onSuccess { tour ->
                    _snackbarEvent.emit("Successfully joined ${tour.title}!")
                    loadTours()
                    onJoined(tour.id)
                }
                .onFailure { error ->
                    _snackbarEvent.emit(error.localizedMessage ?: "Failed to join tour")
                }
        }
    }
}
