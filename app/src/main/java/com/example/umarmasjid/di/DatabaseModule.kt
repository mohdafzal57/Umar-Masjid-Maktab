package com.example.umarmasjid.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.example.umarmasjid.core.data.preferences.DataStorePrayerSettingsPreferences
import com.example.umarmasjid.core.data.preferences.DataStoreSystemSettingsPreferences
import com.example.umarmasjid.core.data.preferences.PrayerSettingsPreferences
import com.example.umarmasjid.core.data.preferences.SystemSettingsPreferences
import com.example.umarmasjid.core.data.preferences.prayerSettingsDataStore
import com.example.umarmasjid.core.data.preferences.systemSettingsDataStore
import com.example.umarmasjid.data.local_database.AppDatabase
import com.example.umarmasjid.data.local_database.dao.AttendanceDao
import com.example.umarmasjid.data.local_database.dao.PaymentDao
import com.example.umarmasjid.data.local_database.dao.StudentDao
import com.example.umarmasjid.util.Constants.UMAR_MASJID_DATABASE
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PrayerSettingsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SystemSettingsDataStore

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindPrayerSettingsPreferences(
        impl: DataStorePrayerSettingsPreferences
    ): PrayerSettingsPreferences

    @Binds
    @Singleton
    abstract fun bindSystemSettingsPreferences(
        impl: DataStoreSystemSettingsPreferences
    ): SystemSettingsPreferences

    companion object {
        @Provides
        @Singleton
        @PrayerSettingsDataStore
        fun providePrayerSettingsDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = context.prayerSettingsDataStore


        @Provides
        @Singleton
        @SystemSettingsDataStore
        fun provideSystemSettingsDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = context.systemSettingsDataStore

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext app: Context): AppDatabase =
            Room.databaseBuilder(
                app,
                AppDatabase::class.java,
                UMAR_MASJID_DATABASE
            )
                .build()

        @Provides
        fun provideStudentDao(db: AppDatabase): StudentDao = db.studentDao()

        @Provides
        fun provideAttendanceDao(db: AppDatabase): AttendanceDao = db.attendanceDao()

        @Provides
        fun provideFeeDao(db: AppDatabase): PaymentDao = db.feeDao()
    }
}
