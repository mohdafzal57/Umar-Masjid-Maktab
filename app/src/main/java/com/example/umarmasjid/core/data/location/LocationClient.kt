package com.example.umarmasjid.core.data.location

import android.location.Location

interface LocationClient {
    suspend fun getCurrentLocation(): Location?
}