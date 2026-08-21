package com.example.tripzyfrontend.ui.screens.tour.dashboard

import androidx.lifecycle.SavedStateHandle
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.TourDashboardMetrics
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDashboardMetricsUseCase
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
class TourDashboardViewModelTest {

    private val getTourDetailsUseCase: GetTourDetailsUseCase = mockk()
    private val getTourDashboardMetricsUseCase: GetTourDashboardMetricsUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDashboard sets uiState to Success when both details and metrics load`() = runTest {
        val tour = TourDetail(
            id = "t1",
            title = "Bandarban Trip",
            description = "Camping",
            status = TourStatus.ACTIVE,
            inviteCode = "BND123",
            baseCurrency = "BDT",
            createdBy = "u1",
            createdAt = "2026-08-20",
            archivedAt = null,
            members = emptyList()
        )
        val metrics = TourDashboardMetrics(
            totalSharedCost = Money.ofPaisa(1200000),
            yourSharedContribution = Money.ofPaisa(600000),
            yourPersonalExpenses = Money.ofPaisa(150000),
            yourTotalSpending = Money.ofPaisa(750000),
            yourBalance = Money.ofPaisa(300000)
        )

        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery { getTourDashboardMetricsUseCase("t1") } returns Result.success(metrics)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = TourDashboardViewModel(getTourDetailsUseCase, getTourDashboardMetricsUseCase, savedStateHandle)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is TourDashboardUiState.Success)
        val success = state as TourDashboardUiState.Success
        assertEquals("Bandarban Trip", success.tour.title)
        assertEquals(Money.ofPaisa(1200000), success.metrics.totalSharedCost)
        assertEquals(Money.ofPaisa(300000), success.metrics.yourBalance)
    }
}
