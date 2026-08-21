package com.example.tripzyfrontend.ui.screens.expense.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.model.Expense
import com.example.tripzyfrontend.domain.model.ExpenseComment
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.usecase.expense.AddExpenseCommentUseCase
import com.example.tripzyfrontend.domain.usecase.expense.GetExpenseCommentsUseCase
import com.example.tripzyfrontend.domain.usecase.expense.GetExpenseDetailsUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ExpenseDetailsUiState {
    object Loading : ExpenseDetailsUiState
    data class Success(
        val expense: Expense,
        val tour: TourDetail,
        val membersMap: Map<String, TourMember>,
        val comments: List<ExpenseComment>,
        val isPostingComment: Boolean = false
    ) : ExpenseDetailsUiState
    data class Error(val message: String) : ExpenseDetailsUiState
}

@HiltViewModel
class ExpenseDetailsViewModel @Inject constructor(
    private val getExpenseDetailsUseCase: GetExpenseDetailsUseCase,
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    private val getExpenseCommentsUseCase: GetExpenseCommentsUseCase,
    private val addExpenseCommentUseCase: AddExpenseCommentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tourId: String = checkNotNull(savedStateHandle["tourId"])
    val expenseId: String = checkNotNull(savedStateHandle["expenseId"])

    private val _uiState = MutableStateFlow<ExpenseDetailsUiState>(ExpenseDetailsUiState.Loading)
    val uiState: StateFlow<ExpenseDetailsUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    init {
        loadExpenseDetails()
    }

    fun loadExpenseDetails() {
        viewModelScope.launch {
            _uiState.value = ExpenseDetailsUiState.Loading

            val expenseResult = getExpenseDetailsUseCase(tourId, expenseId)
            val tourResult = getTourDetailsUseCase(tourId)
            val commentsResult = getExpenseCommentsUseCase(tourId, expenseId)

            if (expenseResult.isSuccess && tourResult.isSuccess) {
                val tour = tourResult.getOrThrow()
                val membersMap = tour.members.associateBy { it.userId }
                val comments = commentsResult.getOrDefault(emptyList())

                _uiState.value = ExpenseDetailsUiState.Success(
                    expense = expenseResult.getOrThrow(),
                    tour = tour,
                    membersMap = membersMap,
                    comments = comments
                )
            } else {
                val error = expenseResult.exceptionOrNull()?.localizedMessage
                    ?: tourResult.exceptionOrNull()?.localizedMessage
                    ?: "Failed to load expense details"
                _uiState.value = ExpenseDetailsUiState.Error(error)
            }
        }
    }

    fun addComment(text: String) {
        val currentState = _uiState.value as? ExpenseDetailsUiState.Success ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isPostingComment = true)
            addExpenseCommentUseCase(tourId, expenseId, text)
                .onSuccess { newComment ->
                    _uiState.value = currentState.copy(
                        isPostingComment = false,
                        comments = currentState.comments + newComment
                    )
                }
                .onFailure { error ->
                    _uiState.value = currentState.copy(isPostingComment = false)
                    _snackbarEvent.emit(error.localizedMessage ?: "Failed to post comment")
                }
        }
    }
}
