package com.example.umarmasjid.di

import com.example.umarmasjid.core.data.networking.HttpClientFactory
import com.example.umarmasjid.data.networking.KtorRemotePrayerTimesDataSource
import com.example.umarmasjid.data.repository.RemotePrayerTimesDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindRemotePrayerTimesDataSource(
        impl: KtorRemotePrayerTimesDataSource
    ): RemotePrayerTimesDataSource

    companion object {
        @Provides
        @Singleton
        fun provideHttpClient(): HttpClient {
            return HttpClientFactory.create(CIO.create())
        }
    }
}
