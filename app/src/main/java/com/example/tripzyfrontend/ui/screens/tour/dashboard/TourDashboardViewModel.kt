package com.example.tripzyfrontend.ui.screens.tour.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.model.TourDashboardMetrics
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDashboardMetricsUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TourDashboardUiState {
    object Loading : TourDashboardUiState
    data class Success(
        val tour: TourDetail,
        val metrics: TourDashboardMetrics
    ) : TourDashboardUiState
    data class Error(val message: String) : TourDashboardUiState
}

@HiltViewModel
class TourDashboardViewModel @Inject constructor(
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    private val getTourDashboardMetricsUseCase: GetTourDashboardMetricsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tourId: String = checkNotNull(savedStateHandle["tourId"])

    private val _uiState = MutableStateFlow<TourDashboardUiState>(TourDashboardUiState.Loading)
    val uiState: StateFlow<TourDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = TourDashboardUiState.Loading

            val tourResult = getTourDetailsUseCase(tourId)
            val metricsResult = getTourDashboardMetricsUseCase(tourId)

            if (tourResult.isSuccess && metricsResult.isSuccess) {
                _uiState.value = TourDashboardUiState.Success(
                    tour = tourResult.getOrThrow(),
                    metrics = metricsResult.getOrThrow()
                )
            } else {
                val errorMsg = tourResult.exceptionOrNull()?.localizedMessage
                    ?: metricsResult.exceptionOrNull()?.localizedMessage
                    ?: "Failed to load dashboard"
                _uiState.value = TourDashboardUiState.Error(errorMsg)
            }
        }
    }
}
