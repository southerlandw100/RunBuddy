package com.example.runbuddy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunCalendarScreen(
    viewModel: RunCalendarViewModel = viewModel(),
) {
    // React to changes in run statuses and push-back operations.
    val runsByDate by viewModel.runsByDate.collectAsState()

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(3) }
    val endMonth = remember { currentMonth.plusMonths(3) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY) }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
    )

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var isSheetOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        val visibleMonth = calendarState.firstVisibleMonth.yearMonth
        Text(
            text = "${visibleMonth.month} ${visibleMonth.year}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
        ) {
            daysOfWeek.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.name.take(3),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        HorizontalCalendar(
            modifier = Modifier.fillMaxWidth(),
            state = calendarState,
            dayContent = { day ->
                val runForDay = runsByDate[day.date]

                DayCell(
                    day = day,
                    isSelected = selectedDate == day.date,
                    run = runForDay,
                    onClick = { clicked ->
                        if (clicked.position == DayPosition.MonthDate) {
                            selectedDate = clicked.date
                            if (runForDay != null) {
                                isSheetOpen = true
                            }
                        }
                    },
                )
            },
        )
    }

    // Bottom sheet for the currently selected training day.
    if (isSheetOpen && selectedDate != null) {
        val date = selectedDate!!
        val run = runsByDate[date]
        if (run != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            ModalBottomSheet(
                onDismissRequest = { isSheetOpen = false },
                sheetState = sheetState,
            ) {
                BottomSheetContent(
                    run = run,
                    onMarkComplete = {
                        viewModel.markComplete(date)
                        isSheetOpen = false
                    },
                    onSkip = {
                        viewModel.skip(date)
                        isSheetOpen = false
                    },
                    onPushBackOneDay = {
                        viewModel.pushBackOneDay(date)
                        isSheetOpen = false
                        selectedDate = null
                    },
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    run: TrainingRun?,
    onClick: (CalendarDay) -> Unit,
) {
    val isOutOfMonth = day.position != DayPosition.MonthDate

    val backgroundColor = when {
        isOutOfMonth -> MaterialTheme.colorScheme.surface
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        run == null -> MaterialTheme.colorScheme.surface
        run.status == RunStatus.COMPLETED ->
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        run.status == RunStatus.SKIPPED ->
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
    }

    val baseModifier = Modifier
        .aspectRatio(1f)
        .padding(2.dp)

    val clickableModifier = if (!isOutOfMonth) {
        baseModifier
            .clickable { onClick(day) }
            .background(backgroundColor)
    } else {
        baseModifier.background(backgroundColor)
    }

    Box(
        modifier = clickableModifier,
        contentAlignment = Alignment.Center,
    ) {
        if (!isOutOfMonth) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Day number
                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodySmall,
                )

                if (run != null) {
                    Spacer(modifier = Modifier.height(2.dp))

                    // Distance text (e.g., "5 mi" or "3.1 mi")
                    run.distanceMiles?.let { miles ->
                        val milesText = if (miles % 1.0 == 0.0) {
                            "${miles.toInt()} mi"
                        } else {
                            "${miles} mi"
                        }

                        Text(
                            text = milesText,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    } ?: runLabelForType(run)?.let { label ->
                        // For XT / Rest days
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }

                    // Short type label
                    val typeLabel = when (run.type) {
                        RunType.PACE -> "Pace"
                        RunType.LONG_RUN -> "Long"
                        RunType.CROSS_TRAIN -> "XT"
                        RunType.REST -> "Rest"
                        RunType.EASY -> "Easy"
                        RunType.TEMPO -> "Tempo"
                        RunType.INTERVAL -> "Int"
                    }

                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

private fun runLabelForType(run: TrainingRun): String? {
    return when (run.type) {
        RunType.CROSS_TRAIN -> "XT"
        RunType.REST -> "Rest"
        else -> null
    }
}

@Composable
private fun BottomSheetContent(
    run: TrainingRun,
    onMarkComplete: () -> Unit,
    onSkip: () -> Unit,
    onPushBackOneDay: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = run.date.toString(),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(4.dp))

        val distanceText = run.distanceMiles?.let { miles ->
            if (miles % 1.0 == 0.0) {
                "${miles.toInt()} mi"
            } else {
                "${miles} mi"
            }
        } ?: "No distance"

        Text(
            text = "$distanceText • ${run.type}",
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Status: ${run.status}",
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onMarkComplete,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Mark as complete")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Skip this run")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onPushBackOneDay,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Push back 1 day")
        }
    }
}
