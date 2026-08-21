package com.example.tripzyfrontend.data.repository

import com.example.tripzyfrontend.data.remote.api.TourApi
import com.example.tripzyfrontend.data.remote.dto.CreateTourRequest
import com.example.tripzyfrontend.data.remote.dto.JoinTourRequest
import com.example.tripzyfrontend.data.remote.dto.TourDetailDto
import com.example.tripzyfrontend.data.remote.dto.TourSummaryDto
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.model.TourRole
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.model.TourSummary
import com.example.tripzyfrontend.domain.repository.TourRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TourRepositoryImpl @Inject constructor(
    private val tourApi: TourApi
) : TourRepository {

    override suspend fun getMyTours(): Result<List<TourSummary>> {
        return try {
            val response = tourApi.getMyTours()
            if (response.isSuccessful && response.body()?.data != null) {
                val summaries = response.body()!!.data!!.map { it.toDomain() }
                Result.success(summaries)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch tours"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTour(title: String, description: String?, baseCurrency: String): Result<TourDetail> {
        return try {
            val response = tourApi.createTour(CreateTourRequest(title, description, baseCurrency))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create tour"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTourDetails(tourId: String): Result<TourDetail> {
        return try {
            val response = tourApi.getTourDetails(tourId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch tour details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinTour(inviteCode: String): Result<TourDetail> {
        return try {
            val response = tourApi.joinTourByCode(JoinTourRequest(inviteCode))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to join tour with code '$inviteCode'"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addMember(tourId: String, email: String, role: String): Result<TourDetail> {
        return try {
            val response = tourApi.addMember(tourId, com.example.tripzyfrontend.data.remote.dto.AddMemberRequest(email = email, role = role))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to add member"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeMember(tourId: String, targetUserId: String): Result<Unit> {
        return try {
            val response = tourApi.removeMember(tourId, targetUserId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to remove member"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun TourSummaryDto.toDomain(): TourSummary = TourSummary(
    id = id,
    title = title,
    description = description,
    status = try { TourStatus.valueOf(status.uppercase()) } catch (e: Exception) { TourStatus.ACTIVE },
    inviteCode = inviteCode,
    baseCurrency = baseCurrency,
    memberCount = memberCount,
    createdAt = createdAt
)

private fun TourDetailDto.toDomain(): TourDetail = TourDetail(
    id = id,
    title = title,
    description = description,
    status = try { TourStatus.valueOf(status.uppercase()) } catch (e: Exception) { TourStatus.ACTIVE },
    inviteCode = inviteCode,
    baseCurrency = baseCurrency,
    createdBy = createdBy,
    createdAt = createdAt,
    archivedAt = archivedAt,
    members = members.map { m ->
        TourMember(
            userId = m.userId,
            name = m.name,
            email = m.email,
            avatarUrl = m.avatarUrl,
            role = try { TourRole.valueOf(m.role.uppercase()) } catch (e: Exception) { TourRole.MEMBER },
            joinedAt = m.joinedAt
        )
    }
)
