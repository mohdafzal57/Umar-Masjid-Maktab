package com.example.umarmasjid.data.sync

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSyncObserver @Inject constructor(
    private val syncManager: SyncManager
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    override fun onStart(owner: LifecycleOwner) {
        scope.launch {
            syncManager.requestSync()
        }
    }
}