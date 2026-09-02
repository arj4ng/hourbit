package com.arj4ng.hourbit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arj4ng.hourbit.ui.history.HistoryScreen
import com.arj4ng.hourbit.ui.home.HomeScreen


/*
  ╔════════════════════════════════════════════════════════╗
  ║  █████╗ ██████╗      ██╗ ██╗  ██╗ ███╗   ██╗  ██████╗  ║
  ║ ██╔══██╗██╔══██╗     ██║ ██║  ██║ ████╗  ██║ ██╔════╝  ║
  ║ ███████║██████╔╝     ██║ ███████║ ██╔██╗ ██║ ██║  ███╗ ║
  ║ ██╔══██║██╔══██╗██   ██║ ╚════██║ ██║╚██╗██║ ██║   ██║ ║
  ║ ██║  ██║██║  ██║╚█████╔╝      ██║ ██║ ╚████║ ╚██████╔╝ ║
  ║ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚════╝       ╚═╝ ╚═╝  ╚═══╝  ╚═════╝  ║
  ╚═════════════════════════════════════════ [ DEV TAG ] ══╝
*/

// central navigation setup for Hourbit
// It connects each type-safe route with the screen that should be displayed
@Composable
fun HourbitNavHost() {

    // remembers the NavController while the Compose UI is active
    // navController manages which screen is currently shown and the back stack
    val navController = rememberNavController()

    // NavHost is the container that displays the current navigation destination
    // Hourbit starts on HomeRoute when the app is opened
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {

        // connects HomeRoute to HomeScreen
        composable<HomeRoute> {
            HomeScreen(
                onOpenHistory = {
                    navController.navigate(HistoryRoute)
                }
            )
        }

        // connects HistoryRoute to HistoryScreen
        composable<HistoryRoute> {
            HistoryScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}