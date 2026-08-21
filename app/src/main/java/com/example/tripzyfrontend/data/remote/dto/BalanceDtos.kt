package com.example.tripzyfrontend.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserBalanceDetailDto(
    @Json(name = "userId") val userId: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "avatarUrl") val avatarUrl: String?,
    @Json(name = "totalPaid") val totalPaid: Long,
    @Json(name = "totalResponsible") val totalResponsible: Long,
    @Json(name = "personalSpend") val personalSpend: Long,
    @Json(name = "sharedPaid") val sharedPaid: Long,
    @Json(name = "sharedResponsible") val sharedResponsible: Long,
    @Json(name = "netBalance") val netBalance: Long
)

@JsonClass(generateAdapter = true)
data class SuggestedTransferDto(
    @Json(name = "fromUserId") val fromUserId: String,
    @Json(name = "fromUserName") val fromUserName: String,
    @Json(name = "toUserId") val toUserId: String,
    @Json(name = "toUserName") val toUserName: String,
    @Json(name = "amount") val amount: Long
)

@JsonClass(generateAdapter = true)
data class TourBalanceResponseDto(
    @Json(name = "tourId") val tourId: String,
    @Json(name = "totalCost") val totalCost: Long,
    @Json(name = "totalSharedCost") val totalSharedCost: Long,
    @Json(name = "totalPersonalCost") val totalPersonalCost: Long,
    @Json(name = "myBalance") val myBalance: UserBalanceDetailDto?,
    @Json(name = "memberBalances") val memberBalances: List<UserBalanceDetailDto>,
    @Json(name = "suggestedTransfers") val suggestedTransfers: List<SuggestedTransferDto>
)

@JsonClass(generateAdapter = true)
data class RecordSettlementRequest(
    @Json(name = "toUserId") val toUserId: String,
    @Json(name = "amount") val amount: Long,
    @Json(name = "paymentMethod") val paymentMethod: String = "CASH",
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "referenceId") val referenceId: String? = null,
    @Json(name = "settledAt") val settledAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SettlementDto(
    @Json(name = "id") val id: String,
    @Json(name = "tourId") val tourId: String,
    @Json(name = "fromUserId") val fromUserId: String,
    @Json(name = "fromUserName") val fromUserName: String,
    @Json(name = "fromUserAvatarUrl") val fromUserAvatarUrl: String?,
    @Json(name = "toUserId") val toUserId: String,
    @Json(name = "toUserName") val toUserName: String,
    @Json(name = "toUserAvatarUrl") val toUserAvatarUrl: String?,
    @Json(name = "amount") val amount: Long,
    @Json(name = "paymentMethod") val paymentMethod: String,
    @Json(name = "notes") val notes: String?,
    @Json(name = "referenceId") val referenceId: String?,
    @Json(name = "settledAt") val settledAt: String,
    @Json(name = "createdBy") val createdBy: String,
    @Json(name = "createdAt") val createdAt: String
)
