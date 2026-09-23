package com.arj4ng.hourbit.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.arj4ng.hourbit.ui.theme.HourbitTheme

@Composable
fun HomeScreen(
    elapsedSeconds: Long,
    breakSeconds: Long,
    isRunning: Boolean,
    isOnBreak: Boolean,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onStartBreak: () -> Unit,
    onResumeWork: () -> Unit,
    onResetTimer: () -> Unit,
    errorMessage: String?,
    onDismissError: () -> Unit
) {

    val context =
        LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            hasLocationPermission =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true ||
                        permissions[
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ] == true
        }

    val primaryColor =
        MaterialTheme.colorScheme.primary

    val secondaryText =
        MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 20.dp
            )
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Hourbit",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Focus, track and review your work",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryText
                )
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Clock",
                    modifier = Modifier
                        .padding(11.dp)
                        .size(20.dp),
                    tint = primaryColor
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color =
                        if (hasLocationPermission)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                ) {

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(9.dp)
                            .size(18.dp),
                        tint =
                            if (hasLocationPermission)
                                primaryColor
                            else
                                secondaryText
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {

                    Text(
                        text = "SESSION CONTEXT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = secondaryText
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (hasLocationPermission)
                                "Weather & location enabled"
                            else
                                "Weather & location disabled",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text =
                            if (hasLocationPermission)
                                "Added automatically when the session ends"
                            else
                                "Enable context for richer session history",
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryText
                    )
                }

                if (!hasLocationPermission) {

                    TextButton(
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    ) {
                        Text("Enable")
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment =
                Alignment.Center
        ) {

            Column(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color =
                        when {
                            isOnBreak ->
                                MaterialTheme.colorScheme.tertiaryContainer

                            isRunning ->
                                MaterialTheme.colorScheme.primaryContainer

                            else ->
                                MaterialTheme.colorScheme.surfaceVariant
                        }
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {

                        Surface(
                            modifier = Modifier.size(7.dp),
                            shape = CircleShape,
                            color =
                                when {
                                    isOnBreak ->
                                        MaterialTheme.colorScheme.tertiary

                                    isRunning ->
                                        primaryColor

                                    else ->
                                        secondaryText
                                }
                        ) {}

                        Text(
                            text =
                                when {
                                    isOnBreak -> "ON BREAK"
                                    isRunning -> "FOCUSING"
                                    else -> "READY"
                                },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.9.sp,
                            color =
                                when {
                                    isOnBreak ->
                                        MaterialTheme.colorScheme.onTertiaryContainer

                                    isRunning ->
                                        MaterialTheme.colorScheme.onPrimaryContainer

                                    else ->
                                        secondaryText
                                }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                Text(
                    text =
                        formatElapsedTime(
                            elapsedSeconds
                        ),
                    fontSize = 68.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-2.5).sp,
                    textAlign =
                        TextAlign.Center,
                    maxLines = 1
                )

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text = "WORK TIME",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                    color = secondaryText
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                if (isOnBreak) {

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {

                            Icon(
                                imageVector = Icons.Default.Coffee,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )

                            Text(
                                text = "Break ${formatElapsedTime(breakSeconds)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                } else {

                    Text(
                        text =
                            if (isRunning)
                                "Tracking your work time"
                            else
                                "Ready when you are",
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            secondaryText,
                        textAlign =
                            TextAlign.Center
                    )
                }
            }
        }

        if (errorMessage != null) {

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(18.dp),
                color =
                    MaterialTheme.colorScheme.errorContainer
            ) {

                Row(
                    modifier =
                        Modifier.padding(16.dp),
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Warning,
                        contentDescription =
                            null,
                        tint =
                            MaterialTheme.colorScheme.error
                    )

                    Text(
                        text =
                            errorMessage,
                        modifier =
                            Modifier.weight(1f),
                        style =
                            MaterialTheme.typography.bodySmall
                    )

                    TextButton(
                        onClick =
                            onDismissError
                    ) {
                        Text("Dismiss")
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )
        }

        Button(
            onClick =
                when {
                    !isRunning ->
                        onStartTimer

                    isOnBreak ->
                        onResumeWork

                    else ->
                        onStopTimer
                },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape =
                RoundedCornerShape(20.dp)
        ) {

            Icon(
                imageVector =
                    when {
                        !isRunning ->
                            Icons.Default.PlayArrow

                        isOnBreak ->
                            Icons.Default.PlayArrow

                        else ->
                            Icons.Default.Stop
                    },
                contentDescription =
                    null
            )

            Text(
                text =
                    when {
                        !isRunning ->
                            "Start session"

                        isOnBreak ->
                            "Resume work"

                        else ->
                            "Stop session"
                    },
                modifier =
                    Modifier.padding(start = 8.dp),
                fontWeight =
                    FontWeight.SemiBold,
                fontSize =
                    16.sp
            )
        }

        if (isRunning) {

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            if (!isOnBreak) {

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {

                    TextButton(
                        onClick = onStartBreak,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Coffee,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = "Take a break",
                            modifier = Modifier.padding(start = 7.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

            } else {

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {

                    TextButton(
                        onClick = onStopTimer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = "End session",
                            modifier = Modifier.padding(start = 6.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (!isRunning) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                )
            ) {

                TextButton(
                    onClick = onResetTimer,
                    enabled = elapsedSeconds > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = "Reset timer",
                        modifier = Modifier.padding(start = 6.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

    }
}

private fun formatElapsedTime(
    totalSeconds: Long
): String {

    val hours =
        totalSeconds / 3600

    val minutes =
        (totalSeconds % 3600) / 60

    val seconds =
        totalSeconds % 60

    return "%02d:%02d:%02d".format(
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
            elapsedSeconds = 3725,
            breakSeconds = 502,
            isRunning = true,
            isOnBreak = true,
            onStartTimer = {},
            onStopTimer = {},
            onStartBreak = {},
            onResumeWork = {},
            onResetTimer = {},
            errorMessage = null,
            onDismissError = {}
        )
    }
}
