package com.example.runbuddy

import java.time.LocalDate

enum class RunType {
    EASY,
    TEMPO,
    INTERVAL,
    LONG_RUN,
    PACE,
    CROSS_TRAIN,
    REST,
}

enum class RunStatus {
    SCHEDULED,
    COMPLETED,
    SKIPPED,
}

data class TrainingRun (
    val date: LocalDate,
    val distanceMiles: Double?, // null option for rest days or cross training
    val type: RunType,
    val status: RunStatus = RunStatus.SCHEDULED,
)