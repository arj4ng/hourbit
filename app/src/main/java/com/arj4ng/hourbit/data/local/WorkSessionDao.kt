package com.arj4ng.hourbit.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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


// DAO = Data Access Object
@Dao // it defines how the app can read from and write to the WorkSession table
interface WorkSessionDao {

    @Insert // inserts one completed work session into the database
    suspend fun insertWorkSession(workSession: WorkSession) // suspend = makes the function callable from a coroutine and allows suspension

    @Delete
    suspend fun deleteWorkSession(workSession: WorkSession)

    @Query("SELECT * FROM work_sessions ORDER BY startTime DESC") // reads all saved work sessions, newest first
    fun getAllWorkSessions(): Flow<List<WorkSession>> // flow automatically emits a new list whenever the table changes
}