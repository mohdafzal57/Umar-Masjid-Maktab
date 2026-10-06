package com.example.umarmasjid.domain.repository

import com.example.umarmasjid.data.local_database.model.PrayerTimesEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface PrayerTimesRepository {
    fun observePrayerTime(date: LocalDate): Flow<PrayerTimesEntity?>
    fun observeMonthlyPrayerTimes(year: Int, month: Int): Flow<List<PrayerTimesEntity>>
    suspend fun syncCurrentAndNextMonth()
}
