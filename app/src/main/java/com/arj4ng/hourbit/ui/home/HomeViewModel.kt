package com.arj4ng.hourbit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arj4ng.hourbit.data.WorkSessionRepository
import com.arj4ng.hourbit.data.local.WorkSession
import com.arj4ng.hourbit.data.location.LocationProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

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
class HomeViewModel(
    // give off work to the repository instead of doing saving in viewModel
    private val repository: WorkSessionRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _elapsedSeconds = MutableStateFlow(0L) // internal mutable state. only the ViewModel can change it
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow() // read only state exposed to the UI

    private val _isRunning = MutableStateFlow(false) // stores whether a work session is currently running
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow() // read only running state exposed to the UI

    // store when the current work session started
    private var startTime: Long? = null // nullable for not started state

    // starts the timer if it is not already running
    fun startTimer() {
        if (_isRunning.value) return

        startTime = System.currentTimeMillis() // returns the current time as a timestamp in milliseconds
        _elapsedSeconds.value = 0L // resets the visible timer when a new work session begins
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
        if (!_isRunning.value) return // if the timer is not running stop and do nothing
        _isRunning.value = false

        // if startTime contains a value, use it
        // if it is null, leave stopTimer()
        val sessionStartTime = startTime ?: return

        val endTime = System.currentTimeMillis()

        // save the object
        viewModelScope.launch {

            // tries to get location, returns null if it fails
            val coordinates = try {

                // wait max 5 seconds for location, then continue without it
                withTimeoutOrNull(5000) {
                    locationProvider.getCurrentLocation()
                }

                // if location request fails, continue without location
            } catch (e: Exception) {
                null
            }

            // get weather only when coordinates are available
            val weather = if (coordinates != null) {
                try {

                    // request weather for the current coordinates
                    // wait max 5 seconds for weather, then continue without it
                    withTimeoutOrNull(5000) {
                        repository.getCurrentWeather(
                            latitude = coordinates.first,
                            longitude = coordinates.second
                        )
                    }

                    // if the API request fails, continue without weather
                } catch (e: Exception) {
                    null
                }
            } else {
                // no coordinates means no weather request
                null
            }

            val workSession = WorkSession(

                // create a new WorkSession object for entity fields
                startTime = sessionStartTime,
                endTime = endTime,

                latitude = coordinates?.first,
                longitude = coordinates?.second,
                // if coordinates exists → use latitude/longitude
                // if coordinates is null → save null

                temperature = weather?.temperature_2m,
                weatherCode = weather?.weather_code
                // if weather exists → save temperature/weathercode
                // if weather is null → save null
            )

            // save the object even if location or weather failed
            repository.insertWorkSession(workSession)
        }
    }

    // resets the timer back to zero
    fun resetTimer() {
        if (_isRunning.value) return // prevent resetting the timer while a session is active

        _elapsedSeconds.value = 0L
        startTime = null // clear the stored session start time
    }
}