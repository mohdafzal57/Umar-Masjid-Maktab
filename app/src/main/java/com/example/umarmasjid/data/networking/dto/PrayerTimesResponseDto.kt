package com.example.umarmasjid.data.networking.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerTimesResponseDto(
    val code: Int,
    val status: String,
    val data: List<PrayerDayDto>
)

@Serializable
data class PrayerDayDto(
    val timings: TimingsDto,
    val date: DateDto
)

@Serializable
data class TimingsDto(
    @SerialName("Fajr")
    val fajr: String,

    @SerialName("Sunrise")
    val sunrise: String,

    @SerialName("Dhuhr")
    val dhuhr: String,

    @SerialName("Asr")
    val asr: String,

    @SerialName("Sunset")
    val sunset: String,

    @SerialName("Maghrib")
    val maghrib: String,

    @SerialName("Isha")
    val isha: String,

    @SerialName("Imsak")
    val imsak: String,

    @SerialName("Midnight")
    val midnight: String,

    @SerialName("Firstthird")
    val firstThird: String,

    @SerialName("Lastthird")
    val lastThird: String
)

@Serializable
data class DateDto(
    val readable: String,
    val timestamp: String,
    val hijri: HijriDateDto,
    val gregorian: GregorianDateDto
)

@Serializable
data class HijriDateDto(
    val date: String,
    val format: String,
    val day: String,
    val weekday: HijriWeekdayDto,
    val month: HijriMonthDto,
    val year: String,
    val designation: HijriDesignationDto,
    val holidays: List<String> = emptyList(),
    val adjustedHolidays: List<String> = emptyList(),
    val method: String
)

@Serializable
data class HijriWeekdayDto(
    val en: String,
    val ar: String
)

@Serializable
data class HijriMonthDto(
    val number: Int,
    val en: String,
    val ar: String,
    val days: Int
)

@Serializable
data class HijriDesignationDto(
    val abbreviated: String,
    val expanded: String
)

@Serializable
data class GregorianDateDto(
    val date: String,
    val format: String,
    val day: String,
    val weekday: GregorianWeekdayDto,
    val month: GregorianMonthDto,
    val year: String,
    val designation: GregorianDesignationDto,
    val lunarSighting: Boolean
)

@Serializable
data class GregorianWeekdayDto(
    val en: String
)

@Serializable
data class GregorianMonthDto(
    val number: Int,
    val en: String
)

@Serializable
data class GregorianDesignationDto(
    val abbreviated: String,
    val expanded: String
)