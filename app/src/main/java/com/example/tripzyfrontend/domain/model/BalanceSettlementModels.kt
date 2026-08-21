package com.example.tripzyfrontend.domain.model

enum class PaymentMethod {
    CASH,
    BKASH,
    NAGAD,
    BANK_TRANSFER
}

data class UserBalance(
    val userId: String,
    val userName: String,
    val totalPaid: Money,
    val sharedPaid: Money,
    val personalSpend: Money,
    val netBalance: Money
)

data class SuggestedTransfer(
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val toUserName: String,
    val amount: Money
)

data class TourBalance(
    val tourId: String,
    val totalSharedCost: Money,
    val myBalance: UserBalance?,
    val balances: List<UserBalance>,
    val suggestedTransfers: List<SuggestedTransfer>
)

data class SettlementTransaction(
    val id: String,
    val tourId: String,
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val toUserName: String,
    val amount: Money,
    val paymentMethod: PaymentMethod,
    val note: String?,
    val transactionRef: String?,
    val settledAt: String,
    val createdBy: String
)
