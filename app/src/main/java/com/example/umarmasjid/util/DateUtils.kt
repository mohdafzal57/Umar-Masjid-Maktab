package com.example.umarmasjid.util

import java.time.LocalDate
import java.time.ZoneId

object DateUtils {
    fun getStartOfMonthMillis(): Long {
        val firstDay = LocalDate.now().withDayOfMonth(1)
        return firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun getEndOfMonthMillis(): Long {
        val lastDay = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        return lastDay.atTime(23, 59, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    }
}
