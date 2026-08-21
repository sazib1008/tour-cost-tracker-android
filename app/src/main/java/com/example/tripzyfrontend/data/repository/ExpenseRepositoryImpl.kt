package com.example.tripzyfrontend.data.repository

import com.example.tripzyfrontend.data.remote.api.ExpenseApi
import com.example.tripzyfrontend.data.remote.dto.AddExpenseCommentRequest
import com.example.tripzyfrontend.data.remote.dto.AllocationDto
import com.example.tripzyfrontend.data.remote.dto.CreateExpenseRequest
import com.example.tripzyfrontend.data.remote.dto.ExpenseCommentDto
import com.example.tripzyfrontend.data.remote.dto.ExpenseDto
import com.example.tripzyfrontend.data.remote.dto.PaymentDto
import com.example.tripzyfrontend.domain.model.Expense
import com.example.tripzyfrontend.domain.model.ExpenseAllocation
import com.example.tripzyfrontend.domain.model.ExpenseCategory
import com.example.tripzyfrontend.domain.model.ExpensePayment
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.SplitType
import com.example.tripzyfrontend.domain.repository.ExpenseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseApi: ExpenseApi
) : ExpenseRepository {

    override suspend fun getTourExpenses(tourId: String): Result<List<Expense>> {
        return try {
            val response = expenseApi.getTourExpenses(tourId)
            if (response.isSuccessful && response.body()?.data != null) {
                val expenses = response.body()!!.data!!.map { it.toDomain() }
                Result.success(expenses)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch expenses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getExpenseDetails(tourId: String, expenseId: String): Result<Expense> {
        return try {
            val response = expenseApi.getExpenseDetails(tourId, expenseId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch expense details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createExpense(
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
        percentages: Map<String, Double>?,
        shares: Map<String, Int>?
    ): Result<Expense> {
        return try {
            val request = CreateExpenseRequest(
                title = title,
                description = description,
                amount = amountPaisa,
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
            val response = expenseApi.createExpense(tourId, idempotencyKey, request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create expense"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getExpenseComments(tourId: String, expenseId: String): Result<List<ExpenseCommentDto>> {
        return try {
            val response = expenseApi.getExpenseComments(tourId, expenseId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch comments"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addExpenseComment(tourId: String, expenseId: String, text: String): Result<ExpenseCommentDto> {
        return try {
            val response = expenseApi.addExpenseComment(tourId, expenseId, AddExpenseCommentRequest(text))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to add comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun ExpenseDto.toDomain(): Expense = Expense(
    id = id,
    tourId = tourId,
    title = title,
    description = description,
    amount = Money.ofPaisa(amount, currency),
    category = try { ExpenseCategory.valueOf(category.uppercase()) } catch (e: Exception) { ExpenseCategory.OTHER },
    splitType = try { SplitType.valueOf(splitType.uppercase()) } catch (e: Exception) { SplitType.EQUAL },
    isPersonal = isPersonal,
    expenseDate = expenseDate,
    payments = payments.map { ExpensePayment(it.userId, Money.ofPaisa(it.amount, currency)) },
    allocations = allocations.map { ExpenseAllocation(it.userId, Money.ofPaisa(it.amount, currency)) },
    createdBy = createdBy,
    createdAt = createdAt,
    receiptUrl = receiptUrl
)
