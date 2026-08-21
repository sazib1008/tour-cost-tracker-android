package com.example.tripzyfrontend.domain.repository

import com.example.tripzyfrontend.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun googleLogin(idToken: String): Result<User>
    fun isLoggedIn(): Flow<Boolean>
    fun getCurrentUser(): Flow<User?>
    suspend fun logout()
}
