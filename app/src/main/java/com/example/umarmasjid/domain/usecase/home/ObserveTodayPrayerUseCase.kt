package com.example.umarmasjid.domain.usecase.home

import com.example.umarmasjid.data.local_database.model.PrayerTimesEntity
import com.example.umarmasjid.domain.repository.PrayerTimesRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class ObserveTodayPrayerUseCase @Inject constructor(
    private val repository: PrayerTimesRepository,
) {
    operator fun invoke(date: LocalDate = LocalDate.now()): Flow<PrayerTimesEntity?> =
        repository.observePrayerTime(date)
}
