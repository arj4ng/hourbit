package com.arj4ng.hourbit.data.local

import android.content.Context
import androidx.room.Room

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

// creates and provides the Room database instance used by the app
object DatabaseProvider {

    // volatile makes changes to INSTANCE immediately visible to all threads
    @Volatile
    private var INSTANCE: HourbitDatabase? = null // holds the single database instance used by the app

    // returns the existing database or creates it the first time it is needed
    fun getDatabase(context: Context): HourbitDatabase {
        return INSTANCE ?: synchronized(this) { // creates the database only if it does not already exist

            // creates the actual room database stored on the device
            val instance = Room.databaseBuilder( // builds the actual local database file on the device
                context.applicationContext,
                HourbitDatabase::class.java,
                "hourbit_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}