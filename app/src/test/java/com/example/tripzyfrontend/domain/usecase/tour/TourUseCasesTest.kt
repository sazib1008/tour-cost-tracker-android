package com.example.tripzyfrontend.domain.usecase.tour

import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.model.TourSummary
import com.example.tripzyfrontend.domain.repository.TourRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TourUseCasesTest {

    private val tourRepository: TourRepository = mockk()
    private lateinit var getMyToursUseCase: GetMyToursUseCase
    private lateinit var createTourUseCase: CreateTourUseCase

    @Before
    fun setUp() {
        getMyToursUseCase = GetMyToursUseCase(tourRepository)
        createTourUseCase = CreateTourUseCase(tourRepository)
    }

    @Test
    fun `getMyTours returns list of tour summaries on success`() = runTest {
        val summaries = listOf(
            TourSummary(
                id = "t1",
                title = "Sajek Trip",
                description = "Weekend getaway",
                status = TourStatus.ACTIVE,
                inviteCode = "SJK123",
                baseCurrency = "BDT",
                memberCount = 4,
                createdAt = "2026-08-20T10:00:00Z"
            )
        )
        coEvery { tourRepository.getMyTours() } returns Result.success(summaries)

        val result = getMyToursUseCase()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Sajek Trip", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `createTour returns failure when title is blank`() = runTest {
        val result = createTourUseCase("", "desc")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
}
