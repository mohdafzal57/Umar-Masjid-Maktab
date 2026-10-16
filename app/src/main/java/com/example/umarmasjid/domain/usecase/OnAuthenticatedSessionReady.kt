package com.example.umarmasjid.domain.usecase

import com.example.umarmasjid.data.sync.PrayerTimesSyncScheduler
import com.example.umarmasjid.data.sync.SyncManager
import javax.inject.Inject

class OnAuthenticatedSessionReady @Inject constructor(
    private val syncManager: SyncManager,
    private val prayerTimesSyncScheduler: PrayerTimesSyncScheduler,
) {
    suspend operator fun invoke() {
        syncManager.requestSync()
        prayerTimesSyncScheduler.requestInitialSync()
        prayerTimesSyncScheduler.ensurePeriodicSyncScheduled()
    }
}