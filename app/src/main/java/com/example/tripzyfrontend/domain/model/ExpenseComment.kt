package com.example.tripzyfrontend.domain.model

data class ExpenseComment(
    val id: String,
    val expenseId: String,
    val tourId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String?,
    val text: String,
    val isSystemGenerated: Boolean,
    val createdAt: String
)
