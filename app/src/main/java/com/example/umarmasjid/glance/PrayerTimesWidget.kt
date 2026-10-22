package com.example.umarmasjid.glance

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import com.example.umarmasjid.di.PrayerTimesWidgetEntryPoint
import com.example.umarmasjid.glance.model.PrayerGlanceUiState
import com.example.umarmasjid.glance.theme.PrayerWidgetTheme
import com.example.umarmasjid.glance.ui.PrayerTimesGlanceContent
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PrayerTimesWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext
        val dao = EntryPointAccessors.fromApplication(
            appContext,
            PrayerTimesWidgetEntryPoint::class.java
        ).prayerTimesDao()

        provideContent {
            val uiStateFlow = remember(appContext) {
                val pattern = if (DateFormat.is24HourFormat(appContext)) "HH:mm" else "h:mm"
                val formatter = DateTimeFormatter.ofPattern(pattern)

                dao.observePrayerTime(LocalDate.now()).map { entity ->
                    PrayerGlanceUiState(
                        sunrise = entity?.sunrise?.format(formatter) ?: PrayerGlanceUiState.EMPTY_TIME,
                        sunset = entity?.sunset?.format(formatter) ?: PrayerGlanceUiState.EMPTY_TIME
                    )
                }
            }

            val state by uiStateFlow.collectAsState(initial = PrayerGlanceUiState())

            PrayerWidgetTheme {
                PrayerTimesGlanceContent(state = state)
            }
        }
    }

    companion object {
        suspend fun refresh(context: Context) {
            PrayerTimesWidget().updateAll(context)
        }
    }
}