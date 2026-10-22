package com.example.umarmasjid.glance.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.umarmasjid.R
import com.example.umarmasjid.glance.model.PrayerGlanceUiState

@Composable
fun PrayerTimesGlanceContent(state: PrayerGlanceUiState) {
    val context = LocalContext.current
    val accessibilityDescription = context.getString(
        R.string.cd_prayer_times,
        state.sunrise,
        state.sunset
    )

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(16.dp)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .semantics { contentDescription = accessibilityDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "●",
            style = monoStyle(GlanceTheme.colors.primary, 7.sp)
        )
        Spacer(GlanceModifier.width(5.dp))
        TimeCell(arrow = "↑", time = state.sunrise)

        Spacer(GlanceModifier.width(8.dp))
        Text(
            text = "· · ·",
            style = monoStyle(GlanceTheme.colors.onSurfaceVariant, 12.sp)
        )
        Spacer(GlanceModifier.width(8.dp))

        TimeCell(arrow = "↓", time = state.sunset)
    }
}

@Composable
private fun TimeCell(arrow: String, time: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = arrow,
            style = monoStyle(GlanceTheme.colors.primary, 14.sp, FontWeight.Bold)
        )
        Spacer(GlanceModifier.width(3.dp))
        Text(
            text = time,
            maxLines = 1,
            style = monoStyle(GlanceTheme.colors.onSurface, 16.sp, FontWeight.Bold)
        )
    }
}

private fun monoStyle(
    color: ColorProvider,
    size: TextUnit,
    weight: FontWeight = FontWeight.Normal
) = TextStyle(
    color = color,
    fontSize = size,
    fontWeight = weight,
    fontFamily = FontFamily.Monospace
)