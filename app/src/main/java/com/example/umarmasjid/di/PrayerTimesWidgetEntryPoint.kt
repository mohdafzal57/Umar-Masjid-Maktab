package com.example.umarmasjid.di

import com.example.umarmasjid.data.local_database.dao.PrayerTimesDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PrayerTimesWidgetEntryPoint {
    fun prayerTimesDao(): PrayerTimesDao
}