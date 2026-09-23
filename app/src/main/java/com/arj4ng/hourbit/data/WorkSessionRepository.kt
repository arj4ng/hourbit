package com.arj4ng.hourbit.data

import com.arj4ng.hourbit.data.local.WorkSession
import com.arj4ng.hourbit.data.local.WorkSessionDao
import kotlinx.coroutines.flow.Flow
import com.arj4ng.hourbit.data.remote.CurrentWeather
import com.arj4ng.hourbit.data.remote.WeatherApi

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


// data layer between the ViewModel and Room
// ViewModel doesn't need to access the DAO directly
class WorkSessionRepository(
    private val workSessionDao: WorkSessionDao,
    private val weatherApi: WeatherApi // give the repository access to remote weather data
    // private because only the repository itself needs direct access to the DAO
) {

    // save one completed work session
    suspend fun insertWorkSession(workSession: WorkSession) {
        workSessionDao.insertWorkSession(workSession)
    }

    // expose all saved sessions from Room
    fun getAllWorkSessions(): Flow<List<WorkSession>> {
        return workSessionDao.getAllWorkSessions()
    }

    // delete one saved work session
    suspend fun deleteWorkSession(workSession: WorkSession) {
        workSessionDao.deleteWorkSession(workSession)
    }

    // get current weather for the given coordinates
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): CurrentWeather {
        return weatherApi // call the weather API through retrofit
            .getCurrentWeather( // send latitude and longitude to the API
                // coordinates used for the weather request
                latitude = latitude,
                longitude = longitude
            )
            // return only the current weather part of the response
            .current
    }
}