package com.example.umarmasjid.data.mappers

import com.example.umarmasjid.data.networking.dto.PrayerDayDto
import com.example.umarmasjid.data.networking.dto.PrayerTimesResponseDto
import com.example.umarmasjid.domain.model.PrayerDate
import com.example.umarmasjid.domain.model.PrayerDay
import com.example.umarmasjid.domain.model.PrayerTimings

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