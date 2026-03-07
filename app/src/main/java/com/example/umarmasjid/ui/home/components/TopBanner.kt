package com.example.umarmasjid.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.umarmasjid.R

@Composable
fun TopBanner() {
    Box {
        Image(
            painter = painterResource(id = R.drawable.top_banner),
            contentDescription = "top banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
