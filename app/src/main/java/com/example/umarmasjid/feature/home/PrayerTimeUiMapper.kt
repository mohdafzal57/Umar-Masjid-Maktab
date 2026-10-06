package com.example.umarmasjid.feature.home

import com.example.umarmasjid.data.local_database.model.PrayerTimesEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object PrayerTimeUiMapper {
    private val displayFormatter =
        DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

    fun map(
        prayer: PrayerTimesEntity,
        now: LocalTime = LocalTime.now(),
    ): PrayerResult {
        val prayers = listOf(
            "Fajr" to prayer.fajr,
            "Dhuhr" to prayer.dhuhr,
            "Asr" to prayer.asr,
            "Maghrib" to prayer.maghrib,
            "Isha" to prayer.isha,
        )

        val nextPrayerIndex = prayers
            .indexOfFirst { (_, time) -> time.isAfter(now) }
            .let { if (it == -1) 0 else it }

        return PrayerResult(
            prayerTimes = prayers.mapIndexed { index, (name, time) ->
                PrayerTime(
                    name = name,
                    time = time.format(displayFormatter),
                    isNext = index == nextPrayerIndex,
                )
            },
            islamicDate = IslamicDate(
                hijriDate = formatHijri(prayer.hijriDate),
                gregorianDate = formatGregorian(prayer.date),
            ),
        )
    }

    private fun formatGregorian(date: LocalDate): String =
        date.format(
            DateTimeFormatter.ofPattern(
                "EEEE, MMMM d, yyyy",
                Locale.ENGLISH,
            )
        )

    private fun formatHijri(date: String): String = try {
        val parts = date.split("-")
        val day = parts[0].toInt()
        val monthIndex = parts[1].toInt() - 1
        val year = parts[2]
        "$day ${hijriMonths.getOrElse(monthIndex) { parts[1] }} $year"
    } catch (_: Exception) {
        date
    }

    private val hijriMonths = listOf(
        "Muharram", "Safar", "Rabi al-Awwal", "Rabi al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qadah", "Dhu al-Hijjah",
    )
}
