package com.example.tripzyfrontend.ui.screens.settlement

import androidx.lifecycle.SavedStateHandle
import com.example.tripzyfrontend.domain.model.Money
import com.example.tripzyfrontend.domain.model.PaymentMethod
import com.example.tripzyfrontend.domain.model.SettlementTransaction
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.model.TourRole
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import com.example.tripzyfrontend.domain.usecase.settlement.GetSettlementsUseCase
import com.example.tripzyfrontend.domain.usecase.settlement.RecordSettlementUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
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
class SettlementViewModelTest {

    private val getTourDetailsUseCase: GetTourDetailsUseCase = mockk()
    private val getSettlementsUseCase: GetSettlementsUseCase = mockk()
    private val recordSettlementUseCase: RecordSettlementUseCase = mockk()
    private val checkSessionUseCase: CheckSessionUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val member1 = TourMember("u1", "Sazib", "sazib@example.com", null, TourRole.ADMIN, "2026-08-20")
    private val member2 = TourMember("u2", "Tanvir", "tanvir@example.com", null, TourRole.MEMBER, "2026-08-20")

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
        members = listOf(member1, member2)
    )

    private val settlement = SettlementTransaction(
        id = "s1",
        tourId = "t1",
        fromUserId = "u2",
        fromUserName = "Tanvir",
        toUserId = "u1",
        toUserName = "Sazib",
        amount = Money.ofPaisa(300000),
        paymentMethod = PaymentMethod.BKASH,
        note = "Settled Sajek trip share",
        transactionRef = "TRX9988",
        settledAt = "2026-08-20",
        createdBy = "u2"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { checkSessionUseCase.getCurrentUser() } returns flowOf(User("u2", "tanvir@example.com", "Tanvir"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSettlements sets state to Success on valid response`() = runTest {
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery { getSettlementsUseCase("t1") } returns Result.success(listOf(settlement))

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = SettlementViewModel(
            getTourDetailsUseCase,
            getSettlementsUseCase,
            recordSettlementUseCase,
            checkSessionUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is SettlementUiState.Success)
        val success = state as SettlementUiState.Success
        assertEquals(1, success.settlements.size)
        assertEquals(PaymentMethod.BKASH, success.settlements[0].paymentMethod)
    }

    @Test
    fun `recordSettlement successfully invokes usecase and refreshes list`() = runTest {
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery { getSettlementsUseCase("t1") } returns Result.success(emptyList())
        coEvery {
            recordSettlementUseCase(
                tourId = "t1",
                fromUserId = "u2",
                toUserId = "u1",
                amountPaisa = 300000L,
                paymentMethod = "BKASH",
                note = "settled",
                transactionRef = "REF123"
            )
        } returns Result.success(settlement)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = SettlementViewModel(
            getTourDetailsUseCase,
            getSettlementsUseCase,
            recordSettlementUseCase,
            checkSessionUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        var successCallbackCalled = false
        viewModel.recordSettlement("u2", "u1", 3000.0, PaymentMethod.BKASH, "settled", "REF123") {
            successCallbackCalled = true
        }

        advanceUntilIdle()

        assertTrue(successCallbackCalled)
    }
}
