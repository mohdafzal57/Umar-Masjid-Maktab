package com.example.umarmasjid.domain.model

data class PrayerDay(
    val date: PrayerDate,
    val timings: PrayerTimings
)

data class PrayerDate(
    val gregorianDate: String,
    val hijriDate: String
)

data class PrayerTimings(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)