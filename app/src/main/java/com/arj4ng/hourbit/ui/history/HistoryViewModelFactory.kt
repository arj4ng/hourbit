package com.arj4ng.hourbit.ui.history

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


// the factory creates HistoryViewModel because it needs the WorkSessionRepository
// normal viewModel() call can't create the dependency automatically

// create HistoryViewModel with the repository it needs
class HistoryViewModelFactory(
    // repository that will be passed into HistoryViewModel
    private val repository: WorkSessionRepository

    // follow Android’s standard interface for creating ViewModels
) : ViewModelProvider.Factory { // = an Android Lifecycle API type
    override fun <T : ViewModel> create( // the function Android calls when it wants a ViewModel
        modelClass: Class<T> // T = the type of ViewModel Android is requesting
    ): T {

        // checks whether HistoryViewModel class was requested
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // suppresses generic type cast warning
            return HistoryViewModel(repository) as T // create HistoryViewModel and give it the repository
        }

        // fails if this factory receives an unsupported ViewModel type
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Repository exists
//      ↓
// Factory receives Repository
//      ↓
// Android asks Factory for HistoryViewModel
//      ↓
// Factory creates HistoryViewModel(repository)