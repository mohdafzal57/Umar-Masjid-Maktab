package com.example.umarmasjid.di

import com.example.umarmasjid.data.networking.OfflineFirstPrayerTimesRepository
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
}
