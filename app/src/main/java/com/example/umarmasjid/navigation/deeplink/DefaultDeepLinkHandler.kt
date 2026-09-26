package com.example.umarmasjid.navigation.deeplink

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.feature.reset_password.UpdatePasswordNavKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override fun parseDeepLink(uri: Uri): NavKey? {
        val scheme = uri.scheme ?: return null
        val host = uri.host ?: return null

        if (scheme != "umarmasjid") return null

        if (host == "auth") {
            val path = uri.path ?: ""
            val fragment = uri.fragment ?: ""
            val type = uri.getQueryParameter("type") ?: extractFragmentParam(fragment, "type")

            if (path.contains("callback") || path.contains("reset-password") || path.contains("update-password") || type == "recovery") {
                return UpdatePasswordNavKey
            }
        }
        return null
    }

    private fun extractFragmentParam(fragment: String, key: String): String? {
        if (fragment.isEmpty()) return null
        return fragment.split("&")
            .map { it.split("=") }
            .firstOrNull { it.size == 2 && it[0] == key }
            ?.get(1)
    }
}
