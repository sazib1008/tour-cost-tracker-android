package com.example.tripzyfrontend.ui.screens.home

import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.model.TourSummary
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetMyToursUseCase
import com.example.tripzyfrontend.domain.usecase.tour.JoinTourUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class HomeViewModelTest {

    private val getMyToursUseCase: GetMyToursUseCase = mockk()
    private val joinTourUseCase: JoinTourUseCase = mockk()
    private val checkSessionUseCase: CheckSessionUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { checkSessionUseCase.getCurrentUser() } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTours sets uiState to Success when fetching tours succeeds`() = runTest {
        val tours = listOf(
            TourSummary(
                id = "t1",
                title = "Cox's Bazar",
                description = null,
                status = TourStatus.ACTIVE,
                inviteCode = "CXB123",
                baseCurrency = "BDT",
                memberCount = 5,
                createdAt = "2026-08-20"
            )
        )
        coEvery { getMyToursUseCase() } returns Result.success(tours)

        viewModel = HomeViewModel(getMyToursUseCase, joinTourUseCase, checkSessionUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(1, (state as HomeUiState.Success).tours.size)
        assertEquals("Cox's Bazar", (state as HomeUiState.Success).tours[0].title)
    }
}
