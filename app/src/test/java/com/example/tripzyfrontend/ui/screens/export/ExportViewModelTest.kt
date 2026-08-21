package com.example.tripzyfrontend.ui.screens.export

import androidx.lifecycle.SavedStateHandle
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.usecase.export.DownloadCsvUseCase
import com.example.tripzyfrontend.domain.usecase.export.DownloadPdfUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExportViewModelTest {

    private val getTourDetailsUseCase: GetTourDetailsUseCase = mockk()
    private val downloadCsvUseCase: DownloadCsvUseCase = mockk()
    private val downloadPdfUseCase: DownloadPdfUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val tour = TourDetail(
        id = "t1",
        title = "Tour",
        description = null,
        status = TourStatus.ACTIVE,
        inviteCode = "INV01",
        baseCurrency = "BDT",
        createdBy = "u1",
        createdAt = "2026-08-20",
        archivedAt = null,
        members = emptyList()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTourInfo sets state to TourLoaded on success`() = runTest {
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = ExportViewModel(
            getTourDetailsUseCase,
            downloadCsvUseCase,
            downloadPdfUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ExportUiState.TourLoaded)
        assertEquals("Tour", (state as ExportUiState.TourLoaded).tour.title)
    }
}
