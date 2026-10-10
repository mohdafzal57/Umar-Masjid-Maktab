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
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

        val currentMonth = YearMonth.from(now)
        val start = currentMonth.atDay(1)
        val endExclusive = currentMonth.plusMonths(2).atDay(1)
        val expected = ChronoUnit.DAYS.between(start, endExclusive).toInt()

        if (local.countTimes(start, endExclusive) == expected) return

        val settings = preferences.settings.first()

        val apiDateFormatter =
            DateTimeFormatter.ofPattern(API_DATE_FORMATTER)

        val result = remote.getPrayerTimesRange(
            startDate = start.format(apiDateFormatter),
            endDate = endExclusive.minusDays(1).format(apiDateFormatter),
            prayerSettings = settings,
        )

        result.onSuccess { days ->
            val fetchedAt = System.currentTimeMillis()
            local.upsertAll(days.data.map { it.toEntity(fetchedAt) })
        }

        local.deleteOlderThan(now.minusMonths(2).withDayOfMonth(1))
    }

    companion object {
        const val API_DATE_FORMATTER = "dd-MM-yyyy"
    }
}
