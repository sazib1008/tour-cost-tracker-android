package com.example.tripzyfrontend.data.remote.api

import com.example.tripzyfrontend.data.remote.dto.ApiResponse
import com.example.tripzyfrontend.data.remote.dto.AuthResponseData
import com.example.tripzyfrontend.data.remote.dto.GoogleAuthRequest
import com.example.tripzyfrontend.data.remote.dto.RegisterDeviceTokenRequest
import com.example.tripzyfrontend.data.remote.dto.UpdateProfileRequest
import com.example.tripzyfrontend.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {
    @POST("auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): Response<ApiResponse<AuthResponseData>>
}

interface UserApi {
    @GET("users/me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>

    @PUT("users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserDto>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<ApiResponse<UserDto>>

    @POST("users/me/device-tokens")
    suspend fun registerDeviceToken(@Body request: RegisterDeviceTokenRequest): Response<ApiResponse<Unit>>

    @DELETE("users/me/device-tokens/{token}")
    suspend fun deleteDeviceToken(@Path("token") token: String): Response<ApiResponse<Unit>>
}
