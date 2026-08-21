package com.example.tripzyfrontend.ui.screens.tour.members

import androidx.lifecycle.SavedStateHandle
import com.example.tripzyfrontend.domain.model.TourDetail
import com.example.tripzyfrontend.domain.model.TourMember
import com.example.tripzyfrontend.domain.model.TourRole
import com.example.tripzyfrontend.domain.model.TourStatus
import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.usecase.auth.CheckSessionUseCase
import com.example.tripzyfrontend.domain.usecase.tour.AddMemberUseCase
import com.example.tripzyfrontend.domain.usecase.tour.GetTourDetailsUseCase
import com.example.tripzyfrontend.domain.usecase.tour.RemoveMemberUseCase
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
class MembersViewModelTest {

    private val getTourDetailsUseCase: GetTourDetailsUseCase = mockk()
    private val addMemberUseCase: AddMemberUseCase = mockk()
    private val removeMemberUseCase: RemoveMemberUseCase = mockk()
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

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { checkSessionUseCase.getCurrentUser() } returns flowOf(User("u1", "sazib@example.com", "Sazib"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMembers sets state to Success and detects admin status`() = runTest {
        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = MembersViewModel(
            getTourDetailsUseCase,
            addMemberUseCase,
            removeMemberUseCase,
            checkSessionUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MembersUiState.Success)
        val success = state as MembersUiState.Success
        assertEquals(2, success.members.size)
        assertTrue(success.isAdmin)
    }

    @Test
    fun `inviteMember adds new member to tour successfully`() = runTest {
        val member3 = TourMember("u3", "Rahim", "rahim@example.com", null, TourRole.MEMBER, "2026-08-20")
        val updatedTour = tour.copy(members = listOf(member1, member2, member3))

        coEvery { getTourDetailsUseCase("t1") } returns Result.success(tour)
        coEvery { addMemberUseCase("t1", "rahim@example.com", "MEMBER") } returns Result.success(updatedTour)

        val savedStateHandle = SavedStateHandle(mapOf("tourId" to "t1"))
        val viewModel = MembersViewModel(
            getTourDetailsUseCase,
            addMemberUseCase,
            removeMemberUseCase,
            checkSessionUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        viewModel.inviteMember("rahim@example.com", "MEMBER")
        advanceUntilIdle()

        val state = viewModel.uiState.value as MembersUiState.Success
        assertEquals(3, state.members.size)
    }
}
