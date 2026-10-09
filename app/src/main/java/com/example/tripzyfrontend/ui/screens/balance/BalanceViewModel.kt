package com.example.tripzyfrontend.ui.screens.balance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.model.TourBalance
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.usecase.settlement.GetTourBalanceUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BalanceUiState {
    object Loading : BalanceUiState
    data class Success(
        val tour: TourDetail,
        val balance: TourBalance
    ) : BalanceUiState
    data class Error(val message: String) : BalanceUiState
}

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val getTourBalanceUseCase: GetTourBalanceUseCase,
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tourId: String = checkNotNull(savedStateHandle["tourId"])

    private val _uiState = MutableStateFlow<BalanceUiState>(BalanceUiState.Loading)
    val uiState: StateFlow<BalanceUiState> = _uiState.asStateFlow()

    init {
        loadBalances()
    }

    fun loadBalances(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent && _uiState.value !is BalanceUiState.Success) {
                _uiState.value = BalanceUiState.Loading
            }

            val tourResult = getTourDetailsUseCase(tourId)
            val balanceResult = getTourBalanceUseCase(tourId)

            if (tourResult.isSuccess && balanceResult.isSuccess) {
                _uiState.value = BalanceUiState.Success(
                    tour = tourResult.getOrThrow(),
                    balance = balanceResult.getOrThrow()
                )
            } else {
                if (_uiState.value !is BalanceUiState.Success) {
                    val error = balanceResult.exceptionOrNull()?.localizedMessage
                        ?: tourResult.exceptionOrNull()?.localizedMessage
                        ?: "Failed to load tour balances"
                    _uiState.value = BalanceUiState.Error(error)
                }
            }
        }
    }
}
