package com.example.tripzyfrontend.data.repository

import com.example.tripzyfrontend.data.remote.api.SettlementApi
import com.example.tripzyfrontend.data.remote.dto.RecordSettlementRequest
import com.example.tripzyfrontend.data.remote.dto.SettlementDto
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.PaymentMethod
import com.example.tripzyfrontend.domain.model.SettlementTransaction
import com.example.tripzyfrontend.domain.repository.SettlementRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettlementRepositoryImpl @Inject constructor(
    private val settlementApi: SettlementApi
) : SettlementRepository {

    override suspend fun getSettlements(tourId: String): Result<List<SettlementTransaction>> {
        return try {
            val response = settlementApi.getTourSettlements(tourId)
            if (response.isSuccessful && response.body()?.data != null) {
                val list = response.body()!!.data!!.map { it.toDomain() }
                Result.success(list)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch settlements"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun recordSettlement(
        tourId: String,
        fromUserId: String,
        toUserId: String,
        amountPaisa: Long,
        paymentMethod: String,
        note: String?,
        transactionRef: String?
    ): Result<SettlementTransaction> {
        return try {
            val request = RecordSettlementRequest(
                toUserId = toUserId,
                amount = amountPaisa,
                paymentMethod = paymentMethod,
                notes = note,
                referenceId = transactionRef
            )
            val response = settlementApi.recordSettlement(tourId, request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to record settlement"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun SettlementDto.toDomain(): SettlementTransaction = SettlementTransaction(
    id = id,
    tourId = tourId,
    fromUserId = fromUserId,
    fromUserName = fromUserName,
    toUserId = toUserId,
    toUserName = toUserName,
    amount = Money.ofPaisa(amount),
    paymentMethod = try { PaymentMethod.valueOf(paymentMethod.uppercase()) } catch (e: Exception) { PaymentMethod.CASH },
    note = notes,
    transactionRef = referenceId,
    settledAt = settledAt,
    createdBy = createdBy
)
