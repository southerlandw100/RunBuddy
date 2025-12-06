package com.example.runbuddy

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class RunCalendarViewModel : ViewModel() {

    // TODO: set this to the real start Monday of your 12-week plan.
    private val startMonday: LocalDate = LocalDate.of(2025, 12, 15)

    // All runs keyed by date, with status in TrainingRun.
    private val _runsByDate = MutableStateFlow(
        generateIntermediate1Plan(startMonday)
    )
    val runsByDate: StateFlow<Map<LocalDate, TrainingRun>> = _runsByDate.asStateFlow()

    fun getRunFor(date: LocalDate): TrainingRun? = _runsByDate.value[date]

    fun markComplete(date: LocalDate) {
        updateRun(date) { it.copy(status = RunStatus.COMPLETED) }
    }

    fun skip(date: LocalDate) {
        updateRun(date) { it.copy(status = RunStatus.SKIPPED) }
    }

    fun resetToScheduled(date: LocalDate) {
        updateRun(date) { it.copy(status = RunStatus.SCHEDULED) }
    }

    fun pushBackOneDay(date: LocalDate) {
        val current = _runsByDate.value
        val run = current[date] ?: return

        val newDate = date.plusDays(1)
        val mutable = current.toMutableMap()

        // Remove old mapping and insert at new date.
        mutable.remove(date)
        val newRun = run.copy(date = newDate)
        mutable[newDate] = newRun

        _runsByDate.value = mutable
    }

    private fun updateRun(
        date: LocalDate,
        transform: (TrainingRun) -> TrainingRun,
    ) {
        val current = _runsByDate.value
        val run = current[date] ?: return

        val mutable = current.toMutableMap()
        mutable[date] = transform(run)
        _runsByDate.value = mutable
    }
}
