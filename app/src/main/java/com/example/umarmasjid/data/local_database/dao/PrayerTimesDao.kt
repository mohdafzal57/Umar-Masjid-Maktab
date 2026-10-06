package com.example.umarmasjid.data.local_database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.umarmasjid.data.local_database.model.PrayerTimesEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PrayerTimesDao {

    @Query("SELECT * FROM prayer_times WHERE date = :date LIMIT 1")
    fun observePrayerTime(date: LocalDate): Flow<PrayerTimesEntity?>

    @Query("SELECT * FROM prayer_times WHERE date >= :startDate AND date < :endDate ORDER BY date")
    fun observeTimes(startDate: LocalDate, endDate: LocalDate): Flow<List<PrayerTimesEntity>>

    @Query("SELECT COUNT(*) FROM prayer_times WHERE date >= :startDate AND date < :endDate")
    suspend fun countTimes(startDate: LocalDate, endDate: LocalDate): Int

    @Upsert
    suspend fun upsertAll(timings: List<PrayerTimesEntity>)

    @Query("DELETE FROM prayer_times WHERE date < :date")
    suspend fun deleteOlderThan(date: LocalDate)

    @Query("DELETE FROM prayer_times")
    suspend fun deleteAll()
}
