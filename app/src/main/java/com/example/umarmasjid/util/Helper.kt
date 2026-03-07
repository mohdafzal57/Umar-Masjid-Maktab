package com.example.umarmasjid.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.umarmasjid.util.Constants.DATE_PATTERN
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

fun getCurrentFormattedDate(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        currentDate.format(formatter)
    } else {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        formatter.format(calendar.time)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): Long {
    return LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}
/*
fun getCurrentMonthRange(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()

    // Start of month
    calendar.set(Calendar.DAY_OF_MONTH, 10) // 1 for (day 1) but i want (day 10) start of the fee day
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfMonth = calendar.timeInMillis

    // End of month
    calendar.add(Calendar.MONTH, 1)
    calendar.add(Calendar.MILLISECOND, -1)
    val endOfMonth = calendar.timeInMillis

    return Pair(startOfMonth, endOfMonth)
}*/

fun getCurrentMonthRange(): Pair<Long, Long> {
    val now = LocalDate.now()

    // Start of month: 1st day at 00:00:00
    val startOfMonth = now.withDayOfMonth(1)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    // End of month: last day at 23:59:59.999
    val endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
        .atTime(23, 59, 59, 999_000_000) // nanos precision
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    return Pair(startOfMonth, endOfMonth)
}