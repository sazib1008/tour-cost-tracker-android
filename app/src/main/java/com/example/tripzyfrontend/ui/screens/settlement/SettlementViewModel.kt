package com.example.tripzyfrontend.ui.screens.settlement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.model.PaymentMethod
import com.example.tripzyfrontend.domain.model.SettlementTransaction
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import com.example.tripzyfrontend.domain.usecase.settlement.GetSettlementsUseCase
import com.example.tripzyfrontend.domain.usecase.settlement.RecordSettlementUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
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

sealed interface SettlementUiState {
    object Loading : SettlementUiState
    data class Success(
        val tour: TourDetail,
        val settlements: List<SettlementTransaction>,
        val members: List<TourMember>,
        val isRecording: Boolean = false
    ) : SettlementUiState
    data class Error(val message: String) : SettlementUiState
}

@HiltViewModel
class SettlementViewModel @Inject constructor(
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    private val getSettlementsUseCase: GetSettlementsUseCase,
    private val recordSettlementUseCase: RecordSettlementUseCase,
    checkSessionUseCase: CheckSessionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tourId: String = checkNotNull(savedStateHandle["tourId"])
    val currentUser: StateFlow<User?> = checkSessionUseCase.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow<SettlementUiState>(SettlementUiState.Loading)
    val uiState: StateFlow<SettlementUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    init {
        loadSettlements()
    }

    fun loadSettlements(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent && _uiState.value !is SettlementUiState.Success) {
                _uiState.value = SettlementUiState.Loading
            }

            val tourResult = getTourDetailsUseCase(tourId)
            val settlementsResult = getSettlementsUseCase(tourId)

            if (tourResult.isSuccess && settlementsResult.isSuccess) {
                val tour = tourResult.getOrThrow()
                val settlements = settlementsResult.getOrThrow()

                _uiState.value = SettlementUiState.Success(
                    tour = tour,
                    settlements = settlements,
                    members = tour.members
                )
            } else {
                if (_uiState.value !is SettlementUiState.Success) {
                    val error = settlementsResult.exceptionOrNull()?.localizedMessage
                        ?: tourResult.exceptionOrNull()?.localizedMessage
                        ?: "Failed to load settlements"
                    _uiState.value = SettlementUiState.Error(error)
                }
            }
        }
    }

    fun recordSettlement(
        fromUserId: String,
        toUserId: String,
        amountMajor: Double,
        paymentMethod: PaymentMethod,
        note: String?,
        transactionRef: String?,
        onSuccess: () -> Unit
    ) {
        val currentState = _uiState.value as? SettlementUiState.Success ?: return
        val amountPaisa = (amountMajor * 100).toLong()

        if (amountPaisa <= 0) {
            viewModelScope.launch { _snackbarEvent.emit("Amount must be greater than zero") }
            return
        }
        if (fromUserId == toUserId) {
            viewModelScope.launch { _snackbarEvent.emit("Debtor and Creditor must be different members") }
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isRecording = true)
            recordSettlementUseCase(
                tourId = tourId,
                fromUserId = fromUserId,
                toUserId = toUserId,
                amountPaisa = amountPaisa,
                paymentMethod = paymentMethod.name,
                note = note,
                transactionRef = transactionRef
            ).onSuccess {
                _snackbarEvent.emit("Settlement recorded successfully!")
                loadSettlements()
                onSuccess()
            }.onFailure { error ->
                _uiState.value = currentState.copy(isRecording = false)
                _snackbarEvent.emit(error.localizedMessage ?: "Failed to record settlement")
            }
        }
    }
}
