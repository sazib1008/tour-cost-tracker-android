package com.example.tripzyfrontend.domain.usecase.export

import com.example.tripzyfrontend.domain.repository.ExportRepository
import javax.inject.Inject

class DownloadCsvUseCase @Inject constructor(
    private val exportRepository: ExportRepository
) {
    suspend operator fun invoke(tourId: String): Result<ByteArray> = exportRepository.downloadCsv(tourId)
}

class DownloadPdfUseCase @Inject constructor(
    private val exportRepository: ExportRepository
) {
    suspend operator fun invoke(tourId: String): Result<ByteArray> = exportRepository.downloadPdf(tourId)
}
