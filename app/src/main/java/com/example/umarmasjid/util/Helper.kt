package com.example.umarmasjid.util

import java.time.LocalDate
import java.time.ZoneId

fun currentMonthRange(): Pair<Long, Long> {
    val now = LocalDate.now()
    val start = now.withDayOfMonth(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant().toEpochMilli()
    val end = now.withDayOfMonth(now.lengthOfMonth())
        .atTime(23, 59, 59)
        .atZone(ZoneId.systemDefault())
        .toInstant().toEpochMilli()
    return start to end
}

fun todayRange(): Pair<Long, Long> {
    val start = LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant().toEpochMilli()
    val end = LocalDate.now()
        .plusDays(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant().toEpochMilli() - 1
    return start to end
}