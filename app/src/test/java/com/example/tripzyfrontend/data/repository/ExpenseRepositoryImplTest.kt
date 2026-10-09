package com.example.tripzyfrontend.data.repository

import com.example.tripzyfrontend.data.remote.api.ExpenseApi
import com.example.tripzyfrontend.data.remote.dto.ApiResponse
import com.example.tripzyfrontend.data.remote.dto.ExpenseDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ExpenseRepositoryImplTest {

    private val expenseApi: ExpenseApi = mockk()
    private lateinit var repository: ExpenseRepositoryImpl

    @Before
    fun setUp() {
        repository = ExpenseRepositoryImpl(expenseApi)
    }

    @Test
    fun `createExpense updates getTourExpensesFlow synchronously on OK response`() = runTest {
        val dto = ExpenseDto(
            id = "exp-123",
            tourId = "tour-1",
            title = "Dinner with team",
            description = null,
            amount = 150000L,
            currency = "BDT",
            category = "FOOD",
            splitType = "EQUAL",
            isPersonal = false,
            expenseDate = "2026-10-09T20:00:00Z",
            payments = emptyList(),
            allocations = emptyList(),
            createdBy = "u1",
            createdAt = "2026-10-09T20:00:00Z",
            receiptUrl = null
        )
        val apiResponse = ApiResponse(success = true, message = "Created", data = dto)
        coEvery { expenseApi.createExpense(any(), any(), any()) } returns Response.success(apiResponse)

        val result = repository.createExpense(
            tourId = "tour-1",
            idempotencyKey = "key-1",
            title = "Dinner with team",
            description = null,
            amountPaisa = 150000,
            currency = "BDT",
            category = "FOOD",
            splitType = "EQUAL",
            isPersonal = false,
            expenseDate = null,
            payments = null,
            participants = null,
            allocations = null
        )

        assertTrue(result.isSuccess)
        val expenses = repository.getTourExpensesFlow("tour-1").value
        assertNotNull(expenses)
        assertEquals(1, expenses!!.size)
        assertEquals("exp-123", expenses[0].id)
        assertEquals("Dinner with team", expenses[0].title)
    }
}
