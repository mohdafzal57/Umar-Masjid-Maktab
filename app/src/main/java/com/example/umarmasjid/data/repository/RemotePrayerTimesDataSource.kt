package com.example.umarmasjid.data.repository

import com.example.umarmasjid.core.domain.util.NetworkError
import com.example.umarmasjid.core.domain.util.Result
import com.example.umarmasjid.data.networking.dto.PrayerTimesResponseDto
import com.example.umarmasjid.domain.model.PrayerSettings

interface RemotePrayerTimesDataSource {

    suspend fun getMonthlyPrayerTimes(
        year: Int,
        month: Int,
        prayerSettings: PrayerSettings
    ): Result<PrayerTimesResponseDto, NetworkError>
}