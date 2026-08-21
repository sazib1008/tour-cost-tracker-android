package com.example.tripzyfrontend.data.repository

import com.example.tripzyfrontend.data.remote.api.BalanceApi
import com.example.tripzyfrontend.data.remote.dto.SuggestedTransferDto
import com.example.tripzyfrontend.data.remote.dto.TourBalanceResponseDto
import com.example.tripzyfrontend.data.remote.dto.UserBalanceDetailDto
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.SuggestedTransfer
import com.example.tripzyfrontend.domain.model.TourBalance
import com.example.tripzyfrontend.domain.model.TourDashboardMetrics
import com.example.tripzyfrontend.domain.model.UserBalance
import com.example.tripzyfrontend.domain.repository.BalanceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceRepositoryImpl @Inject constructor(
    private val balanceApi: BalanceApi
) : BalanceRepository {

    override suspend fun getTourBalance(tourId: String): Result<TourBalance> {
        return try {
            val response = balanceApi.getTourBalance(tourId)
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                Result.success(data.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch balances"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDashboardMetrics(tourId: String): Result<TourDashboardMetrics> {
        return try {
            val response = balanceApi.getTourBalance(tourId)
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                val my = data.myBalance

                val metrics = TourDashboardMetrics(
                    totalSharedCost = Money.ofPaisa(data.totalSharedCost),
                    yourSharedContribution = Money.ofPaisa(my?.sharedPaid ?: 0L),
                    yourPersonalExpenses = Money.ofPaisa(my?.personalSpend ?: 0L),
                    yourTotalSpending = Money.ofPaisa(my?.totalPaid ?: 0L),
                    yourBalance = Money.ofPaisa(my?.netBalance ?: 0L)
                )
                Result.success(metrics)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to calculate dashboard metrics"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun TourBalanceResponseDto.toDomain(): TourBalance = TourBalance(
    tourId = tourId,
    totalSharedCost = Money.ofPaisa(totalSharedCost),
    myBalance = myBalance?.toDomain(),
    balances = memberBalances.map { it.toDomain() },
    suggestedTransfers = suggestedTransfers.map { it.toDomain() }
)

private fun UserBalanceDetailDto.toDomain(): UserBalance = UserBalance(
    userId = userId,
    userName = name,
    totalPaid = Money.ofPaisa(totalPaid),
    sharedPaid = Money.ofPaisa(sharedPaid),
    personalSpend = Money.ofPaisa(personalSpend),
    netBalance = Money.ofPaisa(netBalance)
)

private fun SuggestedTransferDto.toDomain(): SuggestedTransfer = SuggestedTransfer(
    fromUserId = fromUserId,
    fromUserName = fromUserName,
    toUserId = toUserId,
    toUserName = toUserName,
    amount = Money.ofPaisa(amount)
)
