package com.arj4ng.hourbit.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arj4ng.hourbit.data.WorkSessionRepository
import com.arj4ng.hourbit.data.local.DatabaseProvider
import com.arj4ng.hourbit.data.local.SessionStateStore
import com.arj4ng.hourbit.data.location.LocationProvider
import com.arj4ng.hourbit.data.remote.RetrofitClient
import com.arj4ng.hourbit.ui.history.HistoryScreen
import com.arj4ng.hourbit.ui.history.HistoryViewModel
import com.arj4ng.hourbit.ui.history.HistoryViewModelFactory
import com.arj4ng.hourbit.ui.home.HomeScreen
import com.arj4ng.hourbit.ui.home.HomeViewModel
import com.arj4ng.hourbit.ui.home.HomeViewModelFactory

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

@Composable
fun HourbitNavHost() {

    val navController = rememberNavController()

    val context = LocalContext.current

    val database =
        DatabaseProvider.getDatabase(context)

    val repository =
        WorkSessionRepository(
            database.workSessionDao(),
            RetrofitClient.weatherApi
        )

    val locationProvider =
        LocationProvider(context)

    val sessionStateStore =
        SessionStateStore(context)

    val homeFactory =
        HomeViewModelFactory(
            repository,
            locationProvider,
            sessionStateStore
        )

    val homeViewModel: HomeViewModel =
        viewModel(
            factory = homeFactory
        )

    val elapsedSeconds by
        homeViewModel.elapsedSeconds
            .collectAsStateWithLifecycle()

    val isRunning by
        homeViewModel.isRunning
            .collectAsStateWithLifecycle()

    val isOnBreak by
        homeViewModel.isOnBreak
            .collectAsStateWithLifecycle()

    val breakSeconds by
        homeViewModel.breakSeconds
            .collectAsStateWithLifecycle()

    val errorMessage by
        homeViewModel.errorMessage
            .collectAsStateWithLifecycle()

    val historyFactory =
        HistoryViewModelFactory(repository)

    val historyViewModel: HistoryViewModel =
        viewModel(
            factory = historyFactory
        )

    val workSessions by
        historyViewModel.workSessions
            .collectAsStateWithLifecycle()

    val navBackStackEntry by
        navController.currentBackStackEntryAsState()

    val currentDestination =
        navBackStackEntry?.destination

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {

            NavigationBar(
                containerColor =
                    MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {

                NavigationBarItem(
                    selected =
                        currentDestination
                            ?.hasRoute<HomeRoute>() == true,
                    onClick = {

                        navController.navigate(
                            HomeRoute
                        ) {
                            popUpTo<HomeRoute> {
                                inclusive = false
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(
                            text = "Home"
                        )
                    },
                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor =
                                MaterialTheme.colorScheme.primary,
                            selectedTextColor =
                                MaterialTheme.colorScheme.primary,
                            indicatorColor =
                                MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                )

                NavigationBarItem(
                    selected =
                        currentDestination
                            ?.hasRoute<HistoryRoute>() == true,
                    onClick = {

                        navController.navigate(
                            HistoryRoute
                        ) {
                            popUpTo<HomeRoute> {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History"
                        )
                    },
                    label = {
                        Text(
                            text = "History"
                        )
                    },
                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor =
                                MaterialTheme.colorScheme.primary,
                            selectedTextColor =
                                MaterialTheme.colorScheme.primary,
                            indicatorColor =
                                MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier =
                Modifier.padding(innerPadding)
        ) {

            composable<HomeRoute> {

                HomeScreen(
                    elapsedSeconds =
                        elapsedSeconds,
                    breakSeconds =
                        breakSeconds,
                    isRunning =
                        isRunning,
                    isOnBreak =
                        isOnBreak,
                    onStartTimer =
                        homeViewModel::startTimer,
                    onStopTimer =
                        homeViewModel::stopTimer,
                    onStartBreak =
                        homeViewModel::startBreak,
                    onResumeWork =
                        homeViewModel::resumeWork,
                    onResetTimer =
                        homeViewModel::resetTimer,
                    errorMessage =
                        errorMessage,
                    onDismissError =
                        homeViewModel::clearErrorMessage
                )
            }

            composable<HistoryRoute> {

                HistoryScreen(
                    workSessions =
                        workSessions,
                    onDeleteSession =
                        historyViewModel::deleteWorkSession
                )
            }
        }
    }
}
