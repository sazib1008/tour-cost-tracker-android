package com.example.tripzyfrontend.domain.usecase.auth

import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun isLoggedIn(): Flow<Boolean> = authRepository.isLoggedIn()
    fun getCurrentUser(): Flow<User?> = authRepository.getCurrentUser()
}

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}
