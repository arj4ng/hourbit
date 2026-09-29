package com.arj4ng.hourbit.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import com.arj4ng.hourbit.MainActivity
import com.arj4ng.hourbit.R
import com.arj4ng.hourbit.data.local.SessionStateStore


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


class HourbitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateAllWidgets(context)
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)

        val store = SessionStateStore(context)

        when (intent.action) {

            ACTION_START -> {
                if (!store.isRunning()) {
                    store.saveSession(
                        startTime = System.currentTimeMillis(),
                        breakStartTime = null,
                        accumulatedBreakSeconds = 0L,
                        isOnBreak = false
                    )
                }
            }

            ACTION_BREAK -> {
                if (
                    store.isRunning() &&
                    !store.isOnBreak()
                ) {
                    store.updateBreak(
                        breakStartTime =
                            System.currentTimeMillis(),
                        accumulatedBreakSeconds =
                            store.getBreakSeconds(),
                        isOnBreak = true
                    )
                }
            }

            ACTION_RESUME -> {
                if (
                    store.isRunning() &&
                    store.isOnBreak()
                ) {
                    val breakStart =
                        store.getBreakStartTime()

                    var accumulated =
                        store.getBreakSeconds()

                    if (breakStart != null) {
                        accumulated +=
                            (
                                System.currentTimeMillis() -
                                    breakStart
                                ) / 1000L
                    }

                    store.updateBreak(
                        breakStartTime = null,
                        accumulatedBreakSeconds = accumulated,
                        isOnBreak = false
                    )
                }
            }
        }

        updateAllWidgets(context)
    }

    companion object {

        private const val ACTION_START =
            "com.arj4ng.hourbit.widget.START"

        private const val ACTION_BREAK =
            "com.arj4ng.hourbit.widget.BREAK"

        private const val ACTION_RESUME =
            "com.arj4ng.hourbit.widget.RESUME"

        fun updateAllWidgets(context: Context) {
            val manager =
                AppWidgetManager.getInstance(context)

            val component =
                ComponentName(
                    context,
                    HourbitWidgetProvider::class.java
                )

            val ids =
                manager.getAppWidgetIds(component)

            ids.forEach { id ->
                updateWidget(
                    context,
                    manager,
                    id
                )
            }
        }

        private fun updateWidget(
            context: Context,
            manager: AppWidgetManager,
            widgetId: Int
        ) {
            val store =
                SessionStateStore(context)

            val views =
                RemoteViews(
                    context.packageName,
                    R.layout.hourbit_widget
                )

            val running =
                store.isRunning()

            val onBreak =
                running && store.isOnBreak()

            val workSeconds =
                calculateWorkSeconds(store)

            resetColors(views)

            views.setViewVisibility(
                R.id.widget_secondary,
                View.VISIBLE
            )

            views.setViewVisibility(
                R.id.widget_break_timer,
                View.GONE
            )

            when {

                !running -> {
                    views.setTextViewText(
                        R.id.widget_status,
                        "READY"
                    )

                    views.setTextViewText(
                        R.id.widget_live,
                        "◷"
                    )

                    views.setTextViewText(
                        R.id.widget_secondary,
                        "Start your next focus session"
                    )

                    views.setTextViewText(
                        R.id.widget_primary_action,
                        "▶  START"
                    )

                    views.setViewVisibility(
                        R.id.widget_finish_action,
                        View.GONE
                    )

                    views.setViewVisibility(
                        R.id.widget_action_space,
                        View.GONE
                    )

                    resetTimer(views)

                    views.setOnClickPendingIntent(
                        R.id.widget_primary_action,
                        broadcastIntent(
                            context,
                            ACTION_START,
                            1
                        )
                    )
                }

                onBreak -> {
                    views.setTextViewText(
                        R.id.widget_status,
                        "ON BREAK"
                    )

                    views.setTextViewText(
                        R.id.widget_live,
                        "●"
                    )

                    views.setTextColor(
                        R.id.widget_status,
                        0xFFFFB95F.toInt()
                    )

                    views.setTextColor(
                        R.id.widget_live,
                        0xFFFFB95F.toInt()
                    )

                    val breakSeconds =
                        calculateCurrentBreakSeconds(
                            store
                        )

                    views.setViewVisibility(
                        R.id.widget_secondary,
                        View.GONE
                    )

                    views.setViewVisibility(
                        R.id.widget_break_timer,
                        View.VISIBLE
                    )

                    val breakBase =
                        SystemClock.elapsedRealtime() -
                            breakSeconds * 1000L

                    views.setChronometer(
                        R.id.widget_break_timer,
                        breakBase,
                        "Break  %s",
                        true
                    )

                    views.setTextViewText(
                        R.id.widget_primary_action,
                        "▶  RESUME"
                    )

                    showFinishButton(views)

                    setStoppedTimer(
                        views,
                        workSeconds
                    )

                    views.setOnClickPendingIntent(
                        R.id.widget_primary_action,
                        broadcastIntent(
                            context,
                            ACTION_RESUME,
                            3
                        )
                    )
                }

                else -> {
                    views.setTextViewText(
                        R.id.widget_status,
                        "FOCUSING"
                    )

                    views.setTextViewText(
                        R.id.widget_live,
                        "●"
                    )

                    views.setTextViewText(
                        R.id.widget_secondary,
                        "Work session in progress"
                    )

                    views.setTextViewText(
                        R.id.widget_primary_action,
                        "Ⅱ  BREAK"
                    )

                    showFinishButton(views)

                    val base =
                        SystemClock.elapsedRealtime() -
                            workSeconds * 1000L

                    views.setChronometer(
                        R.id.widget_timer,
                        base,
                        null,
                        true
                    )

                    views.setOnClickPendingIntent(
                        R.id.widget_primary_action,
                        broadcastIntent(
                            context,
                            ACTION_BREAK,
                            2
                        )
                    )
                }
            }

            val openApp =
                PendingIntent.getActivity(
                    context,
                    10,
                    Intent(
                        context,
                        MainActivity::class.java
                    ),
                    PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
                )

            views.setOnClickPendingIntent(
                R.id.widget_root,
                openApp
            )

            views.setOnClickPendingIntent(
                R.id.widget_finish_action,
                openApp
            )

            manager.updateAppWidget(
                widgetId,
                views
            )
        }

        private fun resetColors(
            views: RemoteViews
        ) {
            views.setTextColor(
                R.id.widget_status,
                0xFFAFC1FF.toInt()
            )

            views.setTextColor(
                R.id.widget_live,
                0xFF4967FF.toInt()
            )
        }

        private fun showFinishButton(
            views: RemoteViews
        ) {
            views.setViewVisibility(
                R.id.widget_finish_action,
                View.VISIBLE
            )

            views.setViewVisibility(
                R.id.widget_action_space,
                View.VISIBLE
            )
        }

        private fun broadcastIntent(
            context: Context,
            action: String,
            requestCode: Int
        ): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                Intent(
                    context,
                    HourbitWidgetProvider::class.java
                ).setAction(action),
                PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
            )

        private fun resetTimer(
            views: RemoteViews
        ) {
            views.setChronometer(
                R.id.widget_timer,
                SystemClock.elapsedRealtime(),
                null,
                false
            )

            views.setTextViewText(
                R.id.widget_timer,
                "00:00"
            )
        }

        private fun setStoppedTimer(
            views: RemoteViews,
            seconds: Long
        ) {
            val base =
                SystemClock.elapsedRealtime() -
                    seconds * 1000L

            views.setChronometer(
                R.id.widget_timer,
                base,
                null,
                false
            )
        }

        private fun calculateWorkSeconds(
            store: SessionStateStore
        ): Long {
            if (!store.isRunning()) {
                return 0L
            }

            val start =
                store.getStartTime()

            if (start <= 0L) {
                return 0L
            }

            val now =
                System.currentTimeMillis()

            var breaks =
                store.getBreakSeconds()

            if (store.isOnBreak()) {
                val breakStart =
                    store.getBreakStartTime()

                if (breakStart != null) {
                    breaks +=
                        (now - breakStart) /
                            1000L
                }
            }

            return (
                (now - start) / 1000L -
                    breaks
                ).coerceAtLeast(0L)
        }

        private fun calculateCurrentBreakSeconds(
            store: SessionStateStore
        ): Long {
            val breakStart =
                store.getBreakStartTime()
                    ?: return 0L

            return (
                System.currentTimeMillis() -
                    breakStart
                ) / 1000L
        }

        private fun formatShortDuration(
            seconds: Long
        ): String {
            val minutes =
                seconds / 60

            val remainingSeconds =
                seconds % 60

            return "%02d:%02d".format(
                minutes,
                remainingSeconds
            )
        }
    }
}
