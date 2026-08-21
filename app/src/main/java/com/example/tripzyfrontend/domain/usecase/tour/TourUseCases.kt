package com.example.tripzyfrontend.domain.usecase.tour

import com.example.tripzyfrontend.domain.model.TourDashboardMetrics
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourSummary
import com.example.tripzyfrontend.domain.repository.BalanceRepository
import com.example.tripzyfrontend.domain.repository.TourRepository
import javax.inject.Inject

class GetMyToursUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(): Result<List<TourSummary>> = tourRepository.getMyTours()
}

class CreateTourUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(title: String, description: String?, baseCurrency: String = "BDT"): Result<TourDetail> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Tour title cannot be empty"))
        }
        return tourRepository.createTour(title.trim(), description?.trim(), baseCurrency.uppercase().ifBlank { "BDT" })
    }
}

class GetTourDetailsUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(tourId: String): Result<TourDetail> = tourRepository.getTourDetails(tourId)
}

class JoinTourUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(inviteCode: String): Result<TourDetail> {
        if (inviteCode.isBlank()) {
            return Result.failure(IllegalArgumentException("Invite code cannot be empty"))
        }
        return tourRepository.joinTour(inviteCode.trim().uppercase())
    }
}

class GetTourDashboardMetricsUseCase @Inject constructor(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(tourId: String): Result<TourDashboardMetrics> = balanceRepository.getDashboardMetrics(tourId)
}

class AddMemberUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(tourId: String, email: String, role: String = "MEMBER"): Result<TourDetail> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        return tourRepository.addMember(tourId, email.trim(), role)
    }
}

class RemoveMemberUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(tourId: String, targetUserId: String): Result<Unit> {
        if (targetUserId.isBlank()) {
            return Result.failure(IllegalArgumentException("Target user ID cannot be empty"))
        }
        return tourRepository.removeMember(tourId, targetUserId)
    }
}
