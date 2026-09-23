package com.arj4ng.hourbit.ui.home

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
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
    onOpenHistory: () -> Unit,
    errorMessage: String?,
    onDismissError: () -> Unit
) {

    // give this composable access to the current android context
    val context = LocalContext.current

    // track whether location permission is already granted
    var hasLocationPermission by remember { // compose state so the UI updates when permission status changes
        mutableStateOf( // compose state that triggers UI updates when the value changes
            ContextCompat.checkSelfPermission( // check whether the app already has location permission
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

// opens android's location permission dialog
    val locationPermissionLauncher =
        rememberLauncherForActivityResult( // launch a system action and receives its result
            contract = ActivityResultContracts.RequestMultiplePermissions() // define this launcher as a multiple permission request
        ) { permissions -> // result after the user answers the permissions

            hasLocationPermission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            // update permission state if either precise or approximate location was granted
        }

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

        // ask for location permission when it has not been granted yet
        if (!hasLocationPermission) {
            Button(
                onClick = {
                    locationPermissionLauncher.launch( // open androids permission dialog
                        arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION, // request precise location access
                            Manifest.permission.ACCESS_COARSE_LOCATION // request corse location access
                    ))
                }
            ) {
                Text(text = "Allow location")
            }
        }

        // location is optional, so the app still works when permission is denied
        if (!hasLocationPermission) {
            Text(text = "Location is optional. Sessions can still be saved.")
        }

        // show understandable errors instead of crashing
        if (errorMessage != null) {
            Text(text = errorMessage)

            Button(
                onClick = onDismissError
            ) {
                Text(text = "Dismiss")
            }
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
            onOpenHistory = {},
            errorMessage = null,
            onDismissError = {}
        )
    }
}