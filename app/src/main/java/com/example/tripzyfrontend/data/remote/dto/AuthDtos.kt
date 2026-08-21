package com.example.tripzyfrontend.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleAuthRequest(
    @Json(name = "idToken") val idToken: String
)

@JsonClass(generateAdapter = true)
data class AuthResponseData(
    @Json(name = "token") val token: String,
    @Json(name = "user") val user: UserDto,
    @Json(name = "expiresInMs") val expiresInMs: Long
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String,
    @Json(name = "name") val name: String,
    @Json(name = "avatarUrl") val avatarUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    @Json(name = "name") val name: String,
    @Json(name = "avatarUrl") val avatarUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class RegisterDeviceTokenRequest(
    @Json(name = "token") val token: String,
    @Json(name = "deviceType") val deviceType: String = "ANDROID"
)
