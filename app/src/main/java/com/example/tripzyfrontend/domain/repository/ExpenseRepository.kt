package com.example.tripzyfrontend.domain.repository

import com.example.tripzyfrontend.data.remote.dto.AllocationDto
import com.example.tripzyfrontend.data.remote.dto.ExpenseCommentDto
import com.example.tripzyfrontend.data.remote.dto.PaymentDto
import com.example.tripzyfrontend.domain.model.Expense

import kotlinx.coroutines.flow.StateFlow

interface ExpenseRepository {
    fun getTourExpensesFlow(tourId: String): StateFlow<List<Expense>?>
    suspend fun getTourExpenses(tourId: String): Result<List<Expense>>
    suspend fun getExpenseDetails(tourId: String, expenseId: String): Result<Expense>
    suspend fun createExpense(
        tourId: String,
        idempotencyKey: String,
        title: String,
        description: String?,
        amountPaisa: Long,
        currency: String,
        category: String,
        splitType: String,
        isPersonal: Boolean,
        expenseDate: String?,
        payments: List<PaymentDto>?,
        participants: List<String>?,
        allocations: List<AllocationDto>?,
        percentages: Map<String, Double>? = null,
        shares: Map<String, Int>? = null
    ): Result<Expense>
    suspend fun getExpenseComments(tourId: String, expenseId: String): Result<List<ExpenseCommentDto>>
    suspend fun addExpenseComment(tourId: String, expenseId: String, text: String): Result<ExpenseCommentDto>
}
