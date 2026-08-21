package com.example.tripzyfrontend.ui.screens.balance

import androidx.lifecycle.SavedStateHandle
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.SuggestedTransfer
import com.example.tripzyfrontend.domain.model.TourBalance
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.model.UserBalance
import com.example.tripzyfrontend.domain.usecase.settlement.GetTourBalanceUseCase
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
class BalanceViewModelTest {

    private val getTourBalanceUseCase: GetTourBalanceUseCase = mockk()
    private val getTourDetailsUseCase: GetTourDetailsUseCase = mockk()
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

    private val tourBalance = TourBalance(
        tourId = "t1",
        totalSharedCost = Money.ofPaisa(600000),
        myBalance = UserBalance("u1", "Sazib", Money.ofPaisa(600000), Money.ofPaisa(600000), Money.ZERO, Money.ofPaisa(300000)),
        balances = listOf(
            UserBalance("u1", "Sazib", Money.ofPaisa(600000), Money.ofPaisa(600000), Money.ZERO, Money.ofPaisa(300000)),
            UserBalance("u2", "Tanvir", Money.ZERO, Money.ZERO, Money.ZERO, Money.ofPaisa(-300000))
        ),
        suggestedTransfers = listOf(
            SuggestedTransfer("u2", "Tanvir", "u1", "Sazib", Money.ofPaisa(300000))
        )
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
    fun `loadBalances sets state to Success on valid response`() = runTest {
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery { getTourBalanceUseCase("t1") } returns Result.success(tourBalance)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = BalanceViewModel(getTourBalanceUseCase, getTourDetailsUseCase, savedStateHandle)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BalanceUiState.Success)
        val success = state as BalanceUiState.Success
        assertEquals(Money.ofPaisa(600000), success.balance.totalSharedCost)
        assertEquals(1, success.balance.suggestedTransfers.size)
        assertEquals("Tanvir", success.balance.suggestedTransfers[0].fromUserName)
    }
}
