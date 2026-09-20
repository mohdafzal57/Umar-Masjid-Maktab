package com.example.umarmasjid.data.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.umarmasjid.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/*@Singleton
class WorkManagerSyncManager @Inject constructor(
    private val workManager: WorkManager
) : SyncManager {

    companion object {
        const val SYNC_WORK_NAME = "SyncOutboxWork"
    }

    override fun requestSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 15,
                timeUnit = TimeUnit.SECONDS
            )
            .addTag("data_sync")
            .build()

        // ExistingWorkPolicy.KEEP ensures only one sync worker runs/queues at a time
        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }
}*/

@Singleton
class DebouncedSyncManager @Inject constructor(
    private val workManager: WorkManager,
    @ApplicationScope private val scope: CoroutineScope
) : SyncManager {

    private val syncRequests = Channel<Unit>(Channel.CONFLATED)

    init {
        scope.launch {
            syncRequests.receiveAsFlow()
                .debounce(1_500L) // Wait 1.5s after the last mutation
                .collect {
                    scheduleWork()
                }
        }
    }

    override fun requestSync() {
        syncRequests.trySend(Unit)
    }

    private fun scheduleWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "SyncOutboxWork",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }
}