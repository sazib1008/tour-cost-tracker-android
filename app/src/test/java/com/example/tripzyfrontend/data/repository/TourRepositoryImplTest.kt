package com.example.tripzyfrontend.data.repository

import com.example.tripzyfrontend.data.remote.api.TourApi
import com.example.tripzyfrontend.data.remote.dto.ApiResponse
import com.example.tripzyfrontend.data.remote.dto.CreateTourRequest
import com.example.tripzyfrontend.data.remote.dto.TourDetailDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class TourRepositoryImplTest {

    private val tourApi: TourApi = mockk()
    private lateinit var repository: TourRepositoryImpl

    @Before
    fun setUp() {
        repository = TourRepositoryImpl(tourApi)
    }

    @Test
    fun `createTour updates toursFlow synchronously on OK response`() = runTest {
        val dto = TourDetailDto(
            id = "tour-999",
            title = "New Cox Trip",
            description = "Fun weekend",
            status = "ACTIVE",
            inviteCode = "COX999",
            baseCurrency = "BDT",
            createdBy = "user-1",
            createdAt = "2026-10-09T20:00:00Z",
            archivedAt = null,
            members = emptyList()
        )
        val apiResponse = ApiResponse(success = true, message = "Created", data = dto)
        coEvery { tourApi.createTour(any()) } returns Response.success(apiResponse)

        val result = repository.createTour("New Cox Trip", "Fun weekend", "BDT")

        assertTrue(result.isSuccess)
        val tours = repository.toursFlow.value
        assertNotNull(tours)
        assertEquals(1, tours!!.size)
        assertEquals("tour-999", tours[0].id)
        assertEquals("New Cox Trip", tours[0].title)
    }
}
