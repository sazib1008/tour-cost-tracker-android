package com.example.tripzyfrontend.data.repository

import com.example.tripzyfrontend.data.local.SessionManager
import com.example.tripzyfrontend.data.remote.api.AuthApi
import com.example.tripzyfrontend.data.remote.dto.GoogleAuthRequest
import com.example.tripzyfrontend.domain.model.User
import com.example.tripzyfrontend.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun googleLogin(idToken: String): Result<User> {
        return try {
            val response = authApi.googleAuth(GoogleAuthRequest(idToken))
            if (response.isSuccessful && response.body()?.data != null) {
                val authData = response.body()!!.data!!
                val userDto = authData.user
                val user = User(
                    id = userDto.id,
                    email = userDto.email,
                    name = userDto.name,
                    avatarUrl = userDto.avatarUrl
                )
                sessionManager.saveSession(
                    token = authData.token,
                    userId = user.id,
                    name = user.name,
                    email = user.email,
                    avatarUrl = user.avatarUrl
                )
                Result.success(user)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Authentication failed with status ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return sessionManager.isLoggedInFlow
    }

    override fun getCurrentUser(): Flow<User?> {
        return combine(
            sessionManager.userIdFlow,
            sessionManager.userNameFlow,
            sessionManager.userEmailFlow,
            sessionManager.userAvatarFlow
        ) { id, name, email, avatar ->
            if (!id.isNullOrBlank() && !email.isNullOrBlank()) {
                User(id = id, email = email, name = name ?: "User", avatarUrl = avatar)
            } else {
                null
            }
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }
}
