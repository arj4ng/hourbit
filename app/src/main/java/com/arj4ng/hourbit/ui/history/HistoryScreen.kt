package com.arj4ng.hourbit.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arj4ng.hourbit.data.local.WorkSession
import com.arj4ng.hourbit.ui.theme.HourbitTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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


// screen that will display saved work sessions
@Composable
fun HistoryScreen(
    workSessions: List<WorkSession>, // receives the saved sessions from the ViewModel
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        Text(text = "Work history")

        // shows how many saved work sessions are currently loaded
        Text(
            text = "Saved sessions: ${workSessions.size}"
        )
        // screen only requests a back action
        // navigation logic stays outside the UI screen
        Button(
            onClick = onBack
        ) {
            Text(text = "Back")
        }

        if (workSessions.isEmpty()) {
            // show when no sessions have been saved yet
            Text(
                text = "No work sessions yet"
            )
        } else {
            // scrollable list of all saved work sessions
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // create one UI item for each saved WorkSession
                items(workSessions) { session ->
                    WorkSessionItem(session) // reusable UI for one saved work session
                }
            }
        }
    }
}

@Composable
private fun WorkSessionItem(
    session: WorkSession
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = formatDate(session.startTime)
            )

            Text(
                text = "Start: ${formatTime(session.startTime)}"
            )

            Text(
                text = "End: ${formatTime(session.endTime)}"
            )

            Text(
                text = "Duration: ${formatDuration(session)}"
            )
        }
    }
}

// convert stored timestamps into readable date text
private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "dd.MM.yyyy",
        Locale.getDefault()
    )
    return formatter.format(Date(timestamp))
}

// convert stored timestamps into readable time text
private fun formatTime(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "HH:mm:ss",
        Locale.getDefault()
    )
    return formatter.format(Date(timestamp))
}


// calculate duration from endTime - startTime
private fun formatDuration(session: WorkSession): String {
    val durationSeconds =
        (session.endTime - session.startTime) / 1000
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    val seconds = durationSeconds % 60
    return "%02d:%02d:%02d".format(
        hours,
        minutes,
        seconds
    )
}


@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HourbitTheme {
        HistoryScreen(
            workSessions = emptyList(),
            onBack = {}
        )
    }
}