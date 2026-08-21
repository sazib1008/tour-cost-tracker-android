package com.example.tripzyfrontend.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateTourRequest(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "baseCurrency") val baseCurrency: String = "BDT"
)

@JsonClass(generateAdapter = true)
data class UpdateTourRequest(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String? = null
)

@JsonClass(generateAdapter = true)
data class JoinTourRequest(
    @Json(name = "inviteCode") val inviteCode: String
)

@JsonClass(generateAdapter = true)
data class AddMemberRequest(
    @Json(name = "userId") val userId: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "role") val role: String = "MEMBER"
)

@JsonClass(generateAdapter = true)
data class UpdateTourStatusRequest(
    @Json(name = "status") val status: String
)

@JsonClass(generateAdapter = true)
data class TourSummaryDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "status") val status: String,
    @Json(name = "inviteCode") val inviteCode: String,
    @Json(name = "baseCurrency") val baseCurrency: String,
    @Json(name = "memberCount") val memberCount: Int,
    @Json(name = "createdAt") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class TourMemberDetailDto(
    @Json(name = "userId") val userId: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "avatarUrl") val avatarUrl: String?,
    @Json(name = "role") val role: String,
    @Json(name = "joinedAt") val joinedAt: String
)

@JsonClass(generateAdapter = true)
data class TourDetailDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "status") val status: String,
    @Json(name = "inviteCode") val inviteCode: String,
    @Json(name = "baseCurrency") val baseCurrency: String,
    @Json(name = "createdBy") val createdBy: String,
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "archivedAt") val archivedAt: String?,
    @Json(name = "members") val members: List<TourMemberDetailDto>
)
