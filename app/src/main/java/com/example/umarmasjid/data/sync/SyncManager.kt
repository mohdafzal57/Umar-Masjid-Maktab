package com.example.umarmasjid.data.sync

interface SyncManager {
    fun requestSync()
    // fun cancelAllSyncs() // workManager.cancelUniqueWork("PERIODIC_SYNC")
}