package com.arj4ng.hourbit.data.local

import android.content.Context


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


// stores the active timer state so it survives app restarts
class SessionStateStore(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "hourbit_session_state",
            Context.MODE_PRIVATE
        )

    fun saveSession(
        startTime: Long,
        breakStartTime: Long?,
        accumulatedBreakSeconds: Long,
        isOnBreak: Boolean
    ) {

        preferences.edit()
            .putLong("start_time", startTime)
            .putLong(
                "break_start_time",
                breakStartTime ?: 0L
            )
            .putLong(
                "break_seconds",
                accumulatedBreakSeconds
            )
            .putBoolean(
                "is_on_break",
                isOnBreak
            )
            .putBoolean(
                "is_running",
                true
            )
            .apply()
    }

    fun updateBreak(
        breakStartTime: Long?,
        accumulatedBreakSeconds: Long,
        isOnBreak: Boolean
    ) {

        preferences.edit()
            .putLong(
                "break_start_time",
                breakStartTime ?: 0L
            )
            .putLong(
                "break_seconds",
                accumulatedBreakSeconds
            )
            .putBoolean(
                "is_on_break",
                isOnBreak
            )
            .apply()
    }

    fun isRunning(): Boolean =
        preferences.getBoolean(
            "is_running",
            false
        )

    fun getStartTime(): Long =
        preferences.getLong(
            "start_time",
            0L
        )

    fun getBreakStartTime(): Long? =
        preferences.getLong(
            "break_start_time",
            0L
        ).takeIf { it > 0L }

    fun getBreakSeconds(): Long =
        preferences.getLong(
            "break_seconds",
            0L
        )

    fun isOnBreak(): Boolean =
        preferences.getBoolean(
            "is_on_break",
            false
        )

    fun clearSession() {
        preferences.edit()
            .clear()
            .apply()
    }
}
