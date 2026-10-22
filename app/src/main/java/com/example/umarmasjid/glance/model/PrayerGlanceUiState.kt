package com.example.umarmasjid.glance.model

data class PrayerGlanceUiState(
    val sunrise: String = EMPTY_TIME,
    val sunset: String = EMPTY_TIME
) {
    companion object {
        const val EMPTY_TIME = "--:--"
    }
}