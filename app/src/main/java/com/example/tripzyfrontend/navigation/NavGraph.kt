package com.example.tripzyfrontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tripzyfrontend.ui.screens.auth.AuthScreen
import com.example.tripzyfrontend.ui.screens.balance.BalanceScreen
import com.example.tripzyfrontend.ui.screens.expense.add.AddExpenseScreen
import com.example.tripzyfrontend.ui.screens.expense.details.ExpenseDetailsScreen
import com.example.tripzyfrontend.ui.screens.expense.list.ExpensesScreen

import com.example.tripzyfrontend.ui.screens.export.ExportScreen

import com.example.tripzyfrontend.ui.screens.home.HomeScreen
import com.example.tripzyfrontend.ui.screens.profile.ProfileScreen
import com.example.tripzyfrontend.ui.screens.settlement.SettlementScreen
import com.example.tripzyfrontend.ui.screens.splash.SplashScreen
import com.example.tripzyfrontend.ui.screens.tour.JoinTourScreen
import com.example.tripzyfrontend.ui.screens.tour.create.CreateTourScreen
import com.example.tripzyfrontend.ui.screens.tour.dashboard.TourDashboardScreen
import com.example.tripzyfrontend.ui.screens.tour.members.MembersScreen



@Composable
fun TripzyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Auth.route) {
            AuthScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.CreateTour.route) {
            CreateTourScreen(navController = navController)
        }

        composable(Screen.JoinTour.route) {
            JoinTourScreen(navController = navController)
        }

        composable(
            route = Screen.TourDashboard.route,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType }),
            deepLinks = listOf(
                androidx.navigation.navDeepLink { uriPattern = "tripzy://tours/{tourId}" },
                androidx.navigation.navDeepLink { uriPattern = "https://tripzy.app/tours/{tourId}" }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
            TourDashboardScreen(navController = navController, tourId = tourId)
        }

        composable(
            route = Screen.Expenses.route,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
            ExpensesScreen(navController = navController, tourId = tourId)
        }

        composable(
            route = Screen.AddExpense.route,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
            AddExpenseScreen(navController = navController, tourId = tourId)
        }

        composable(
            route = Screen.ExpenseDetails.route,
            arguments = listOf(
                navArgument("tourId") { type = NavType.StringType },
                navArgument("expenseId") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                androidx.navigation.navDeepLink { uriPattern = "tripzy://tours/{tourId}/expenses/{expenseId}" },
                androidx.navigation.navDeepLink { uriPattern = "https://tripzy.app/tours/{tourId}/expenses/{expenseId}" }
            )
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
            val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""
            ExpenseDetailsScreen(navController = navController, tourId = tourId, expenseId = expenseId)
        }

        composable(
            route = Screen.Members.route,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
            MembersScreen(navController = navController, tourId = tourId)
        }

        composable(
            route = Screen.Balance.route,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
            BalanceScreen(navController = navController, tourId = tourId)
        }

        composable(
            route = Screen.Settlement.route,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
            SettlementScreen(navController = navController, tourId = tourId)
        }

        composable(
            route = Screen.Export.route,
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId") ?: ""
            ExportScreen(navController = navController, tourId = tourId)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
