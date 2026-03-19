package com.example.umarmasjid.data.mappers

import com.example.umarmasjid.data.networking.dto.PrayerDayDto
import com.example.umarmasjid.data.networking.dto.PrayerTimesResponseDto
import com.example.umarmasjid.domain.model.PrayerDate
import com.example.umarmasjid.domain.model.PrayerDay
import com.example.umarmasjid.domain.model.PrayerTimings
import com.example.umarmasjid.feature.home.PrayerTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun PrayerTimesResponseDto.toDomain(): List<PrayerDay> {
    return data.map { it.toDomain() }
}

fun PrayerDayDto.toDomain(): PrayerDay {
    return PrayerDay(
        date = PrayerDate(
            gregorianDate = date.gregorian.date,
            hijriDate = date.hijri.date
        ),
        timings = PrayerTimings(
            fajr = timings.fajr,
            sunrise = timings.sunrise,
            dhuhr = timings.dhuhr,
            asr = timings.asr,
            maghrib = timings.maghrib,
            isha = timings.isha
        )
    )
}

private fun formatPrayerTimes(day: PrayerDay): List<PrayerTime> {
    val t = day.timings

    return listOf(
        PrayerTime("Fajr", t.fajr.to12HourFormat()),
        PrayerTime("Dhuhr", t.dhuhr.to12HourFormat()),
        PrayerTime("Asr", t.asr.to12HourFormat()),
        PrayerTime("Maghrib", t.maghrib.to12HourFormat()),
        PrayerTime("Isha", t.isha.to12HourFormat())
    )
}

fun String.to12HourFormat(): String {
    val clean = substringBefore(" ")

    val formatter24 = DateTimeFormatter.ofPattern("HH:mm")
    val formatter12 = DateTimeFormatter.ofPattern("hh:mm a")

    val time = LocalTime.parse(clean, formatter24)

    return time.format(formatter12)
}
