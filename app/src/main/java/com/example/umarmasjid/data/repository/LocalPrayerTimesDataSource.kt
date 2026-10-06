package com.example.umarmasjid.data.repository

import com.example.umarmasjid.data.local_database.model.PrayerTimesEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface LocalPrayerTimesDataSource {
    fun observePrayerTime(date: LocalDate): Flow<PrayerTimesEntity?>
    fun observeTimes(startDate: LocalDate, endDate: LocalDate): Flow<List<PrayerTimesEntity>>
    suspend fun countTimes(startDate: LocalDate, endDate: LocalDate): Int
    suspend fun upsertAll(timings: List<PrayerTimesEntity>)
    suspend fun deleteOlderThan(date: LocalDate)
}
