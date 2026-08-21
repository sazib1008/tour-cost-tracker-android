package com.example.tripzyfrontend.domain.repository

interface ExportRepository {
    suspend fun downloadCsv(tourId: String): Result<ByteArray>
    suspend fun downloadPdf(tourId: String): Result<ByteArray>
}
