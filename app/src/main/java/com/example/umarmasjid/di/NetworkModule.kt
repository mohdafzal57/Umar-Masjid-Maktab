package com.example.umarmasjid.di

import com.example.umarmasjid.BuildConfig
import com.example.umarmasjid.core.data.networking.HttpClientFactory
import com.example.umarmasjid.data.networking.KtorRemotePrayerTimesDataSource
import com.example.umarmasjid.data.repository.RemotePrayerTimesDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
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
            return HttpClientFactory.create(OkHttp.create())
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth) {
                scheme = "umarmasjid"
                host = "auth"
            }
            install(Postgrest)
            // Add other Supabase plugins here
        }
    }
}
