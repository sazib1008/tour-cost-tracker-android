package com.example.tripzyfrontend.ui.screens.expense.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.data.remote.dto.AllocationDto
import com.example.tripzyfrontend.data.remote.dto.PaymentDto
import com.example.tripzyfrontend.domain.model.ExpenseCategory
import com.example.tripzyfrontend.domain.model.SplitType
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import com.example.tripzyfrontend.domain.usecase.expense.CreateExpenseUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class ExpenseFlowType {
    SHARED,
    PERSONAL,
    PAID_FOR_SOMEONE
}

sealed interface AddExpenseUiState {
    object Loading : AddExpenseUiState
    data class Content(
        val tour: TourDetail,
        val members: List<TourMember>,
        val isSaving: Boolean = false,
        val errorMessage: String? = null
    ) : AddExpenseUiState
    data class Success(val expenseId: String) : AddExpenseUiState
}

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase,
    checkSessionUseCase: CheckSessionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tourId: String = checkNotNull(savedStateHandle["tourId"])
    val currentUser: StateFlow<User?> = checkSessionUseCase.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow<AddExpenseUiState>(AddExpenseUiState.Loading)
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    // Generated once per add-session and preserved across retries
    private var idempotencyKey: String = UUID.randomUUID().toString()

    init {
        loadTourInfo()
    }

    fun loadTourInfo() {
        viewModelScope.launch {
            _uiState.value = AddExpenseUiState.Loading
            getTourDetailsUseCase(tourId)
                .onSuccess { tour ->
                    _uiState.value = AddExpenseUiState.Content(
                        tour = tour,
                        members = tour.members
                    )
                }
                .onFailure { error ->
                    _uiState.value = AddExpenseUiState.Content(
                        tour = TourDetail(
                            id = tourId,
                            title = "Tour",
                            description = null,
                            status = com.example.tripzyfrontend.domain.model.TourStatus.ACTIVE,
                            inviteCode = "",
                            baseCurrency = "BDT",
                            createdBy = "",
                            createdAt = "",
                            archivedAt = null,
                            members = emptyList()
                        ),
                        members = emptyList(),
                        errorMessage = error.localizedMessage ?: "Failed to load tour"
                    )
                }
        }
    }

    fun submitExpense(
        title: String,
        description: String?,
        amountMajor: Double,
        category: ExpenseCategory,
        flowType: ExpenseFlowType,
        splitType: SplitType,
        selectedPayerId: String,
        selectedParticipantIds: Set<String>,
        paidForTargetUserId: String?,
        exactAmounts: Map<String, Double>?,
        percentages: Map<String, Double>?,
        shares: Map<String, Int>?
    ) {
        val currentState = _uiState.value as? AddExpenseUiState.Content ?: return
        val amountPaisa = (amountMajor * 100).toLong()

        if (title.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Please enter an expense title")
            return
        }
        if (amountPaisa <= 0) {
            _uiState.value = currentState.copy(errorMessage = "Amount must be greater than zero")
            return
        }

        // Validate Split specific constraints
        if (flowType == ExpenseFlowType.SHARED) {
            when (splitType) {
                SplitType.EXACT -> {
                    val totalExactPaisa = exactAmounts?.values?.sumOf { (it * 100).toLong() } ?: 0L
                    if (totalExactPaisa != amountPaisa) {
                        _uiState.value = currentState.copy(errorMessage = "Sum of exact amounts must match total expense ($amountMajor ৳)")
                        return
                    }
                }
                SplitType.PERCENTAGE -> {
                    val totalPercent = percentages?.values?.sum() ?: 0.0
                    if (Math.abs(totalPercent - 100.0) > 0.01) {
                        _uiState.value = currentState.copy(errorMessage = "Sum of percentages must equal 100% (currently ${"%.1f".format(totalPercent)}%)")
                        return
                    }
                }
                SplitType.SHARES -> {
                    val totalShares = shares?.values?.sum() ?: 0
                    if (totalShares <= 0) {
                        _uiState.value = currentState.copy(errorMessage = "Total shares must be greater than zero")
                        return
                    }
                }
                SplitType.EQUAL -> {
                    if (selectedParticipantIds.isEmpty()) {
                        _uiState.value = currentState.copy(errorMessage = "Please select at least one participant")
                        return
                    }
                }
            }
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true, errorMessage = null)

            val isPersonal = (flowType == ExpenseFlowType.PERSONAL)
            val payments = listOf(PaymentDto(userId = selectedPayerId, amount = amountPaisa))

            val participants = when (flowType) {
                ExpenseFlowType.SHARED -> if (splitType == SplitType.EQUAL) selectedParticipantIds.toList() else null
                ExpenseFlowType.PERSONAL -> listOf(selectedPayerId)
                ExpenseFlowType.PAID_FOR_SOMEONE -> null
            }

            val allocations = when {
                flowType == ExpenseFlowType.PAID_FOR_SOMEONE && !paidForTargetUserId.isNullOrBlank() -> {
                    listOf(AllocationDto(userId = paidForTargetUserId, amount = amountPaisa))
                }
                flowType == ExpenseFlowType.SHARED && splitType == SplitType.EXACT && exactAmounts != null -> {
                    exactAmounts.filter { it.value > 0.0 }.map { (userId, amt) ->
                        AllocationDto(userId = userId, amount = (amt * 100).toLong())
                    }
                }
                else -> null
            }

            val requestPercentages = if (flowType == ExpenseFlowType.SHARED && splitType == SplitType.PERCENTAGE) {
                percentages?.filter { it.value > 0.0 }
            } else null

            val requestShares = if (flowType == ExpenseFlowType.SHARED && splitType == SplitType.SHARES) {
                shares?.filter { it.value > 0 }
            } else null

            val effectiveSplitType = when (flowType) {
                ExpenseFlowType.SHARED -> splitType.name
                ExpenseFlowType.PERSONAL -> "EQUAL"
                ExpenseFlowType.PAID_FOR_SOMEONE -> "EXACT"
            }

            createExpenseUseCase(
                tourId = tourId,
                idempotencyKey = idempotencyKey,
                title = title,
                description = description,
                amountPaisa = amountPaisa,
                currency = currentState.tour.baseCurrency,
                category = category.name,
                splitType = effectiveSplitType,
                isPersonal = isPersonal,
                payments = payments,
                participants = participants,
                allocations = allocations,
                percentages = requestPercentages,
                shares = requestShares
            ).onSuccess { created ->
                _uiState.value = AddExpenseUiState.Success(created.id)
            }.onFailure { error ->
                _uiState.value = currentState.copy(
                    isSaving = false,
                    errorMessage = error.localizedMessage ?: "Failed to save expense"
                )
            }
        }
    }
}
