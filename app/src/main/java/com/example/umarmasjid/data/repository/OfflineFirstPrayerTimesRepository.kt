package com.example.umarmasjid.data.repository

import com.example.umarmasjid.core.data.preferences.PrayerSettingsPreferences
import com.example.umarmasjid.core.domain.util.onSuccess
import com.example.umarmasjid.data.local_database.model.PrayerTimesEntity
import com.example.umarmasjid.data.mappers.toEntity
import com.example.umarmasjid.domain.repository.PrayerTimesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class OfflineFirstPrayerTimesRepository @Inject constructor(
    private val local: LocalPrayerTimesDataSource,
    private val remote: RemotePrayerTimesDataSource,
    private val preferences: PrayerSettingsPreferences,
) : PrayerTimesRepository {

    override fun observePrayerTime(date: LocalDate): Flow<PrayerTimesEntity?> =
        local.observePrayerTime(date)

    override fun observeMonthlyPrayerTimes(
        year: Int,
        month: Int
    ): Flow<List<PrayerTimesEntity>> =
        local.observeTimes(
            YearMonth.of(year, month).atDay(1),
            YearMonth.of(year, month).plusMonths(1).atDay(1)
        )

    override suspend fun syncCurrentAndNextMonth() {
        val now = LocalDate.now()
        syncMonth(YearMonth.from(now))
        syncMonth(YearMonth.from(now).plusMonths(1))
        local.deleteOlderThan(now.minusMonths(2).withDayOfMonth(1))
    }

    private suspend fun syncMonth(month: YearMonth) {
        val start = month.atDay(1)
        val end = month.plusMonths(1).atDay(1)
        val expected = month.lengthOfMonth()
        if (local.countTimes(start, end) == expected) return

        val settings = preferences.settings.first()
        val result = remote.getMonthlyPrayerTimes(
            year = month.year,
            month = month.monthValue,
            prayerSettings = settings,
        )

        result.onSuccess { days ->
            val fetchedAt = System.currentTimeMillis()
            local.upsertAll(days.data.map { it.toEntity(fetchedAt) })
        }
    }
}
