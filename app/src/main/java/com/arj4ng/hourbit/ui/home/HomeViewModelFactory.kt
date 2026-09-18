package com.arj4ng.hourbit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arj4ng.hourbit.data.WorkSessionRepository

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


// the factory creates HomeViewModel because it needs the WorkSessionRepository
// normal viewModel() call can't create the dependency automatically

// create HomeViewModel with the dependencies it needs
class HomeViewModelFactory(
    // repository that will be passed into HomeViewModel
    private val repository: WorkSessionRepository

    // follow Android’s standard interface for creating ViewModels
) : ViewModelProvider.Factory { // = an Android Lifecycle API type
    override fun <T : ViewModel> create( // the function Android calls when it wants a ViewModel
        modelClass: Class<T> // T = the type of ViewModel Android is requesting
    ): T {

        // checks whether HomeViewModel was requested
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // suppresses generic type cast warning
            return HomeViewModel(repository) as T // create HomeViewModel and give it the repository
        }

        // fails if this factory receives an unsupported ViewModel type
        throw IllegalArgumentException("unknown ViewModel class")
    }
}

// Repository exists
//      ↓
// Factory receives Repository
//      ↓
// Android asks Factory for HomeViewModel
//      ↓
// Factory creates HomeViewModel(repository)