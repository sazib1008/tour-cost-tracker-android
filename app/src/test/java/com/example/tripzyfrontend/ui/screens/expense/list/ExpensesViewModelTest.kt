package com.example.tripzyfrontend.ui.screens.expense.list

import androidx.lifecycle.SavedStateHandle
import com.example.tripzyfrontend.domain.model.Expense
import com.example.tripzyfrontend.domain.model.ExpenseCategory
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.SplitType
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.usecase.expense.GetTourExpensesUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import io.mockk.coEvery
import io.mockk.every
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
class ExpensesViewModelTest {

    private val getTourExpensesUseCase: GetTourExpensesUseCase = mockk()
    private val getTourDetailsUseCase: GetTourDetailsUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val tour = TourDetail(
        id = "t1",
        title = "Sajek Tour",
        description = null,
        status = TourStatus.ACTIVE,
        inviteCode = "SJK01",
        baseCurrency = "BDT",
        createdBy = "u1",
        createdAt = "2026-08-20",
        archivedAt = null,
        members = emptyList()
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
    fun `loadExpenses sets uiState to Success and filtering works correctly`() = runTest {
        val sharedExpense = Expense(
            id = "e1",
            tourId = "t1",
            title = "Resort Booking",
            description = null,
            amount = Money.ofPaisa(800000),
            category = ExpenseCategory.ACCOMMODATION,
            splitType = SplitType.EQUAL,
            isPersonal = false,
            expenseDate = "2026-08-20",
            payments = emptyList(),
            allocations = emptyList(),
            createdBy = "u1",
            createdAt = "2026-08-20",
            receiptUrl = null
        )
        val personalExpense = Expense(
            id = "e2",
            tourId = "t1",
            title = "Personal Souvenirs",
            description = null,
            amount = Money.ofPaisa(150000),
            category = ExpenseCategory.SHOPPING,
            splitType = SplitType.EQUAL,
            isPersonal = true,
            expenseDate = "2026-08-20",
            payments = emptyList(),
            allocations = emptyList(),
            createdBy = "u1",
            createdAt = "2026-08-20",
            receiptUrl = null
        )

        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery { getTourExpensesUseCase("t1") } returns Result.success(listOf(sharedExpense, personalExpense))

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = ExpensesViewModel(getTourExpensesUseCase, getTourDetailsUseCase, savedStateHandle)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ExpensesUiState.Success)
        val success = state as ExpensesUiState.Success
        assertEquals(2, success.expenses.size)

        // Filter Personal
        viewModel.setFilter(ExpenseFilter.PERSONAL)
        val filteredState = viewModel.uiState.value as ExpensesUiState.Success
        assertEquals(1, filteredState.filteredExpenses.size)
        assertEquals("Personal Souvenirs", filteredState.filteredExpenses[0].title)

        // Filter Shared
        viewModel.setFilter(ExpenseFilter.SHARED)
        val sharedState = viewModel.uiState.value as ExpensesUiState.Success
        assertEquals(1, sharedState.filteredExpenses.size)
        assertEquals("Resort Booking", sharedState.filteredExpenses[0].title)
    }

    @Test
    fun `expense flow emission updates uiState reactively`() = runTest {
        val expensesFlow = kotlinx.coroutines.flow.MutableStateFlow<List<Expense>?>(null)
        every { getTourExpensesUseCase.getFlow("t1") } returns expensesFlow
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery { getTourExpensesUseCase("t1") } returns Result.success(emptyList())

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = ExpensesViewModel(getTourExpensesUseCase, getTourDetailsUseCase, savedStateHandle)
        advanceUntilIdle()

        val newExpense = Expense(
            id = "e99",
            tourId = "t1",
            title = "Lunch at Diner",
            description = null,
            amount = Money(50000L, "BDT"),
            category = ExpenseCategory.FOOD,
            splitType = SplitType.EQUAL,
            isPersonal = false,
            expenseDate = "2026-10-09",
            payments = emptyList(),
            allocations = emptyList(),
            createdBy = "u1",
            createdAt = "2026-10-09",
            receiptUrl = null
        )
        expensesFlow.value = listOf(newExpense)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ExpensesUiState.Success)
        val success = state as ExpensesUiState.Success
        assertEquals(1, success.expenses.size)
        assertEquals("Lunch at Diner", success.expenses[0].title)
    }
}
