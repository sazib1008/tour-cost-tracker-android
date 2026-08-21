package com.example.tripzyfrontend.domain.usecase.expense

import com.example.tripzyfrontend.data.remote.dto.AllocationDto
import com.example.tripzyfrontend.data.remote.dto.PaymentDto
import com.example.tripzyfrontend.domain.model.Expense
import com.example.tripzyfrontend.domain.repository.ExpenseRepository
import javax.inject.Inject

class GetTourExpensesUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(tourId: String): Result<List<Expense>> = expenseRepository.getTourExpenses(tourId)
}

class CreateExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(
        tourId: String,
        idempotencyKey: String,
        title: String,
        description: String?,
        amountPaisa: Long,
        currency: String = "BDT",
        category: String = "OTHER",
        splitType: String = "EQUAL",
        isPersonal: Boolean = false,
        expenseDate: String? = null,
        payments: List<PaymentDto>? = null,
        participants: List<String>? = null,
        allocations: List<AllocationDto>? = null,
        percentages: Map<String, Double>? = null,
        shares: Map<String, Int>? = null
    ): Result<Expense> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Expense title cannot be empty"))
        }
        if (amountPaisa <= 0) {
            return Result.failure(IllegalArgumentException("Expense amount must be greater than zero"))
        }
        return expenseRepository.createExpense(
            tourId = tourId,
            idempotencyKey = idempotencyKey,
            title = title.trim(),
            description = description?.trim(),
            amountPaisa = amountPaisa,
            currency = currency,
            category = category,
            splitType = splitType,
            isPersonal = isPersonal,
            expenseDate = expenseDate,
            payments = payments,
            participants = participants,
            allocations = allocations,
            percentages = percentages,
            shares = shares
        )
    }
}
