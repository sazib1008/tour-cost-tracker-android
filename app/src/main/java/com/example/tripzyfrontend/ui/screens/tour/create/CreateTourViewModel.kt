package com.example.tripzyfrontend.ui.screens.tour.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.usecase.tour.CreateTourUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CreateTourUiState {
    object Idle : CreateTourUiState
    object Loading : CreateTourUiState
    data class Success(val tourId: String) : CreateTourUiState
    data class Error(val message: String) : CreateTourUiState
}

@HiltViewModel
class CreateTourViewModel @Inject constructor(
    private val createTourUseCase: CreateTourUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateTourUiState>(CreateTourUiState.Idle)
    val uiState: StateFlow<CreateTourUiState> = _uiState.asStateFlow()

    fun createTour(title: String, description: String?, baseCurrency: String = "BDT") {
        viewModelScope.launch {
            _uiState.value = CreateTourUiState.Loading
            createTourUseCase(title, description, baseCurrency)
                .onSuccess { tour ->
                    _uiState.value = CreateTourUiState.Success(tour.id)
                }
                .onFailure { error ->
                    _uiState.value = CreateTourUiState.Error(error.localizedMessage ?: "Failed to create tour")
                }
        }
    }

    fun resetState() {
        _uiState.value = CreateTourUiState.Idle
    }
}
