package com.example.umarmasjid.ui.bottom_bar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UmarMasjidBottomBar(
    selectedIndex: Int = 0,
    onSelectedItem: (Int) -> Unit,
    navigationItems: List<ModernNavigationItem> = umarMasjidNavigationItems
) {
    Column {
        GradientLine(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            lineColor = MaterialTheme.colorScheme.outline,
            centerEmphasisFactor = 0.5f
        )

        NavigationBar(
            containerColor = Color.Transparent
        ) {
            navigationItems.forEachIndexed { index, item ->
                ModernNavigationBarItem(
                    item = item,
                    isSelected = index == selectedIndex,
                    onClick = { onSelectedItem(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}

@Composable
fun RowScope.ModernNavigationBarItem(
    item: ModernNavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBarItem(
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = Color.Transparent,
        ),
        selected = isSelected,
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                    contentDescription = item.title,
                )
                item.badge?.let {
                    Badge(modifier = Modifier.align(Alignment.TopEnd)) {
                        Text(it)
                    }
                }
            }
        },
        label = { Text(item.title) },
        modifier = modifier
    )
}

@Composable
fun GradientLine(
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.outline,
    centerEmphasisFactor: Float = 0.5f // Controls how much brighter the center is (0.0 to 1.0)
) {
    Canvas(modifier = modifier) {
        val yCenter = size.height / 2f
        val startAlpha = 0.0f // Fully transparent at the edges
        val centerAlpha =
            lineColor.alpha * centerEmphasisFactor.coerceIn(0f, 1f) // More opaque in center

        val brush = Brush.horizontalGradient(
            colors = listOf(
                lineColor.copy(alpha = startAlpha),
                lineColor.copy(alpha = centerAlpha),
                lineColor.copy(alpha = startAlpha)
            ),
            startX = 0f,
            endX = size.width
        )

        drawLine(
            brush = brush,
            start = Offset(0f, yCenter),
            end = Offset(size.width, yCenter),
            strokeWidth = size.height // Use the canvas height as strokeWidth for a solid gradient fill
        )
    }
}
