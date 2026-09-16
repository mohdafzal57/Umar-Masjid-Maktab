package com.example.umarmasjid.util

import java.time.LocalDate

object DateUtils {
    /*fun getStartOfMonthMillis(): Long {
        val firstDay = LocalDate.now().withDayOfMonth(1)
        return firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun getEndOfMonthMillis(): Long {
        val lastDay = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        return lastDay.atTime(23, 59, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    }*/

    fun getCurrentMonthRange(): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()

        val startOfMonth = today.withDayOfMonth(1)
        val startOfNextMonth = startOfMonth.plusMonths(1)

        return startOfMonth to startOfNextMonth
    }
}
