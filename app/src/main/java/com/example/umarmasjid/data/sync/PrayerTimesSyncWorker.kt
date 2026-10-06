package com.example.umarmasjid.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.umarmasjid.domain.repository.PrayerTimesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PrayerTimesSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PrayerTimesRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = runCatching {
        repository.syncCurrentAndNextMonth()
    }.fold(
        onSuccess = { Result.success() },
        onFailure = { Result.retry() },
    )
}