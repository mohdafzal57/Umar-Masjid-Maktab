package com.example.umarmasjid.data.repository

import com.example.umarmasjid.core.data.preferences.PrayerSettingsPreferences
import com.example.umarmasjid.core.domain.util.NetworkError
import com.example.umarmasjid.core.domain.util.Result
import com.example.umarmasjid.core.domain.util.map
import com.example.umarmasjid.data.mappers.toDomain
import com.example.umarmasjid.domain.model.PrayerDay
import com.example.umarmasjid.domain.repository.PrayerTimesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OfflineFirstPrayerTimesRepository @Inject constructor(
    private val remote: RemotePrayerTimesDataSource,
    private val preferences: PrayerSettingsPreferences,
) : PrayerTimesRepository {

    override suspend fun getMonthlyPrayerTimes(
        year: Int,
        month: Int,
    ): Result<List<PrayerDay>, NetworkError> {

        val settings = preferences.settings.first()

        return remote.getMonthlyPrayerTimes(
            year = year,
            month = month,
            prayerSettings = settings
        ).map { it.toDomain() }
    }


}