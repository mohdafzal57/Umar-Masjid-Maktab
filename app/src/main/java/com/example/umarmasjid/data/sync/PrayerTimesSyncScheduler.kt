package com.example.umarmasjid.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object PrayerTimesSyncScheduler {

    private const val PERIODIC_WORK_NAME =
        "prayer_times_periodic_sync"

    private const val INITIAL_WORK_NAME =
        "prayer_times_initial_sync"

    fun ensureScheduled(context: Context) {
        scheduleInitialSync(context)
        schedulePeriodicSync(context)
    }

    private fun scheduleInitialSync(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request =
            OneTimeWorkRequestBuilder<PrayerTimesSyncWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(
                INITIAL_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                request
            )
    }

    private fun schedulePeriodicSync(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request =
            PeriodicWorkRequestBuilder<PrayerTimesSyncWorker>(
                24,
                TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}