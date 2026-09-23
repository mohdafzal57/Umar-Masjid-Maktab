package com.example.umarmasjid.domain.repository

interface LocationRepository {
    suspend fun fetchAndSaveCurrentLocation(markInitialized: Boolean = true): Boolean
}
