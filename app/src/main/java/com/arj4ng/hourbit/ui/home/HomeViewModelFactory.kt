package com.arj4ng.hourbit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arj4ng.hourbit.data.WorkSessionRepository
import com.arj4ng.hourbit.data.local.SessionStateStore
import com.arj4ng.hourbit.data.location.LocationProvider

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

class HomeViewModelFactory(
    private val repository: WorkSessionRepository,
    private val locationProvider: LocationProvider,
    private val sessionStateStore: SessionStateStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                HomeViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                repository,
                locationProvider,
                sessionStateStore
            ) as T
        }

        throw IllegalArgumentException(
            "unknown ViewModel class"
        )
    }
}
