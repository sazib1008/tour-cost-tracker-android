package com.example.tripzyfrontend.data.repository

import com.example.tripzyfrontend.data.remote.api.ExportApi
import com.example.tripzyfrontend.domain.repository.ExportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportRepositoryImpl @Inject constructor(
    private val exportApi: ExportApi
) : ExportRepository {

    override suspend fun downloadCsv(tourId: String): Result<ByteArray> {
        return try {
            val response = exportApi.exportTour(tourId, format = "csv")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.bytes())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to download CSV"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadPdf(tourId: String): Result<ByteArray> {
        return try {
            val response = exportApi.exportTour(tourId, format = "pdf")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.bytes())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to download PDF"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
