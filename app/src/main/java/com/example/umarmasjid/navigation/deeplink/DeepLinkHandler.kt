package com.example.umarmasjid.navigation.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey

interface DeepLinkHandler {
    /**
     * Parses an incoming deep link URI into a Navigation 3 NavKey.
     * Returns null if the URI is not recognized by the deep link handler.
     */
    fun parseDeepLink(uri: Uri): NavKey?
}
