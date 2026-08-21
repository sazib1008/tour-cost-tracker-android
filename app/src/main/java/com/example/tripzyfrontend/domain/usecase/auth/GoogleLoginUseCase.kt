package com.example.tripzyfrontend.domain.usecase.auth

import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.repository.AuthRepository
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        if (idToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Google ID token cannot be empty"))
        }
        return authRepository.googleLogin(idToken)
    }
}
