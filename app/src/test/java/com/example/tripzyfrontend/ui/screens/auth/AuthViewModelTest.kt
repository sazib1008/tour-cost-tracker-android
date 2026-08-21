package com.example.tripzyfrontend.ui.screens.auth

import android.content.Context
import com.example.tripzyfrontend.data.auth.GoogleAuthManager
import com.example.tripzyfrontend.data.auth.GoogleSignInResult
import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.usecase.auth.GoogleLoginUseCase
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
class AuthViewModelTest {

    private val googleLoginUseCase: GoogleLoginUseCase = mockk()
    private val googleAuthManager: GoogleAuthManager = mockk()
    private val mockContext: Context = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(googleLoginUseCase, googleAuthManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `launchSignIn updates uiState to Success when Google Sign-In and backend login succeed`() = runTest {
        val user = User(id = "u1", email = "test@example.com", name = "Test User")
        coEvery { googleAuthManager.signIn(mockContext) } returns GoogleSignInResult.Success(
            idToken = "valid-google-id-token",
            email = "test@example.com",
            displayName = "Test User"
        )
        coEvery { googleLoginUseCase("valid-google-id-token") } returns Result.success(user)

        viewModel.launchSignIn(mockContext)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Success)
        assertEquals(user, (state as AuthUiState.Success).user)
    }

    @Test
    fun `launchSignIn cleanly resets uiState to Idle when user cancels sign-in prompt`() = runTest {
        coEvery { googleAuthManager.signIn(mockContext) } returns GoogleSignInResult.Cancelled

        viewModel.launchSignIn(mockContext)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Idle)
    }

    @Test
    fun `launchSignIn updates uiState to Error when Google Sign-In fails`() = runTest {
        coEvery { googleAuthManager.signIn(mockContext) } returns GoogleSignInResult.Failure(
            error = Exception("No Google accounts on device"),
            userFacingMessage = "No Google accounts available on this device."
        )

        viewModel.launchSignIn(mockContext)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("No Google accounts available on this device.", (state as AuthUiState.Error).message)
    }

    @Test
    fun `signInWithGoogle updates uiState to Success when backend login succeeds`() = runTest {
        val user = User(id = "u1", email = "test@example.com", name = "Test User")
        coEvery { googleLoginUseCase("valid-id-token") } returns Result.success(user)

        viewModel.signInWithGoogle("valid-id-token")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Success)
        assertEquals(user, (state as AuthUiState.Success).user)
    }

    @Test
    fun `signInWithGoogle updates uiState to Error when backend login fails`() = runTest {
        coEvery { googleLoginUseCase("invalid-token") } returns Result.failure(Exception("Invalid Token"))

        viewModel.signInWithGoogle("invalid-token")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("Invalid Token", (state as AuthUiState.Error).message)
    }
}
