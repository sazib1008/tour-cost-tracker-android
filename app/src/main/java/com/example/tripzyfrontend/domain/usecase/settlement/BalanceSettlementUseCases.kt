package com.example.tripzyfrontend.domain.usecase.settlement

import com.example.tripzyfrontend.domain.model.SettlementTransaction
import com.example.tripzyfrontend.domain.model.TourBalance
import com.example.tripzyfrontend.domain.repository.BalanceRepository
import com.example.tripzyfrontend.domain.repository.SettlementRepository
import javax.inject.Inject

class GetTourBalanceUseCase @Inject constructor(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(tourId: String): Result<TourBalance> = balanceRepository.getTourBalance(tourId)
}

class GetSettlementsUseCase @Inject constructor(
    private val settlementRepository: SettlementRepository
) {
    suspend operator fun invoke(tourId: String): Result<List<SettlementTransaction>> =
        settlementRepository.getSettlements(tourId)
}

class RecordSettlementUseCase @Inject constructor(
    private val settlementRepository: SettlementRepository
) {
    suspend operator fun invoke(
        tourId: String,
        fromUserId: String,
        toUserId: String,
        amountPaisa: Long,
        paymentMethod: String,
        note: String?,
        transactionRef: String?
    ): Result<SettlementTransaction> {
        if (fromUserId.isBlank() || toUserId.isBlank()) {
            return Result.failure(IllegalArgumentException("Debtor and creditor must be selected"))
        }
        if (fromUserId == toUserId) {
            return Result.failure(IllegalArgumentException("Cannot settle debt with oneself"))
        }
        if (amountPaisa <= 0) {
            return Result.failure(IllegalArgumentException("Settlement amount must be greater than zero"))
        }
        return settlementRepository.recordSettlement(
            tourId = tourId,
            fromUserId = fromUserId,
            toUserId = toUserId,
            amountPaisa = amountPaisa,
            paymentMethod = paymentMethod,
            note = note?.trim(),
            transactionRef = transactionRef?.trim()
        )
    }
}
