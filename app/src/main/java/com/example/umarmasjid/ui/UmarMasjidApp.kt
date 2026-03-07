package com.example.umarmasjid.ui

import androidx.compose.runtime.Composable
import com.example.umarmasjid.ui.navigation.UmarMasjidNavGraph
import com.example.umarmasjid.ui.theme.UmarMasjidTheme

@Composable
fun UmarMasjidApp() {
    UmarMasjidTheme {
        UmarMasjidNavGraph()
    }
}