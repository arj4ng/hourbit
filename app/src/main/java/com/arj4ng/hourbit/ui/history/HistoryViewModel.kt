package com.arj4ng.hourbit.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arj4ng.hourbit.data.WorkSessionRepository
import com.arj4ng.hourbit.data.local.WorkSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

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

// Room
// ↓
// WorkSessionDao
// ↓
// WorkSessionRepository
// ↓
// HistoryViewModel
// ↓
// HistoryScreen

// provide saved session state for HistoryScreen
class HistoryViewModel(
    repository: WorkSessionRepository // receive data access through the repository
) : ViewModel() {

    // convert the Room Flow into observable UI state
    val workSessions: StateFlow<List<WorkSession>> = // current observable list of saved work sessions
        repository.getAllWorkSessions() // get the saved sessions Flow from the repository
            .stateIn( // turn the Flow into UI state
                scope = viewModelScope, // keep the StateFlow tied to the ViewModel lifecycle
                started = SharingStarted.WhileSubscribed(5000), // keep collecting while UI is subscribed with 5s delay before stopping
                initialValue = emptyList() // start with an empty list until Room provides data
            )
}