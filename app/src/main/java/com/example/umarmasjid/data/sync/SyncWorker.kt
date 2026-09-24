package com.example.umarmasjid.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okio.IOException

const val TAG: String = "SyncWorker"

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            syncRepository.sync()
            Result.success()
        } catch (e: IOException) {
            Log.e(TAG, "Temporary network failure", e)
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed!", e)
            Result.failure()
        }
    }
}
