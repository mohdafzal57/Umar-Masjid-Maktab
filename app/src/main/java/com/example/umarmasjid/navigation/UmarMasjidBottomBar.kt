package com.example.umarmasjid.navigation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.umarmasjid.ui.theme.UmarMasjidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UmarMasjidBottomBar(
    currentTopLevelKey: NavKey,
    onNavigate: (NavKey) -> Unit,
) {
    Column {
        GradientLine(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.6.dp),
            lineColor = MaterialTheme.colorScheme.outline,
            centerEmphasisFactor = 0.5f
        )
        ShortNavigationBar (
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                val selected = navKey == currentTopLevelKey
                ShortNavigationBarItem(
                    colors = ShortNavigationBarItemDefaults.colors(
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

        val colorStops = arrayOf(
            0.0f to lineColor.copy(alpha = 0f),
            0.1f to lineColor.copy(alpha = centerAlpha * 0.3f),
            0.3f to lineColor.copy(alpha = centerAlpha),
            0.7f to lineColor.copy(alpha = centerAlpha),
            0.9f to lineColor.copy(alpha = centerAlpha * 0.3f),
            1.0f to lineColor.copy(alpha = 0f)
        )

        val colors = listOf(
            lineColor.copy(alpha = startAlpha),
            lineColor.copy(alpha = centerAlpha),
            lineColor.copy(alpha = startAlpha)
        )

        val brush = Brush.horizontalGradient(
            colorStops = colorStops,
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

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewUmarMasjidBottomBar() {
    // Replace with your actual Theme wrapper
    UmarMasjidTheme {
        Surface {
            UmarMasjidBottomBar(
                currentTopLevelKey = TOP_LEVEL_NAV_ITEMS.keys.first(),
                onNavigate = {}
            )
        }
    }
}
