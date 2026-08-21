package com.example.tripzyfrontend.ui.screens.expense.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.model.Expense
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.usecase.expense.GetTourExpensesUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ExpenseFilter {
    ALL,
    SHARED,
    PERSONAL
}

sealed interface ExpensesUiState {
    object Loading : ExpensesUiState
    data class Success(
        val tour: TourDetail,
        val expenses: List<Expense>,
        val filteredExpenses: List<Expense>,
        val selectedFilter: ExpenseFilter = ExpenseFilter.ALL
    ) : ExpensesUiState
    data class Error(val message: String) : ExpensesUiState
}

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val getTourExpensesUseCase: GetTourExpensesUseCase,
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tourId: String = checkNotNull(savedStateHandle["tourId"])

    private val _uiState = MutableStateFlow<ExpensesUiState>(ExpensesUiState.Loading)
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = ExpensesUiState.Loading

            val tourResult = getTourDetailsUseCase(tourId)
            val expensesResult = getTourExpensesUseCase(tourId)

            if (tourResult.isSuccess && expensesResult.isSuccess) {
                val tour = tourResult.getOrThrow()
                val expenses = expensesResult.getOrThrow()
                _uiState.value = ExpensesUiState.Success(
                    tour = tour,
                    expenses = expenses,
                    filteredExpenses = expenses,
                    selectedFilter = ExpenseFilter.ALL
                )
            } else {
                val error = expensesResult.exceptionOrNull()?.localizedMessage
                    ?: tourResult.exceptionOrNull()?.localizedMessage
                    ?: "Failed to load expenses"
                _uiState.value = ExpensesUiState.Error(error)
            }
        }
    }

    fun setFilter(filter: ExpenseFilter) {
        val currentState = _uiState.value
        if (currentState is ExpensesUiState.Success) {
            val filtered = when (filter) {
                ExpenseFilter.ALL -> currentState.expenses
                ExpenseFilter.SHARED -> currentState.expenses.filter { !it.isPersonal }
                ExpenseFilter.PERSONAL -> currentState.expenses.filter { it.isPersonal }
            }
            _uiState.value = currentState.copy(
                filteredExpenses = filtered,
                selectedFilter = filter
            )
        }
    }
}
