package com.arj4ng.hourbit.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.draggable
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arj4ng.hourbit.data.local.WorkSession
import com.arj4ng.hourbit.ui.theme.HourbitTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(
    workSessions: List<WorkSession>,
    onDeleteSession: (WorkSession) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 22.dp,
                vertical = 20.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Review your recent work sessions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    modifier = Modifier
                        .padding(11.dp)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        val pagerState =
            rememberPagerState(
                initialPage = 1000,
                pageCount = { 2001 }
            )

        val visibleWeek =
            remember(pagerState.currentPage) {
                getWeekForPage(
                    page = pagerState.currentPage,
                    centerPage = 1000
                )
            }

        val weekSessions =
            workSessions.filter { session ->
                session.startTime >= visibleWeek.first &&
                        session.startTime <= visibleWeek.second
            }

        SevenDayCalendar(
            pagerState = pagerState,
            workSessions = workSessions
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (weekSessions.isEmpty()) {

            EmptyHistory()

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                itemsIndexed(
                    items = weekSessions,
                    key = { _, session ->
                        session.id
                    }
                ) { index, session ->

                    val previousSession =
                        weekSessions.getOrNull(
                            index - 1
                        )

                    val showDateHeader =
                        previousSession == null ||
                                !isSameDay(
                                    previousSession.startTime,
                                    session.startTime
                                )

                    if (showDateHeader) {

                        Text(
                            text = formatSectionDate(
                                session.startTime
                            ),
                            style =
                                MaterialTheme.typography.labelMedium,
                            fontWeight =
                                FontWeight.Bold,
                            letterSpacing =
                                1.2.sp,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier =
                                Modifier.padding(
                                    top =
                                        if (index == 0)
                                            0.dp
                                        else
                                            12.dp,
                                    bottom = 2.dp
                                )
                        )
                    }

                    SwipeToDeleteSession(
                        session = session,
                        onDelete = {
                            onDeleteSession(session)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SevenDayCalendar(
    pagerState: androidx.compose.foundation.pager.PagerState,
    workSessions: List<WorkSession>
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->

            val week =
                getWeekDaysForPage(
                    page = page,
                    centerPage = 1000
                )

            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 15.dp
                )
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "RECENT ACTIVITY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = formatWeekRange(
                                week.first(),
                                week.last()
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "Swipe",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    week.forEach { day ->

                        val hasSession =
                            workSessions.any {
                                isSameDay(
                                    it.startTime,
                                    day.time
                                )
                            }

                        val isToday =
                            isSameDay(
                                System.currentTimeMillis(),
                                day.time
                            )

                        CalendarDay(
                            date = day,
                            hasSession = hasSession,
                            isToday = isToday,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: Date,
    hasSession: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color =
            if (isToday)
                MaterialTheme.colorScheme.primaryContainer
            else
                Color.Transparent
    ) {

        Column(
            modifier = Modifier.padding(
                vertical = 9.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = SimpleDateFormat(
                    "EEE",
                    Locale.getDefault()
                ).format(date).take(1).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color =
                    if (isToday)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = SimpleDateFormat(
                    "dd",
                    Locale.getDefault()
                ).format(date),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Surface(
                modifier = Modifier.size(6.dp),
                shape = RoundedCornerShape(50),
                color =
                    if (hasSession)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Transparent
            ) {}
        }
    }
}

private fun getWeekForPage(
    page: Int,
    centerPage: Int
): Pair<Long, Long> {

    val days =
        getWeekDaysForPage(
            page = page,
            centerPage = centerPage
        )

    val first =
        Calendar.getInstance().apply {
            time = days.first()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    val last =
        Calendar.getInstance().apply {
            time = days.last()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

    return Pair(
        first,
        last
    )
}

private fun getWeekDaysForPage(
    page: Int,
    centerPage: Int
): List<Date> {

    val offsetWeeks =
        page - centerPage

    val calendar =
        Calendar.getInstance().apply {

            firstDayOfWeek =
                Calendar.MONDAY

            set(
                Calendar.DAY_OF_WEEK,
                Calendar.MONDAY
            )

            add(
                Calendar.WEEK_OF_YEAR,
                offsetWeeks
            )
        }

    return List(7) {

        val date =
            calendar.time

        calendar.add(
            Calendar.DAY_OF_MONTH,
            1
        )

        date
    }
}

private fun formatWeekRange(
    start: Date,
    end: Date
): String {

    val formatter =
        SimpleDateFormat(
            "dd MMM",
            Locale.getDefault()
        )

    return "${formatter.format(start)} – ${formatter.format(end)}"
}

@Composable
private fun EmptyHistory() {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 36.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "No sessions yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Completed work sessions will appear here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SwipeToDeleteSession(
    session: WorkSession,
    onDelete: () -> Unit
) {

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val scope =
        rememberCoroutineScope()

    val density =
        LocalDensity.current

    val revealWidth =
        96.dp

    val revealPx =
        with(density) {
            revealWidth.toPx()
        }

    val offsetX =
        remember {
            Animatable(0f)
        }

    val draggableState =
        rememberDraggableState { delta ->

            scope.launch {

                val proposed =
                    offsetX.value + delta

                val newOffset =
                    when {

                        // Don't allow dragging right past the closed position
                        proposed > 0f ->
                            0f

                        // Normal movement until delete is fully revealed
                        proposed >= -revealPx ->
                            proposed

                        // Small resistance / rubber-band effect past reveal point
                        else -> {
                            val overshoot =
                                proposed + revealPx

                            -revealPx +
                                overshoot * 0.18f
                        }
                    }

                // limit how far the rubber-band can stretch
                offsetX.snapTo(
                    newOffset.coerceAtLeast(
                        -revealPx * 1.18f
                    )
                )
            }
        }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        // DELETE ACTION BEHIND THE CARD
        //
        // The red background fills the ENTIRE swipe container.
        // The foreground card covers it until it is dragged left.
        // This makes the red continue underneath the card's
        // rounded right corners instead of exposing the screen background.
        Surface(
            modifier = Modifier
                .matchParentSize()
                .clip(
                    RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            color =
                if (androidx.compose.foundation.isSystemInDarkTheme())
                    com.arj4ng.hourbit.ui.theme.DeleteRedDark
                else
                    com.arj4ng.hourbit.ui.theme.DeleteRed
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {

                Column(
                    modifier = Modifier
                        .width(revealWidth)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete session",
                        tint = Color.White,
                        modifier = Modifier.size(21.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Delete",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // SESSION CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = offsetX.value.roundToInt(),
                        y = 0
                    )
                }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {

                        scope.launch {

                            val target =
                                if (
                                    offsetX.value <
                                    -revealPx * 0.55f
                                ) {
                                    -revealPx
                                } else {
                                    0f
                                }

                            offsetX.animateTo(
                                targetValue = target,
                                animationSpec = spring(
                                    dampingRatio = 0.72f,
                                    stiffness = 550f
                                )
                            )
                        }
                    }
                )
        ) {

            WorkSessionCard(
                session = session
            )
        }
    }

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {

                showDeleteDialog = false

                scope.launch {
                    offsetX.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = 0.75f,
                            stiffness = 550f
                        )
                    )
                }
            },
            title = {
                Text(
                    text = "Delete session?"
                )
            },
            text = {
                Text(
                    text =
                        "This work session will be permanently removed from your history."
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        showDeleteDialog = false

                        scope.launch {
                            offsetX.animateTo(
                                targetValue = 0f
                            )
                        }

                        onDelete()
                    }
                ) {

                    Text(
                        text = "Delete",
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        showDeleteDialog = false

                        scope.launch {
                            offsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = 0.75f,
                                    stiffness = 550f
                                )
                            )
                        }
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}

@Composable
private fun WorkSessionCard(
    session: WorkSession
) {

    val workSeconds =
        workDurationSeconds(session)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = formatDate(
                            session.startTime
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = formatDay(
                            session.startTime
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {

                    Text(
                        text =
                            formatDurationCompact(
                                workSeconds
                            ),
                        modifier =
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            HorizontalDivider(
                color =
                    MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "START",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = formatTime(session.startTime),
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(9.dp)
                                .size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {

                        Text(
                            text = "END",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = formatTime(session.endTime),
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (
                session.breakDurationSeconds > 0 ||
                session.temperature != null ||
                session.weatherCode != null ||
                (
                    session.latitude != null &&
                    session.longitude != null
                )
            ) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // break is a session metric, so it gets the full width
                if (session.breakDurationSeconds > 0) {

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 12.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface.copy(
                                    alpha = 0.55f
                                )
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Coffee,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(9.dp)
                                        .size(19.dp),
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }

                            Column(
                                modifier = Modifier.padding(start = 12.dp)
                            ) {

                                Text(
                                    text = "BREAK",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.9.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )

                                Spacer(
                                    modifier = Modifier.height(2.dp)
                                )

                                Text(
                                    text = formatBreakDuration(
                                        session.breakDurationSeconds
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    if (
                        session.temperature != null ||
                        session.weatherCode != null ||
                        (
                            session.latitude != null &&
                            session.longitude != null
                        )
                    ) {
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }
                }

                // weather and location are contextual information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if (
                        session.temperature != null ||
                        session.weatherCode != null
                    ) {

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 12.dp
                                )
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {

                                    Icon(
                                        imageVector = weatherIcon(
                                            session.weatherCode
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(17.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = "WEATHER",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.7.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(7.dp)
                                )

                                Text(
                                    text = buildWeatherText(session),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    if (
                        session.latitude != null &&
                        session.longitude != null
                    ) {

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 12.dp
                                )
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(17.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = "LOCATION",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.7.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(7.dp)
                                )

                                Text(
                                    text = session.cityName ?: "Unknown",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

private fun workDurationSeconds(
    session: WorkSession
): Long {

    val totalSeconds =
        (
            session.endTime -
            session.startTime
        ) / 1000

    return (
        totalSeconds -
        session.breakDurationSeconds
    ).coerceAtLeast(0)
}

private fun formatBreakDuration(
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

private fun formatDurationCompact(
    totalSeconds: Long
): String {

    val hours =
        totalSeconds / 3600

    val minutes =
        (totalSeconds % 3600) / 60

    val seconds =
        totalSeconds % 60

    return when {

        hours > 0 ->
            if (minutes > 0)
                "${hours}h ${minutes}m"
            else
                "${hours}h"

        minutes > 0 ->
            "${minutes}m"

        else ->
            "${seconds}s"
    }
}

private fun buildWeatherText(
    session: WorkSession
): String {

    val temperature =
        session.temperature?.let {
            "${"%.1f".format(it)} °C"
        }

    val condition =
        session.weatherCode?.let {
            weatherDescription(it)
        }

    return listOfNotNull(
        temperature,
        condition
    ).joinToString(" · ")
}

private fun weatherIcon(
    code: Int?
) =
    when (code) {

        0 ->
            Icons.Default.WbSunny

        1, 2, 3,
        45, 48 ->
            Icons.Default.Cloud

        51, 53, 55,
        56, 57,
        61, 63, 65,
        66, 67,
        80, 81, 82 ->
            Icons.Default.Umbrella

        71, 73, 75, 77,
        85, 86 ->
            Icons.Default.AcUnit

        95, 96, 99 ->
            Icons.Default.Thunderstorm

        else ->
            Icons.Default.Cloud
    }

private fun weatherDescription(
    code: Int
): String =
    when (code) {

        0 -> "Clear"
        1, 2, 3 -> "Cloudy"
        45, 48 -> "Fog"

        51, 53, 55 ->
            "Drizzle"

        56, 57 ->
            "Freezing drizzle"

        61, 63, 65 ->
            "Rain"

        66, 67 ->
            "Freezing rain"

        71, 73, 75, 77 ->
            "Snow"

        80, 81, 82 ->
            "Showers"

        85, 86 ->
            "Snow showers"

        95 ->
            "Thunderstorm"

        96, 99 ->
            "Storm with hail"

        else ->
            "Unknown"
    }

private fun isSameDay(
    first: Long,
    second: Long
): Boolean {

    val formatter =
        SimpleDateFormat(
            "yyyyMMdd",
            Locale.getDefault()
        )

    return formatter.format(
        Date(first)
    ) == formatter.format(
        Date(second)
    )
}

private fun formatSectionDate(
    timestamp: Long
): String {

    val today =
        SimpleDateFormat(
            "yyyyMMdd",
            Locale.getDefault()
        ).format(
            Date()
        )

    val sessionDay =
        SimpleDateFormat(
            "yyyyMMdd",
            Locale.getDefault()
        ).format(
            Date(timestamp)
        )

    return if (today == sessionDay) {
        "TODAY"
    } else {
        SimpleDateFormat(
            "EEEE, dd MMMM",
            Locale.getDefault()
        ).format(
            Date(timestamp)
        ).uppercase()
    }
}

private fun formatDay(
    timestamp: Long
): String =
    SimpleDateFormat(
        "EEEE",
        Locale.getDefault()
    ).format(
        Date(timestamp)
    ).uppercase()

private fun formatDate(
    timestamp: Long
): String =
    SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    ).format(
        Date(timestamp)
    )

private fun formatTime(
    timestamp: Long
): String =
    SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(
        Date(timestamp)
    )

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {

    val now =
        System.currentTimeMillis()

    HourbitTheme {

        HistoryScreen(
            workSessions =
                listOf(
                    WorkSession(
                        id = 1,
                        startTime =
                            now - 9000000,
                        endTime =
                            now,
                        breakDurationSeconds =
                            720,
                        latitude =
                            53.9,
                        longitude =
                            9.5,
                        cityName =
                            "Itzehoe",
                        temperature =
                            17.4,
                        weatherCode =
                            2
                    )
                ),
            onDeleteSession = {}
        )
    }
}
