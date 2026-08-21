package com.example.tripzyfrontend.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object CreateTour : Screen("tours/create")
    object JoinTour : Screen("tours/join")

    object TourDashboard : Screen("tours/{tourId}/dashboard") {
        fun createRoute(tourId: String) = "tours/$tourId/dashboard"
    }

    object Expenses : Screen("tours/{tourId}/expenses") {
        fun createRoute(tourId: String) = "tours/$tourId/expenses"
    }

    object AddExpense : Screen("tours/{tourId}/expenses/add") {
        fun createRoute(tourId: String) = "tours/$tourId/expenses/add"
    }

    object ExpenseDetails : Screen("tours/{tourId}/expenses/{expenseId}") {
        fun createRoute(tourId: String, expenseId: String) = "tours/$tourId/expenses/$expenseId"
    }

    object Members : Screen("tours/{tourId}/members") {
        fun createRoute(tourId: String) = "tours/$tourId/members"
    }

    object Balance : Screen("tours/{tourId}/balance") {
        fun createRoute(tourId: String) = "tours/$tourId/balance"
    }

    object Settlement : Screen("tours/{tourId}/settlements") {
        fun createRoute(tourId: String) = "tours/$tourId/settlements"
    }

    object Export : Screen("tours/{tourId}/export") {
        fun createRoute(tourId: String) = "tours/$tourId/export"
    }

    object Profile : Screen("profile")
}
