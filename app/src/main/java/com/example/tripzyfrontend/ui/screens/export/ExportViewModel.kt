package com.example.tripzyfrontend.ui.screens.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.usecase.export.DownloadCsvUseCase
import com.example.tripzyfrontend.domain.usecase.export.DownloadPdfUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

sealed interface ExportUiState {
    object Idle : ExportUiState
    object LoadingTour : ExportUiState
    object DownloadingPdf : ExportUiState
    object DownloadingCsv : ExportUiState
    data class TourLoaded(val tour: TourDetail) : ExportUiState
    data class Error(val message: String) : ExportUiState
}

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    private val downloadCsvUseCase: DownloadCsvUseCase,
    private val downloadPdfUseCase: DownloadPdfUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tourId: String = checkNotNull(savedStateHandle["tourId"])

    private val _uiState = MutableStateFlow<ExportUiState>(ExportUiState.LoadingTour)
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    private val _tourDetail = MutableStateFlow<TourDetail?>(null)
    val tourDetail: StateFlow<TourDetail?> = _tourDetail.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    init {
        loadTourInfo()
    }

    fun loadTourInfo() {
        viewModelScope.launch {
            _uiState.value = ExportUiState.LoadingTour
            getTourDetailsUseCase(tourId)
                .onSuccess { tour ->
                    _tourDetail.value = tour
                    _uiState.value = ExportUiState.TourLoaded(tour)
                }
                .onFailure { error ->
                    _uiState.value = ExportUiState.Error(error.localizedMessage ?: "Failed to load tour")
                }
        }
    }

    fun exportPdf(context: Context, onShareUriReady: (Uri) -> Unit) {
        viewModelScope.launch {
            _uiState.value = ExportUiState.DownloadingPdf
            downloadPdfUseCase(tourId)
                .onSuccess { bytes ->
                    val file = saveFile(context, bytes, "tripzy_tour_${tourId}_summary.pdf")
                    val uri = getFileUri(context, file)
                    _uiState.value = ExportUiState.TourLoaded(_tourDetail.value!!)
                    _snackbarEvent.emit("PDF report generated successfully!")
                    onShareUriReady(uri)
                }
                .onFailure { error ->
                    _uiState.value = ExportUiState.TourLoaded(_tourDetail.value!!)
                    _snackbarEvent.emit(error.localizedMessage ?: "Failed to download PDF")
                }
        }
    }

    fun exportCsv(context: Context, onShareUriReady: (Uri) -> Unit) {
        viewModelScope.launch {
            _uiState.value = ExportUiState.DownloadingCsv
            downloadCsvUseCase(tourId)
                .onSuccess { bytes ->
                    val file = saveFile(context, bytes, "tripzy_tour_${tourId}_expenses.csv")
                    val uri = getFileUri(context, file)
                    _uiState.value = ExportUiState.TourLoaded(_tourDetail.value!!)
                    _snackbarEvent.emit("CSV expenses export generated successfully!")
                    onShareUriReady(uri)
                }
                .onFailure { error ->
                    _uiState.value = ExportUiState.TourLoaded(_tourDetail.value!!)
                    _snackbarEvent.emit(error.localizedMessage ?: "Failed to download CSV")
                }
        }
    }

    private fun saveFile(context: Context, bytes: ByteArray, fileName: String): File {
        val cacheDir = File(context.cacheDir, "exports")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val file = File(cacheDir, fileName)
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
