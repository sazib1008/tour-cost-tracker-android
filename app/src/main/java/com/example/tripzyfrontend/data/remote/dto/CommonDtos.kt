package com.example.tripzyfrontend.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: T? = null,
    @Json(name = "timestamp") val timestamp: String? = null
)

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "status") val status: Int,
    @Json(name = "error") val error: String,
    @Json(name = "message") val message: String,
    @Json(name = "path") val path: String,
    @Json(name = "validationErrors") val validationErrors: Map<String, String>? = null
)
