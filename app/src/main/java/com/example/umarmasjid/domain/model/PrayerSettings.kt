package com.example.umarmasjid.domain.model

data class PrayerSettings(
    val latitude: Double?,
    val longitude: Double?,
    val cityName: String?,
    val method: Int,
    val school: Int,
    val midnightMode: Int,
    val latitudeAdjustmentMethod: Int?,
    val calendarMethod: String,
    val timezone: String,
    val shafaq: String?,
    val tune: String?,
    val adjustment: Int?,
    val iso8601: Boolean
)