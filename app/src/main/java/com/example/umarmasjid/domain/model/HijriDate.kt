package com.example.umarmasjid.domain.model

data class HijriDate(
    val day: Int,
    val monthName: String,
    val year: Int
) {
    val formatted: String
        get() = "$day $monthName $year"
}