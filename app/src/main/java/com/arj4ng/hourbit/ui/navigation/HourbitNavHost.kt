package com.arj4ng.hourbit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arj4ng.hourbit.ui.history.HistoryScreen
import com.arj4ng.hourbit.ui.home.HomeScreen
import com.arj4ng.hourbit.ui.home.HomeViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arj4ng.hourbit.data.WorkSessionRepository
import com.arj4ng.hourbit.data.local.DatabaseProvider
import com.arj4ng.hourbit.data.location.LocationProvider
import com.arj4ng.hourbit.ui.home.HomeViewModelFactory
import com.arj4ng.hourbit.ui.history.HistoryViewModel
import com.arj4ng.hourbit.ui.history.HistoryViewModelFactory


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

    // remembers the NavController while the compose UI is active
    // navController manages which screen is currently shown and the back stack
    val navController = rememberNavController()

    // provide Android context inside Compose
    val context = LocalContext.current

    // get the reusable Room database instance
    val database = DatabaseProvider.getDatabase(context)

    // repository gets access to WorkSession database operations
    val repository = WorkSessionRepository(
        database.workSessionDao()
    )

    // give LocationProvider access to the android context
    val locationProvider = LocationProvider(context)

    // factory can now create HomeViewModel with the repository
    val factory = HomeViewModelFactory(
        repository,
        locationProvider
    )

    // create or return the HomeViewModel for this screen
    val homeViewModel: HomeViewModel = viewModel(
        factory = factory
    )

    // observe the ViewModel state and converts it into compose state
    val elapsedSeconds by homeViewModel.elapsedSeconds.collectAsStateWithLifecycle() // use my state flow from viewmodel with compose
    // so if state changes.. ui changes automatically
    val isRunning by homeViewModel.isRunning.collectAsStateWithLifecycle()

    // creates HistoryViewModel with the repository
    val historyFactory = HistoryViewModelFactory(repository)

    // gets the HistoryViewModel using the custom factory
    val historyViewModel: HistoryViewModel = viewModel(
        factory = historyFactory
    )

    // observes saved sessions from the ViewModel
    val workSessions by historyViewModel.workSessions.collectAsStateWithLifecycle()



    // NavHost is the container that displays the current navigation destination
    // Hourbit starts on HomeRoute when the app is opened
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {

        // connects HomeRoute to HomeScreen
        composable<HomeRoute> {
            HomeScreen(
                elapsedSeconds = elapsedSeconds,
                isRunning = isRunning,
                onStartTimer = homeViewModel::startTimer, // pass the startTimer function as the callback
                onStopTimer = homeViewModel::stopTimer,
                onResetTimer = homeViewModel::resetTimer,
                onOpenHistory = {
                    navController.navigate(HistoryRoute)
                }
            )
        }


        // connects HistoryRoute to HistoryScreen
        composable<HistoryRoute> {
            HistoryScreen(
                workSessions = workSessions,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}