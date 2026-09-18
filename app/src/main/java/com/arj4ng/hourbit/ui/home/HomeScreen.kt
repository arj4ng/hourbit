package com.arj4ng.hourbit.ui.home

import android.text.format.DateUtils.formatElapsedTime
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arj4ng.hourbit.ui.theme.HourbitTheme

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

// main screen
// screen receives state and actions from outside instead of managing timer logic itself
@Composable
fun HomeScreen(
    elapsedSeconds: Long,
    isRunning: Boolean,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Hourbit")

        // convert the timer value into a readable HH:MM:SS format
        Text(
            text = formatElapsedTime(elapsedSeconds)
        )

        // start while stopped and stop while a session is running
        Button(
            onClick = if (isRunning) onStopTimer else onStartTimer
        ) {
            Text(
                text = if (isRunning) "Stop" else "Start"
            )
        }

        // reset kept separate from stop so the measured time can still be seen
        Button(
            onClick = onResetTimer,
            enabled = !isRunning
        ) {
            Text(text = "Reset")
        }

        // navigation is passed into the screen as a callback
        // keeps HomeScreen independent from NavController
        Button(
            onClick = onOpenHistory
        ) {
            Text(text = "Open history")
        }
    }
}

// converts elapsed seconds into HH:MM:SS ; 3665 seconds becomes 01:01:05
private fun formatElapsedTime(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format( // "%02d" = display the number with at least two digits
        hours,
        minutes,
        seconds
    )
}




@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HourbitTheme {
        HomeScreen(
            elapsedSeconds = 65,
            isRunning = false,
            onStartTimer = {},
            onStopTimer = {},
            onResetTimer = {},
            onOpenHistory = {}
        )
    }
}