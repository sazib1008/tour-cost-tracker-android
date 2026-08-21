package com.example.tripzyfrontend.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentDto(
    @Json(name = "userId") val userId: String,
    @Json(name = "amount") val amount: Long // in Paisa
)

@JsonClass(generateAdapter = true)
data class AllocationDto(
    @Json(name = "userId") val userId: String,
    @Json(name = "amount") val amount: Long // in Paisa
)

@JsonClass(generateAdapter = true)
data class CreateExpenseRequest(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "amount") val amount: Long, // in Paisa
    @Json(name = "currency") val currency: String = "BDT",
    @Json(name = "category") val category: String = "OTHER",
    @Json(name = "splitType") val splitType: String = "EQUAL",
    @Json(name = "isPersonal") val isPersonal: Boolean = false,
    @Json(name = "expenseDate") val expenseDate: String? = null,
    @Json(name = "payments") val payments: List<PaymentDto>? = null,
    @Json(name = "allocations") val allocations: List<AllocationDto>? = null,
    @Json(name = "participants") val participants: List<String>? = null,
    @Json(name = "percentages") val percentages: Map<String, Double>? = null,
    @Json(name = "shares") val shares: Map<String, Int>? = null,
    @Json(name = "receiptUrl") val receiptUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateExpenseRequest(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "amount") val amount: Long,
    @Json(name = "currency") val currency: String = "BDT",
    @Json(name = "category") val category: String = "OTHER",
    @Json(name = "splitType") val splitType: String = "EQUAL",
    @Json(name = "isPersonal") val isPersonal: Boolean = false,
    @Json(name = "expenseDate") val expenseDate: String? = null,
    @Json(name = "payments") val payments: List<PaymentDto>? = null,
    @Json(name = "allocations") val allocations: List<AllocationDto>? = null,
    @Json(name = "participants") val participants: List<String>? = null,
    @Json(name = "percentages") val percentages: Map<String, Double>? = null,
    @Json(name = "shares") val shares: Map<String, Int>? = null,
    @Json(name = "receiptUrl") val receiptUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class ExpenseDto(
    @Json(name = "id") val id: String,
    @Json(name = "tourId") val tourId: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "amount") val amount: Long,
    @Json(name = "currency") val currency: String,
    @Json(name = "category") val category: String,
    @Json(name = "splitType") val splitType: String,
    @Json(name = "isPersonal") val isPersonal: Boolean,
    @Json(name = "expenseDate") val expenseDate: String,
    @Json(name = "payments") val payments: List<PaymentDto>,
    @Json(name = "allocations") val allocations: List<AllocationDto>,
    @Json(name = "createdBy") val createdBy: String,
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "receiptUrl") val receiptUrl: String?
)

@JsonClass(generateAdapter = true)
data class AddExpenseCommentRequest(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class ExpenseCommentDto(
    @Json(name = "id") val id: String,
    @Json(name = "expenseId") val expenseId: String,
    @Json(name = "tourId") val tourId: String,
    @Json(name = "userId") val userId: String,
    @Json(name = "userName") val userName: String,
    @Json(name = "userAvatarUrl") val userAvatarUrl: String?,
    @Json(name = "text") val text: String,
    @Json(name = "isSystemGenerated") val isSystemGenerated: Boolean,
    @Json(name = "createdAt") val createdAt: String
)
