package com.arj4ng.hourbit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arj4ng.hourbit.data.WorkSessionRepository
import com.arj4ng.hourbit.data.local.SessionStateStore
import com.arj4ng.hourbit.data.local.WorkSession
import com.arj4ng.hourbit.data.location.LocationProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class HomeViewModel(
    private val repository: WorkSessionRepository,
    private val locationProvider: LocationProvider,
    private val sessionStateStore: SessionStateStore
) : ViewModel() {

    private val _elapsedSeconds =
        MutableStateFlow(0L)

    val elapsedSeconds: StateFlow<Long> =
        _elapsedSeconds.asStateFlow()

    private val _isRunning =
        MutableStateFlow(false)

    val isRunning: StateFlow<Boolean> =
        _isRunning.asStateFlow()

    private val _isOnBreak =
        MutableStateFlow(false)

    val isOnBreak: StateFlow<Boolean> =
        _isOnBreak.asStateFlow()

    private val _breakSeconds =
        MutableStateFlow(0L)

    val breakSeconds: StateFlow<Long> =
        _breakSeconds.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    private var startTime: Long? = null

    private var breakStartTime: Long? = null

    private var accumulatedBreakSeconds =
        0L

    init {
        restoreSession()

        viewModelScope.launch {
            while (true) {
                updateTimers()
                delay(1000)
            }
        }
    }

    private fun restoreSession() {

        if (!sessionStateStore.isRunning()) {
            return
        }

        val savedStartTime =
            sessionStateStore.getStartTime()

        if (savedStartTime <= 0L) {
            sessionStateStore.clearSession()
            return
        }

        startTime =
            savedStartTime

        breakStartTime =
            sessionStateStore.getBreakStartTime()

        accumulatedBreakSeconds =
            sessionStateStore.getBreakSeconds()

        _isRunning.value =
            true

        _isOnBreak.value =
            sessionStateStore.isOnBreak()

        updateTimers()
    }

    private fun updateTimers() {

        if (!_isRunning.value) {
            return
        }

        val sessionStart =
            startTime ?: return

        val now =
            System.currentTimeMillis()

        var totalBreakSeconds =
            accumulatedBreakSeconds

        if (_isOnBreak.value) {

            val breakStart =
                breakStartTime

            if (breakStart != null) {

                totalBreakSeconds +=
                    (
                        now -
                        breakStart
                    ) / 1000
            }
        }

        val totalSessionSeconds =
            (
                now -
                sessionStart
            ) / 1000

        _breakSeconds.value =
            totalBreakSeconds

        _elapsedSeconds.value =
            (
                totalSessionSeconds -
                totalBreakSeconds
            ).coerceAtLeast(0L)
    }

    fun startTimer() {

        if (_isRunning.value) {
            return
        }

        _errorMessage.value =
            null

        val now =
            System.currentTimeMillis()

        startTime =
            now

        breakStartTime =
            null

        accumulatedBreakSeconds =
            0L

        _elapsedSeconds.value =
            0L

        _breakSeconds.value =
            0L

        _isOnBreak.value =
            false

        _isRunning.value =
            true

        sessionStateStore.saveSession(
            startTime = now,
            breakStartTime = null,
            accumulatedBreakSeconds = 0L,
            isOnBreak = false
        )
    }

    fun startBreak() {

        if (!_isRunning.value) {
            return
        }

        if (_isOnBreak.value) {
            return
        }

        breakStartTime =
            System.currentTimeMillis()

        _isOnBreak.value =
            true

        sessionStateStore.updateBreak(
            breakStartTime =
                breakStartTime,
            accumulatedBreakSeconds =
                accumulatedBreakSeconds,
            isOnBreak =
                true
        )

        updateTimers()
    }

    fun resumeWork() {

        if (!_isRunning.value) {
            return
        }

        if (!_isOnBreak.value) {
            return
        }

        val breakStart =
            breakStartTime

        if (breakStart != null) {

            accumulatedBreakSeconds +=
                (
                    System.currentTimeMillis() -
                    breakStart
                ) / 1000
        }

        breakStartTime =
            null

        _isOnBreak.value =
            false

        sessionStateStore.updateBreak(
            breakStartTime =
                null,
            accumulatedBreakSeconds =
                accumulatedBreakSeconds,
            isOnBreak =
                false
        )

        updateTimers()
    }

    fun stopTimer() {

        if (!_isRunning.value) {
            return
        }

        updateTimers()

        val sessionStartTime =
            startTime ?: return

        val endTime =
            System.currentTimeMillis()

        val sessionBreakSeconds =
            _breakSeconds.value

        _isRunning.value =
            false

        _isOnBreak.value =
            false

        sessionStateStore.clearSession()

        viewModelScope.launch {

            val coordinates =
                try {

                    withTimeoutOrNull(5000) {
                        locationProvider
                            .getCurrentLocation()
                    }

                } catch (e: Exception) {
                    null
                }

            if (coordinates == null) {

                _errorMessage.value =
                    "Location unavailable. Session will be saved without location."
            }

            val cityName =
                if (coordinates != null) {

                    try {
                        withTimeoutOrNull(5000) {
                            locationProvider.getCityName(
                                latitude = coordinates.first,
                                longitude = coordinates.second
                            )
                        }
                    } catch (e: Exception) {
                        null
                    }

                } else {
                    null
                }

            val weather =
                if (coordinates != null) {

                    try {

                        withTimeoutOrNull(5000) {

                            repository
                                .getCurrentWeather(
                                    latitude =
                                        coordinates.first,
                                    longitude =
                                        coordinates.second
                                )
                        }

                    } catch (e: Exception) {
                        null
                    }

                } else {
                    null
                }

            if (
                coordinates != null &&
                weather == null
            ) {

                _errorMessage.value =
                    "Weather unavailable. Session will be saved without weather."
            }

            val workSession =
                WorkSession(
                    startTime =
                        sessionStartTime,
                    endTime =
                        endTime,
                    breakDurationSeconds =
                        sessionBreakSeconds,
                    latitude =
                        coordinates?.first,
                    longitude =
                        coordinates?.second,
                    cityName =
                        cityName,
                    temperature =
                        weather?.temperature_2m,
                    weatherCode =
                        weather?.weather_code
                )

            try {

                repository
                    .insertWorkSession(
                        workSession
                    )

            } catch (e: Exception) {

                _errorMessage.value =
                    "Could not save work session."
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value =
            null
    }

    fun resetTimer() {

        if (_isRunning.value) {
            return
        }

        _elapsedSeconds.value =
            0L

        _breakSeconds.value =
            0L

        startTime =
            null

        breakStartTime =
            null

        accumulatedBreakSeconds =
            0L

        sessionStateStore.clearSession()
    }
}
