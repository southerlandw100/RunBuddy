package com.example.runbuddy

import java.time.LocalDate

fun generateIntermediate1Plan(startMonday: LocalDate): Map<LocalDate, TrainingRun> {

    val plan = mutableMapOf<LocalDate, TrainingRun>()

    fun addWeek(
        mon: TrainingRun,
        tue: TrainingRun,
        wed: TrainingRun,
        thu: TrainingRun,
        fri: TrainingRun,
        sat: TrainingRun,
        sun: TrainingRun,
    ) {
        var d = mon.date
        plan[d] = mon; d = d.plusDays(1)
        plan[d] = tue; d = d.plusDays(1)
        plan[d] = wed; d = d.plusDays(1)
        plan[d] = thu; d = d.plusDays(1)
        plan[d] = fri; d = d.plusDays(1)
        plan[d] = sat; d = d.plusDays(1)
        plan[d] = sun
    }

    // Helper to build a day
    fun day(date: LocalDate, miles: Double?, type: RunType) =
        TrainingRun(date, miles, type)

    // Start Monday
    var mon = startMonday

    // WEEK 1
    addWeek(
        mon = day(mon,        3.0, RunType.EASY),
        tue = day(mon.plusDays(1), 4.0, RunType.EASY),
        wed = day(mon.plusDays(2), 3.0, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), 3.0, RunType.PACE),
        sat = day(mon.plusDays(5), 4.0, RunType.EASY),
        sun = day(mon.plusDays(6), null, RunType.CROSS_TRAIN),  // 30 min cross
    )

    mon = mon.plusWeeks(1)


    // WEEK 2
    addWeek(
        mon = day(mon,        3.0, RunType.EASY),
        tue = day(mon.plusDays(1), 4.0, RunType.PACE),
        wed = day(mon.plusDays(2), 3.0, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), 3.0, RunType.PACE),
        sat = day(mon.plusDays(5), 5.0, RunType.EASY),
        sun = day(mon.plusDays(6), null, RunType.CROSS_TRAIN),
    )

    mon = mon.plusWeeks(1)

    // WEEK 3
    addWeek(
        mon = day(mon,        3.5, RunType.EASY),
        tue = day(mon.plusDays(1), 5.0, RunType.EASY),
        wed = day(mon.plusDays(2), 3.5, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), null, RunType.REST),
        sat = day(mon.plusDays(5), 6.0, RunType.EASY),
        sun = day(mon.plusDays(6), null, RunType.CROSS_TRAIN),
    )

    mon = mon.plusWeeks(1)

    // WEEK 4
    addWeek(
        mon = day(mon,        3.5, RunType.EASY),
        tue = day(mon.plusDays(1), 5.0, RunType.PACE),
        wed = day(mon.plusDays(2), 3.5, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), 3.0, RunType.EASY),
        sat = day(mon.plusDays(5), 7.0, RunType.EASY),
        sun = day(mon.plusDays(6), null, RunType.CROSS_TRAIN),
    )

    mon = mon.plusWeeks(1)

    // WEEK 5
    addWeek(
        mon = day(mon,        4.0, RunType.EASY),
        tue = day(mon.plusDays(1), 6.0, RunType.EASY),
        wed = day(mon.plusDays(2), 4.0, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), 3.0, RunType.PACE),
        sat = day(mon.plusDays(5), 8.0, RunType.EASY),
        sun = day(mon.plusDays(6), null, RunType.CROSS_TRAIN),
    )

    mon = mon.plusWeeks(1)

    // WEEK 6
    addWeek(
        mon = day(mon,        4.0, RunType.EASY),
        tue = day(mon.plusDays(1), 6.0, RunType.PACE),
        wed = day(mon.plusDays(2), 4.0, RunType.EASY),
        thu = day(mon.plusDays(3), 2.0, RunType.EASY), // "easy or rest" → choose easy 2 mi
        fri = day(mon.plusDays(4), null, RunType.REST),
        sat = day(mon.plusDays(5), 3.1, RunType.LONG_RUN), // 5K
        sun = day(mon.plusDays(6), null, RunType.CROSS_TRAIN),
    )

    mon = mon.plusWeeks(1)

    // WEEK 7
    addWeek(
        mon = day(mon,        4.5, RunType.EASY),
        tue = day(mon.plusDays(1), 7.0, RunType.EASY),
        wed = day(mon.plusDays(2), 4.5, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), 4.0, RunType.PACE),
        sat = day(mon.plusDays(5), 9.0, RunType.EASY),
        sun = day(mon.plusDays(6), null, RunType.REST),
    )

    mon = mon.plusWeeks(1)

    // WEEK 8
    addWeek(
        mon = day(mon,        4.5, RunType.EASY),
        tue = day(mon.plusDays(1), 7.0, RunType.PACE),
        wed = day(mon.plusDays(2), 4.5, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), 5.0, RunType.PACE),
        sat = day(mon.plusDays(5), 10.0, RunType.EASY),
        sun = day(mon.plusDays(6), null, RunType.CROSS_TRAIN),
    )

    mon = mon.plusWeeks(1)

    // WEEK 9 — includes 10K race
    addWeek(
        mon = day(mon,        5.0, RunType.EASY),
        tue = day(mon.plusDays(1), 8.0, RunType.EASY),
        wed = day(mon.plusDays(2), 5.0, RunType.EASY),
        thu = day(mon.plusDays(3), 2.0, RunType.EASY), // "rest or easy" → 2 mi
        fri = day(mon.plusDays(4), null, RunType.REST),
        sat = day(mon.plusDays(5), 6.2, RunType.LONG_RUN), // 10K
        sun = day(mon.plusDays(6), null, RunType.CROSS_TRAIN),
    )

    mon = mon.plusWeeks(1)

    // WEEK 10
    addWeek(
        mon = day(mon,        5.0, RunType.EASY),
        tue = day(mon.plusDays(1), 8.0, RunType.PACE),
        wed = day(mon.plusDays(2), 5.0, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), 5.0, RunType.PACE),
        sat = day(mon.plusDays(5), 11.0, RunType.EASY),
        sun = day(mon.plusDays(6), null, RunType.REST),
    )

    mon = mon.plusWeeks(1)

    // WEEK 11
    addWeek(
        mon = day(mon,        5.0, RunType.EASY),
        tue = day(mon.plusDays(1), 6.0, RunType.EASY),
        wed = day(mon.plusDays(2), 4.0, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), 3.0, RunType.PACE),
        sat = day(mon.plusDays(5), 12.0, RunType.EASY),
        sun = day(mon.plusDays(6), null, RunType.CROSS_TRAIN),
    )

    mon = mon.plusWeeks(1)

    // WEEK 12
    addWeek(
        mon = day(mon,        4.0, RunType.EASY),
        tue = day(mon.plusDays(1), 4.0, RunType.PACE),
        wed = day(mon.plusDays(2), 2.0, RunType.EASY),
        thu = day(mon.plusDays(3), null, RunType.REST),
        fri = day(mon.plusDays(4), null, RunType.REST),
        sat = day(mon.plusDays(5), 13.1, RunType.LONG_RUN),  // Half Marathon
        sun = day(mon.plusDays(6), null, RunType.REST),
    )

    return plan
}
