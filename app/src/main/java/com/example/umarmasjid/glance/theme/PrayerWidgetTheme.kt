package com.example.umarmasjid.glance.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.example.umarmasjid.ui.theme.backgroundDark
import com.example.umarmasjid.ui.theme.backgroundLight
import com.example.umarmasjid.ui.theme.onSurfaceDark
import com.example.umarmasjid.ui.theme.onSurfaceLight
import com.example.umarmasjid.ui.theme.onSurfaceVariantDark
import com.example.umarmasjid.ui.theme.onSurfaceVariantLight
import com.example.umarmasjid.ui.theme.primaryDark
import com.example.umarmasjid.ui.theme.primaryLight

private val WidgetLightColorScheme = lightColorScheme(
    primary = primaryLight,
    background = backgroundLight,
    surface = backgroundLight,
    onSurface = onSurfaceLight,
    onSurfaceVariant = onSurfaceVariantLight
)

private val WidgetDarkColorScheme = darkColorScheme(
    primary = primaryDark,
    background = backgroundDark,
    surface = backgroundDark,
    onSurface = onSurfaceDark,
    onSurfaceVariant = onSurfaceVariantDark
)

val WidgetColorProviders = ColorProviders(
    light = WidgetLightColorScheme,
    dark = WidgetDarkColorScheme
)

@Composable
fun PrayerWidgetTheme(content: @Composable () -> Unit) {
    GlanceTheme(
        colors = WidgetColorProviders,
        content = content
    )
}