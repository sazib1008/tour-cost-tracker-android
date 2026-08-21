package com.example.tripzyfrontend.ui.screens.expense.add

import androidx.lifecycle.SavedStateHandle
import com.example.tripzyfrontend.domain.model.Expense
import com.example.tripzyfrontend.domain.model.ExpenseCategory
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.SplitType
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.model.TourRole
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import com.example.tripzyfrontend.domain.usecase.expense.CreateExpenseUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class AddExpenseViewModelTest {

    private val getTourDetailsUseCase: GetTourDetailsUseCase = mockk()
    private val createExpenseUseCase: CreateExpenseUseCase = mockk()
    private val checkSessionUseCase: CheckSessionUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val member1 = TourMember("u1", "Sazib", "sazib@example.com", null, TourRole.ADMIN, "2026-08-20")
    private val member2 = TourMember("u2", "Tanvir", "tanvir@example.com", null, TourRole.MEMBER, "2026-08-20")

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
        members = listOf(member1, member2)
    )

    private val createdExpense = Expense(
        id = "e100",
        tourId = "t1",
        title = "Dinner",
        description = null,
        amount = Money.ofPaisa(50000),
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
        every { checkSessionUseCase.getCurrentUser() } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitExpense with EQUAL split succeeds`() = runTest {
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery {
            createExpenseUseCase(
                tourId = "t1",
                idempotencyKey = any(),
                title = "Dinner",
                description = any(),
                amountPaisa = 50000L,
                currency = any(),
                category = any(),
                splitType = "EQUAL",
                isPersonal = false,
                expenseDate = any(),
                payments = any(),
                participants = any(),
                allocations = any(),
                percentages = any(),
                shares = any()
            )
        } returns Result.success(createdExpense)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = AddExpenseViewModel(getTourDetailsUseCase, createExpenseUseCase, checkSessionUseCase, savedStateHandle)

        advanceUntilIdle()

        viewModel.submitExpense(
            title = "Dinner",
            description = null,
            amountMajor = 500.0,
            category = ExpenseCategory.FOOD,
            flowType = ExpenseFlowType.SHARED,
            splitType = SplitType.EQUAL,
            selectedPayerId = "u1",
            selectedParticipantIds = setOf("u1", "u2"),
            paidForTargetUserId = null,
            exactAmounts = null,
            percentages = null,
            shares = null
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AddExpenseUiState.Success)
        assertEquals("e100", (state as AddExpenseUiState.Success).expenseId)
    }

    @Test
    fun `submitExpense with EXACT split fails when amounts do not match total`() = runTest {
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = AddExpenseViewModel(getTourDetailsUseCase, createExpenseUseCase, checkSessionUseCase, savedStateHandle)

        advanceUntilIdle()

        viewModel.submitExpense(
            title = "Dinner",
            description = null,
            amountMajor = 500.0,
            category = ExpenseCategory.FOOD,
            flowType = ExpenseFlowType.SHARED,
            splitType = SplitType.EXACT,
            selectedPayerId = "u1",
            selectedParticipantIds = emptySet(),
            paidForTargetUserId = null,
            exactAmounts = mapOf("u1" to 200.0, "u2" to 200.0), // Sum is 400 != 500
            percentages = null,
            shares = null
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value as AddExpenseUiState.Content
        assertTrue(state.errorMessage?.contains("must match total expense") == true)
    }

    @Test
    fun `submitExpense with PERCENTAGE split fails when sum is not 100%`() = runTest {
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = AddExpenseViewModel(getTourDetailsUseCase, createExpenseUseCase, checkSessionUseCase, savedStateHandle)

        advanceUntilIdle()

        viewModel.submitExpense(
            title = "Dinner",
            description = null,
            amountMajor = 500.0,
            category = ExpenseCategory.FOOD,
            flowType = ExpenseFlowType.SHARED,
            splitType = SplitType.PERCENTAGE,
            selectedPayerId = "u1",
            selectedParticipantIds = emptySet(),
            paidForTargetUserId = null,
            exactAmounts = null,
            percentages = mapOf("u1" to 60.0, "u2" to 30.0), // Sum is 90% != 100%
            shares = null
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value as AddExpenseUiState.Content
        assertTrue(state.errorMessage?.contains("must equal 100%") == true)
    }
}
