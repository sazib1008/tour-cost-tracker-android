package com.example.tripzyfrontend.domain.usecase.expense

import com.example.tripzyfrontend.domain.model.Expense
import com.example.tripzyfrontend.domain.model.ExpenseCategory
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.SplitType
import com.example.tripzyfrontend.domain.repository.ExpenseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExpenseUseCasesTest {

    private val expenseRepository: ExpenseRepository = mockk()
    private lateinit var getTourExpensesUseCase: GetTourExpensesUseCase
    private lateinit var createExpenseUseCase: CreateExpenseUseCase

    @Before
    fun setUp() {
        getTourExpensesUseCase = GetTourExpensesUseCase(expenseRepository)
        createExpenseUseCase = CreateExpenseUseCase(expenseRepository)
    }

    @Test
    fun `getTourExpenses returns list on success`() = runTest {
        val expenses = listOf(
            Expense(
                id = "e1",
                tourId = "t1",
                title = "Lunch",
                description = null,
                amount = Money.ofPaisa(30000),
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
        )
        coEvery { expenseRepository.getTourExpenses("t1") } returns Result.success(expenses)

        val result = getTourExpensesUseCase("t1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Lunch", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `createExpense returns failure when amount is zero or negative`() = runTest {
        val result = createExpenseUseCase(
            tourId = "t1",
            idempotencyKey = "key-1",
            title = "Zero expense",
            description = null,
            amountPaisa = 0L
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
}
