package com.example.tripzyfrontend.ui.screens.expense.details

import androidx.lifecycle.SavedStateHandle
import com.example.tripzyfrontend.domain.model.Expense
import com.example.tripzyfrontend.domain.model.ExpenseCategory
import com.example.tripzyfrontend.domain.model.ExpenseComment
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.SplitType
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.usecase.expense.AddExpenseCommentUseCase
import com.example.tripzyfrontend.domain.usecase.expense.GetExpenseCommentsUseCase
import com.example.tripzyfrontend.domain.usecase.expense.GetExpenseDetailsUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseDetailsViewModelTest {

    private val getExpenseDetailsUseCase: GetExpenseDetailsUseCase = mockk()
    private val getTourDetailsUseCase: GetTourDetailsUseCase = mockk()
    private val getExpenseCommentsUseCase: GetExpenseCommentsUseCase = mockk()
    private val addExpenseCommentUseCase: AddExpenseCommentUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val tour = TourDetail(
        id = "t1",
        title = "Tour",
        description = null,
        status = TourStatus.ACTIVE,
        inviteCode = "INV01",
        baseCurrency = "BDT",
        createdBy = "u1",
        createdAt = "2026-08-20",
        archivedAt = null,
        members = emptyList()
    )

    private val expense = Expense(
        id = "e1",
        tourId = "t1",
        title = "Dinner at Seagull",
        description = "Fish curry",
        amount = Money.ofPaisa(450000),
        category = ExpenseCategory.FOOD,
        splitType = SplitType.EQUAL,
        isPersonal = false,
        expenseDate = "2026-08-20",
        payments = emptyList(),
        allocations = emptyList(),
        createdBy = "u1",
        createdAt = "2026-08-20",
        receiptUrl = null
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadExpenseDetails sets state to Success on valid response`() = runTest {
        val comments = listOf(
            ExpenseComment(
                id = "c1",
                expenseId = "e1",
                tourId = "t1",
                userId = "u1",
                userName = "Sazib",
                userAvatarUrl = null,
                text = "Is tips included?",
                isSystemGenerated = false,
                createdAt = "2026-08-20"
            )
        )

        coEvery { getExpenseDetailsUseCase("t1", "e1") } returns Result.success(expense)
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery { getExpenseCommentsUseCase("t1", "e1") } returns Result.success(comments)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1", "expenseId" to "e1"))
        val viewModel = ExpenseDetailsViewModel(
            getExpenseDetailsUseCase,
            getTourDetailsUseCase,
            getExpenseCommentsUseCase,
            addExpenseCommentUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ExpenseDetailsUiState.Success)
        val success = state as ExpenseDetailsUiState.Success
        assertEquals("Dinner at Seagull", success.expense.title)
        assertEquals(1, success.comments.size)
        assertEquals("Is tips included?", success.comments[0].text)
    }

    @Test
    fun `addComment appends new comment to state`() = runTest {
        val initialComments = emptyList<ExpenseComment>()
        val newComment = ExpenseComment(
            id = "c2",
            expenseId = "e1",
            tourId = "t1",
            userId = "u1",
            userName = "Sazib",
            userAvatarUrl = null,
            text = "Yes, tips included!",
            isSystemGenerated = false,
            createdAt = "2026-08-20"
        )

        coEvery { getExpenseDetailsUseCase("t1", "e1") } returns Result.success(expense)
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery { getExpenseCommentsUseCase("t1", "e1") } returns Result.success(initialComments)
        coEvery { addExpenseCommentUseCase("t1", "e1", "Yes, tips included!") } returns Result.success(newComment)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1", "expenseId" to "e1"))
        val viewModel = ExpenseDetailsViewModel(
            getExpenseDetailsUseCase,
            getTourDetailsUseCase,
            getExpenseCommentsUseCase,
            addExpenseCommentUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        viewModel.addComment("Yes, tips included!")
        advanceUntilIdle()

        val state = viewModel.uiState.value as ExpenseDetailsUiState.Success
        assertEquals(1, state.comments.size)
        assertEquals("Yes, tips included!", state.comments[0].text)
    }
}
