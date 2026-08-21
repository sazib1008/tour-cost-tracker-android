package com.example.tripzyfrontend.domain.repository

import com.example.tripzyfrontend.data.remote.dto.TourBalanceResponseDto
import com.example.tripzyfrontend.domain.model.TourDashboardMetrics
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourSummary

interface TourRepository {
    suspend fun getMyTours(): Result<List<TourSummary>>
    suspend fun createTour(title: String, description: String?, baseCurrency: String): Result<TourDetail>
    suspend fun getTourDetails(tourId: String): Result<TourDetail>
    suspend fun joinTour(inviteCode: String): Result<TourDetail>
    suspend fun addMember(tourId: String, email: String, role: String = "MEMBER"): Result<TourDetail>
    suspend fun removeMember(tourId: String, targetUserId: String): Result<Unit>
}

interface BalanceRepository {
    suspend fun getTourBalance(tourId: String): Result<com.example.tripzyfrontend.domain.model.TourBalance>
    suspend fun getDashboardMetrics(tourId: String): Result<TourDashboardMetrics>
}

interface SettlementRepository {
    suspend fun getSettlements(tourId: String): Result<List<com.example.tripzyfrontend.domain.model.SettlementTransaction>>
    suspend fun recordSettlement(
        tourId: String,
        fromUserId: String,
        toUserId: String,
        amountPaisa: Long,
        paymentMethod: String,
        note: String?,
        transactionRef: String?
    ): Result<com.example.tripzyfrontend.domain.model.SettlementTransaction>
}
