package com.example.umarmasjid.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation3.runtime.NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UmarMasjidBottomBar(
    scrollBehavior: BottomAppBarScrollBehavior,
    currentTopLevelKey: NavKey,
    onNavigate: (NavKey) -> Unit,
) {
    Column {
        GradientLine(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            lineColor = Color.LightGray,
            centerEmphasisFactor = 0.5f
        )
        BottomAppBar(
            scrollBehavior = scrollBehavior,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                val selected = navKey == currentTopLevelKey
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    selected = selected,
                    onClick = {onNavigate(navKey)},
                    icon = {
                        Box(
                            modifier = Modifier,
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selected) navItem.selectedIcon else navItem.unselectedIcon,
                                contentDescription = navItem.title,
                            )
                            navItem.badge?.let {
                                Badge(modifier = Modifier.align(Alignment.TopEnd)) {
                                    Text(it)
                                }
                            }
                        }
                    },
                    label = { Text(navItem.title) },
                )
            }
        }
    }
}


@Composable
fun GradientLine(
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.outline,
    centerEmphasisFactor: Float = 0.5f
) {
    Canvas(modifier = modifier) {
        val yCenter = size.height / 2f
        val startAlpha = 0f
        val centerAlpha = lineColor.alpha * centerEmphasisFactor.coerceIn(0f, 1f)

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
            strokeWidth = size.height
        )
    }
}