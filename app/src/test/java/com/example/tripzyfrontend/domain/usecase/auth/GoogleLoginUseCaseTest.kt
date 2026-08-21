package com.example.tripzyfrontend.domain.usecase.auth

import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GoogleLoginUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private lateinit var useCase: GoogleLoginUseCase

    @Before
    fun setUp() {
        useCase = GoogleLoginUseCase(authRepository)
    }

    @Test
    fun `invoke should return user on successful login`() = runTest {
        val user = User(id = "u1", email = "test@example.com", name = "Test User")
        coEvery { authRepository.googleLogin("valid-token") } returns Result.success(user)

        val result = useCase("valid-token")

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify(exactly = 1) { authRepository.googleLogin("valid-token") }
    }

    @Test
    fun `invoke should return failure when token is blank`() = runTest {
        val result = useCase("")

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { authRepository.googleLogin(any()) }
    }
}
