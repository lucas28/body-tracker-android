package com.bodyrecomptracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import com.bodyrecomptracker.feature.dashboard.DashboardScreen
import com.bodyrecomptracker.feature.history.HistoryScreen
import com.bodyrecomptracker.feature.onboarding.OnboardingScreen
import com.bodyrecomptracker.feature.settings.SettingsScreen
import com.bodyrecomptracker.feature.workout.WorkoutScreen
import com.bodyrecomptracker.feature.meals.MealsScreen

enum class AppRoute {
	Onboarding,
	Dashboard,
	Meals,
	Workout,
	History,
	Settings
}

private data class BottomItem(
	val route: AppRoute,
	val label: String,
	val icon: @Composable () -> Unit
)

@Composable
fun AppScaffold() {
	val navController = rememberNavController()
	val bottomItems = listOf(
		BottomItem(AppRoute.Dashboard, "Resumo", { Icon(Icons.Filled.Home, contentDescription = null) }),
		BottomItem(AppRoute.Meals, "Refeições", { Icon(Icons.Filled.Fastfood, contentDescription = null) }),
		BottomItem(AppRoute.Workout, "Treino", { Icon(Icons.Filled.FitnessCenter, contentDescription = null) }),
		BottomItem(AppRoute.History, "Histórico", { Icon(Icons.Filled.Assessment, contentDescription = null) }),
		BottomItem(AppRoute.Settings, "Config", { Icon(Icons.Filled.Settings, contentDescription = null) })
	)

	val backStackEntry by navController.currentBackStackEntryAsState()
	val currentDestination = backStackEntry?.destination
	val showBottomBar = currentDestination?.route != AppRoute.Onboarding.name

	Scaffold(
		bottomBar = {
			if (showBottomBar) {
				NavigationBar(containerColor = com.bodyrecomptracker.ui.theme.DarkSurface) {
					bottomItems.forEach { item ->
						val selected = currentDestination.isTopLevel(item.route, navController)
						NavigationBarItem(
							selected = selected,
							onClick = {
								navController.navigate(item.route.name) {
									launchSingleTop = true
									restoreState = true
									popUpTo(navController.graph.startDestinationId) {
										saveState = true
									}
								}
							},
							icon = item.icon,
							label = { androidx.compose.material3.Text(item.label) },
							colors = NavigationBarItemDefaults.colors()
						)
					}
				}
			}
		}
	) { padding ->
		AppNavHost(navController = navController, innerPadding = padding)
	}
}

private fun NavDestination?.isTopLevel(route: AppRoute, navController: NavHostController): Boolean {
	return this?.hierarchy?.any { it.route == route.name } == true
}

@Composable
private fun AppNavHost(
	navController: NavHostController,
	innerPadding: PaddingValues
) {
	NavHost(
		navController = navController,
		startDestination = AppRoute.Onboarding.name,
		modifier = androidx.compose.ui.Modifier.padding(innerPadding)
	) {
		composable(AppRoute.Onboarding.name) {
			OnboardingScreen(onFinished = {
				navController.navigate(AppRoute.Dashboard.name) {
					popUpTo(AppRoute.Onboarding.name) { inclusive = true }
				}
			})
		}
		composable(AppRoute.Dashboard.name) { DashboardScreen() }
		composable(AppRoute.Meals.name) { MealsScreen() }
		composable(AppRoute.Workout.name) { WorkoutScreen(onBack = { /* no-op when tabbed */ }) }
		composable(AppRoute.History.name) { HistoryScreen(onBack = { /* no-op when tabbed */ }) }
		composable(AppRoute.Settings.name) { SettingsScreen(onBack = { /* no-op when tabbed */ }) }
	}
}

