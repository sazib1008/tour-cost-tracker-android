package com.example.tripzyfrontend.domain.model

enum class ExpenseCategory {
    FOOD,
    TRANSPORT,
    ACCOMMODATION,
    ACTIVITIES,
    SHOPPING,
    SNACKS,
    MEDICAL,
    TICKETS,
    OTHER
}

enum class SplitType {
    EQUAL,
    EXACT,
    PERCENTAGE,
    SHARES
}

data class ExpensePayment(
    val userId: String,
    val amount: Money
)

data class ExpenseAllocation(
    val userId: String,
    val amount: Money
)

data class Expense(
    val id: String,
    val tourId: String,
    val title: String,
    val description: String?,
    val amount: Money,
    val category: ExpenseCategory,
    val splitType: SplitType,
    val isPersonal: Boolean,
    val expenseDate: String,
    val payments: List<ExpensePayment>,
    val allocations: List<ExpenseAllocation>,
    val createdBy: String,
    val createdAt: String,
    val receiptUrl: String?
)
