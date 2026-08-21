package com.example.tripzyfrontend.domain.model

enum class TourStatus {
    ACTIVE,
    SETTLED,
    ARCHIVED;

    val isArchived: Boolean
        get() = this == ARCHIVED

    val isSettled: Boolean
        get() = this == SETTLED
}

enum class TourRole {
    ADMIN,
    MEMBER;

    val isAdmin: Boolean
        get() = this == ADMIN
}

data class TourMember(
    val userId: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val role: TourRole,
    val joinedAt: String
)

data class TourSummary(
    val id: String,
    val title: String,
    val description: String?,
    val status: TourStatus,
    val inviteCode: String,
    val baseCurrency: String,
    val memberCount: Int,
    val createdAt: String
)

data class TourDetail(
    val id: String,
    val title: String,
    val description: String?,
    val status: TourStatus,
    val inviteCode: String,
    val baseCurrency: String,
    val createdBy: String,
    val createdAt: String,
    val archivedAt: String?,
    val members: List<TourMember>
)

data class TourDashboardMetrics(
    val totalSharedCost: Money,
    val yourSharedContribution: Money,
    val yourPersonalExpenses: Money,
    val yourTotalSpending: Money,
    val yourBalance: Money
)
