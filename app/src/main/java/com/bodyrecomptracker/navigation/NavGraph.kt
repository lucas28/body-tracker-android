package com.bodyrecomptracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bodyrecomptracker.feature.dashboard.DashboardScreen
import com.bodyrecomptracker.feature.history.HistoryScreen
import com.bodyrecomptracker.feature.onboarding.OnboardingScreen
import com.bodyrecomptracker.feature.settings.SettingsScreen
import com.bodyrecomptracker.feature.workout.WorkoutScreen

enum class AppRoute {
	Onboarding,
	Dashboard,
	Workout,
	History,
	Settings
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
	NavHost(
		navController = navController,
		startDestination = AppRoute.Onboarding.name
	) {
		composable(AppRoute.Onboarding.name) {
			OnboardingScreen(onFinished = {
				navController.navigate(AppRoute.Dashboard.name) {
					popUpTo(AppRoute.Onboarding.name) { inclusive = true }
				}
			})
		}
		composable(AppRoute.Dashboard.name) {
			DashboardScreen(
				onLogWorkout = { navController.navigate(AppRoute.Workout.name) },
				onHistory = { navController.navigate(AppRoute.History.name) },
				onSettings = { navController.navigate(AppRoute.Settings.name) }
			)
		}
		composable(AppRoute.Workout.name) { WorkoutScreen(onBack = { navController.popBackStack() }) }
		composable(AppRoute.History.name) { HistoryScreen(onBack = { navController.popBackStack() }) }
		composable(AppRoute.Settings.name) { SettingsScreen(onBack = { navController.popBackStack() }) }
	}
}

