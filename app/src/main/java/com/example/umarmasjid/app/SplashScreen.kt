package com.example.umarmasjid.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.umarmasjid.R
import com.example.umarmasjid.ui.theme.UmarMasjidTheme

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    UmarMasjidTheme {
        Scaffold { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.umar_masjid_ic),
                    contentDescription = "umar_masjid_icon"
                )
                Spacer(
                    modifier = Modifier.padding(10.dp),
                )
                Text(
                    text = "Loading...",
                    fontSize = TextUnit(20f, TextUnitType(20))
                )
            }
        }
    }
}