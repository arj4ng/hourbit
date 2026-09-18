package com.arj4ng.hourbit.data

import com.arj4ng.hourbit.data.local.WorkSession
import com.arj4ng.hourbit.data.local.WorkSessionDao
import kotlinx.coroutines.flow.Flow

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
    private val workSessionDao: WorkSessionDao
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
}