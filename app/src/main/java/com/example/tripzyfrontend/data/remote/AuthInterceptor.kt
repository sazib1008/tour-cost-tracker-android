package com.example.tripzyfrontend.data.remote

import com.example.tripzyfrontend.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking { sessionManager.getToken() }

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank() && !originalRequest.headers.names().contains("Authorization")) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            // Unauthorized / Token expired -> clear session
            runBlocking { sessionManager.clearSession() }
        }

        return response
    }
}
