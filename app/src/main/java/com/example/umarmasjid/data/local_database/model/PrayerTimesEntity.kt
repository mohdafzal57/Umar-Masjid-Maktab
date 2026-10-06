package com.example.umarmasjid.data.local_database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "prayer_times")
data class PrayerTimesEntity(
    @PrimaryKey val date: LocalDate,
    val hijriDate: String,
    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val sunset: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime,
    val fetchedAt: Long,
)
