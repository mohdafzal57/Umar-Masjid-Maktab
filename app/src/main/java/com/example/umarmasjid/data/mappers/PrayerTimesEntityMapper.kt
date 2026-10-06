package com.example.umarmasjid.data.mappers

import com.example.umarmasjid.data.local_database.model.PrayerTimesEntity
import com.example.umarmasjid.data.networking.dto.PrayerDayDto
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
private val GREGORIAN_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy")

private fun parseTime(value: String): LocalTime =
    LocalTime.parse(value.substringBefore(' '), TIME_FORMATTER)


fun PrayerDayDto.toEntity(fetchedAt: Long): PrayerTimesEntity = PrayerTimesEntity(
    date = LocalDate.parse(date.gregorian.date, GREGORIAN_FORMATTER),
    hijriDate = date.hijri.date,
    fajr = parseTime(timings.fajr),
    sunrise = parseTime(timings.sunrise),
    dhuhr = parseTime(timings.dhuhr),
    asr = parseTime(timings.asr),
    sunset = parseTime(timings.sunset),
    maghrib = parseTime(timings.maghrib),
    isha = parseTime(timings.isha),
    fetchedAt = fetchedAt,
)

