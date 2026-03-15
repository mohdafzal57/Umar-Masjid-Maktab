package com.example.umarmasjid.domain.repository

import com.example.umarmasjid.core.domain.util.NetworkError
import com.example.umarmasjid.core.domain.util.Result
import com.example.umarmasjid.domain.model.HijriDate
import com.example.umarmasjid.domain.model.PrayerDay

interface PrayerTimesRepository {

    suspend fun getMonthlyPrayerTimes(
        year: Int,
        month: Int,
    ): Result<List<PrayerDay>, NetworkError>

}