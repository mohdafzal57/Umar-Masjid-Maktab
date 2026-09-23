package com.example.umarmasjid.di

import com.example.umarmasjid.data.repository.OfflineFirstPrayerTimesRepository
import com.example.umarmasjid.data.repository.DefaultLocationRepository
import com.example.umarmasjid.data.repository.SupabaseAuthRepository
import com.example.umarmasjid.data.sync.DebouncedSyncManager
import com.example.umarmasjid.data.sync.SyncManager
import com.example.umarmasjid.domain.repository.AuthRepository
import com.example.umarmasjid.domain.repository.LocationRepository
import com.example.umarmasjid.domain.repository.PrayerTimesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPrayerTimesRepository(
        impl: OfflineFirstPrayerTimesRepository
    ): PrayerTimesRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: SupabaseAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: DefaultLocationRepository
    ): LocationRepository

    /*@Binds
    @Singleton
    abstract fun bindSyncManager(impl: WorkManagerSyncManager): SyncManager*/

    @Binds
    @Singleton
    abstract fun bindSyncManager(impl: DebouncedSyncManager): SyncManager
}
