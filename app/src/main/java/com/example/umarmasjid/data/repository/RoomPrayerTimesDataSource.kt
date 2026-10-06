package com.example.umarmasjid.data.repository

import com.example.umarmasjid.data.local_database.dao.PrayerTimesDao
import com.example.umarmasjid.data.local_database.model.PrayerTimesEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class RoomPrayerTimesDataSource @Inject constructor(
    private val dao: PrayerTimesDao,
) : LocalPrayerTimesDataSource {

    override fun observePrayerTime(
        date: LocalDate,
    ): Flow<PrayerTimesEntity?> = dao.observePrayerTime(date)

    override fun observeTimes(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<PrayerTimesEntity>> = dao.observeTimes(startDate, endDate)

    override suspend fun countTimes(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Int = dao.countTimes(startDate, endDate)

    override suspend fun upsertAll(
        timings: List<PrayerTimesEntity>,
    ) = dao.upsertAll(timings)

    override suspend fun deleteOlderThan(
        date: LocalDate,
    ) = dao.deleteOlderThan(date)
}

