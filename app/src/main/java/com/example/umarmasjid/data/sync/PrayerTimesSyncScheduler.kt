package com.example.umarmasjid.data.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTimesSyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val INITIAL_SYNC = "prayer_times_initial_sync"
        private const val PERIODIC_SYNC = "prayer_times_periodic_sync"
    }

    private fun constraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun requestInitialSync() {
        val request = OneTimeWorkRequestBuilder<PrayerTimesSyncWorker>()
            .setConstraints(constraints())
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            INITIAL_SYNC,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun ensurePeriodicSyncScheduled() {
        val request = PeriodicWorkRequestBuilder<PrayerTimesSyncWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints())
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelPrayerTimesSync() {
        workManager.cancelUniqueWork(INITIAL_SYNC)
        workManager.cancelUniqueWork(PERIODIC_SYNC)
    }
}