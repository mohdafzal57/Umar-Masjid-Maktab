package com.example.umarmasjid.ui.bottom_bar

import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DynamicBadge(
    count: Int,
    modifier: Modifier = Modifier,
    showZero: Boolean = false
) {
    if (count > 0 || showZero) {
        Badge(modifier = modifier, containerColor = MaterialTheme.colorScheme.error) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}