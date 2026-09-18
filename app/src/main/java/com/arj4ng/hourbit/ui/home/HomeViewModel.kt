package com.arj4ng.hourbit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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


// the timer state and logic for the Home screen
// UI observes this state instead of managing the timer itself
class HomeViewModel : ViewModel() {

    private val _elapsedSeconds = MutableStateFlow(0L) // internal mutable state. only the ViewModel can change it

    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow() // read only state exposed to the UI

    private val _isRunning = MutableStateFlow(false) // stores whether a work session is currently running

    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow() // read only running state exposed to the UI

    // starts the timer if it is not already running
    fun startTimer() {
        if (_isRunning.value) return
        _isRunning.value = true
        viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                _elapsedSeconds.value++
            }
        }
    }

    // stops the currently running timer
    fun stopTimer() {
        _isRunning.value = false
    }

    // resets the timer back to zero
    fun resetTimer() {
        _elapsedSeconds.value = 0L
    }
}