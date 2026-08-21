package com.example.tripzyfrontend.domain.usecase.expense

import com.example.tripzyfrontend.domain.model.Expense
import com.example.tripzyfrontend.domain.model.ExpenseComment
import com.example.tripzyfrontend.domain.repository.ExpenseRepository
import javax.inject.Inject

class GetExpenseDetailsUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(tourId: String, expenseId: String): Result<Expense> =
        expenseRepository.getExpenseDetails(tourId, expenseId)
}

class GetExpenseCommentsUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(tourId: String, expenseId: String): Result<List<ExpenseComment>> {
        return expenseRepository.getExpenseComments(tourId, expenseId).map { dtoList ->
            dtoList.map { dto ->
                ExpenseComment(
                    id = dto.id,
                    expenseId = dto.expenseId,
                    tourId = dto.tourId,
                    userId = dto.userId,
                    userName = dto.userName,
                    userAvatarUrl = dto.userAvatarUrl,
                    text = dto.text,
                    isSystemGenerated = dto.isSystemGenerated,
                    createdAt = dto.createdAt
                )
            }
        }
    }
}

class AddExpenseCommentUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(tourId: String, expenseId: String, text: String): Result<ExpenseComment> {
        if (text.isBlank()) {
            return Result.failure(IllegalArgumentException("Comment cannot be empty"))
        }
        return expenseRepository.addExpenseComment(tourId, expenseId, text.trim()).map { dto ->
            ExpenseComment(
                id = dto.id,
                expenseId = dto.expenseId,
                tourId = dto.tourId,
                userId = dto.userId,
                userName = dto.userName,
                userAvatarUrl = dto.userAvatarUrl,
                text = dto.text,
                isSystemGenerated = dto.isSystemGenerated,
                createdAt = dto.createdAt
            )
        }
    }
}
