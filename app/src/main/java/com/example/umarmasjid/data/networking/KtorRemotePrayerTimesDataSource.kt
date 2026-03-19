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
import java.util.Locale
import javax.inject.Inject

class KtorRemotePrayerTimesDataSource @Inject constructor(
    private val httpClient: HttpClient
) : RemotePrayerTimesDataSource {

    override suspend fun getMonthlyPrayerTimes(
        year: Int,
        month: Int,
        prayerSettings: PrayerSettings
    ): Result<PrayerTimesResponseDto, NetworkError> {

        return safeCall {

            val useCoordinates = prayerSettings.latitude != null && prayerSettings.longitude != null

            val endpoint = if (useCoordinates) {
                "calendar/$year/$month"
            } else {
                "calendarByCity/$year/$month"
            }

            val url = constructUrl(endpoint)

            httpClient.get(url) {

                if (useCoordinates) {

                    parameter("latitude", prayerSettings.latitude)
                    parameter("longitude", prayerSettings.longitude)

                } else {

                    val (city, country) = resolveCityCountry(prayerSettings)

                    parameter("city", city)
                    parameter("country", country)
                }

                // ---- COMMON PARAMETERS ----
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

val countryCityMap = mapOf(
    "India" to "Delhi",
    "Pakistan" to "Islamabad",
    "Bangladesh" to "Dhaka",
    "Saudi Arabia" to "Riyadh",
    "United Arab Emirates" to "Abu Dhabi",
    "Qatar" to "Doha",
    "Kuwait" to "Kuwait City",
    "Oman" to "Muscat",
    "Turkey" to "Ankara",
    "Indonesia" to "Jakarta",
    "Malaysia" to "Kuala Lumpur",
    "Egypt" to "Cairo",
    "Morocco" to "Rabat",
    "Algeria" to "Algiers",
    "Tunisia" to "Tunis",
    "United Kingdom" to "London",
    "United States" to "Washington",
    "Canada" to "Ottawa",
    "France" to "Paris",
    "Germany" to "Berlin",
    "Russia" to "Moscow",
    "China" to "Beijing",
    "Japan" to "Tokyo",
    "Australia" to "Canberra"
)

fun resolveCityCountry(settings: PrayerSettings): Pair<String, String> {
    val locale = Locale.getDefault()
    val country = settings.country ?: locale.displayCountry ?: "India"

    val city = settings.city
        ?: countryCityMap[country]
        ?: "Delhi"

    return city to country
}