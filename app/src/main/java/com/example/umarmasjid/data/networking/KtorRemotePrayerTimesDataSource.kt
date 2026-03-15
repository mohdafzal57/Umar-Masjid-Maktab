package com.example.umarmasjid.data.networking

import com.example.umarmasjid.core.data.networking.constructUrl
import com.example.umarmasjid.core.data.networking.safeCall
import com.example.umarmasjid.core.domain.util.NetworkError
import com.example.umarmasjid.core.domain.util.Result
import com.example.umarmasjid.data.networking.dto.PrayerTimesResponseDto
import com.example.umarmasjid.data.repository.RemotePrayerTimesDataSource
import com.example.umarmasjid.domain.model.PrayerSettings
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class KtorRemotePrayerTimesDataSource @Inject constructor(
    private val httpClient: HttpClient
) : RemotePrayerTimesDataSource {

    override suspend fun getMonthlyPrayerTimes(
        year: Int,
        month: Int,
        prayerSettings: PrayerSettings
    ): Result<PrayerTimesResponseDto, NetworkError> {

        return safeCall<PrayerTimesResponseDto> {

            val url = constructUrl("calendar/${year}/${month}")

            httpClient.get(url) {
                parameter("latitude", prayerSettings.latitude)
                parameter("longitude", prayerSettings.longitude)

                parameter("method", prayerSettings.method)
                parameter("school", prayerSettings.school)
                parameter("midnightMode", prayerSettings.midnightMode)
                parameter("calendarMethod", prayerSettings.calendarMethod)
                parameter("timezonestring", prayerSettings.timezone)
                parameter("iso8601", prayerSettings.iso8601)

                prayerSettings.latitudeAdjustmentMethod?.let {
                    parameter("latitudeAdjustmentMethod", it)
                }

                prayerSettings.shafaq?.let {
                    parameter("shafaq", it)
                }

                prayerSettings.tune?.let {
                    parameter("tune", it)
                }

                prayerSettings.adjustment?.let {
                    parameter("adjustment", it)
                }
            }
        }
    }
}