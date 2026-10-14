package com.example.umarmasjid.navigation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.umarmasjid.ui.theme.UmarMasjidTheme

/**
 * Umar Masjid navigation bar item with icon and label content slots. Wraps Material 3
 * [NavigationBarItem].
 */
@Composable
fun RowScope.UmarMasjidNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = UmarMasjidNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            selectedTextColor = UmarMasjidNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            indicatorColor = UmarMasjidNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Umar Masjid navigation bar with content slot. Wraps Material 3 [NavigationBar].
 */
@Composable
fun UmarMasjidNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = UmarMasjidNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

/**
 * Umar Masjid navigation rail item with icon and label content slots. Wraps Material 3
 * [NavigationRailItem].
 */
@Composable
fun UmarMasjidNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = UmarMasjidNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            selectedTextColor = UmarMasjidNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            indicatorColor = UmarMasjidNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Umar Masjid navigation rail with header and content slots. Wraps Material 3 [NavigationRail].
 */
@Composable
fun UmarMasjidNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = UmarMasjidNavigationDefaults.navigationContentColor(),
        header = header,
        content = content,
    )
}

/**
 * Umar Masjid navigation suite scaffold with item and content slots.
 * Wraps Material 3 [NavigationSuiteScaffold].
 */
@Composable
fun UmarMasjidNavigationSuiteScaffold(
    navigationSuiteItems: UmarMasjidNavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2(),
    content: @Composable () -> Unit,
) {
    val layoutType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo(windowAdaptiveInfo)
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = UmarMasjidNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            selectedTextColor = UmarMasjidNavigationDefaults.navigationSelectedTextColor(),
            unselectedTextColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            indicatorColor = UmarMasjidNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = UmarMasjidNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            selectedTextColor = UmarMasjidNavigationDefaults.navigationSelectedTextColor(),
            unselectedTextColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            indicatorColor = UmarMasjidNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = UmarMasjidNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            selectedTextColor = UmarMasjidNavigationDefaults.navigationSelectedTextColor(),
            unselectedTextColor = UmarMasjidNavigationDefaults.navigationContentColor(),
        ),
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            UmarMasjidNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(navigationSuiteItems)
        },
        layoutType = layoutType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContentColor = UmarMasjidNavigationDefaults.navigationContentColor(),
            navigationRailContainerColor = Color.Transparent,
        ),
        modifier = modifier,
    ) {
        content()
    }
}

/**
 * A wrapper around [NavigationSuiteScope] to declare navigation items.
 */
class UmarMasjidNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable (() -> Unit)? = null,
        badge: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = {
            if (badge != null) {
                Box(contentAlignment = Alignment.Center) {
                    if (selected) selectedIcon() else icon()
                    Box(modifier = Modifier.align(Alignment.TopEnd)) {
                        badge()
                    }
                }
            } else {
                if (selected) {
                    selectedIcon()
                } else {
                    icon()
                }
            }
        },
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )
}

/**
 * Umar Masjid navigation default values.
 */
object UmarMasjidNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer

    @Composable
    fun navigationSelectedTextColor() = MaterialTheme.colorScheme.onSurface
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun UmarMasjidNavigationBarPreview() {
    val items = TOP_LEVEL_NAV_ITEMS.values.toList()

    UmarMasjidTheme {
        UmarMasjidNavigationBar {
            items.forEachIndexed { index, item ->
                UmarMasjidNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.unselectedIcon,
                            contentDescription = item.title,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = item.selectedIcon,
                            contentDescription = item.title,
                        )
                    },
                    label = { Text(item.title) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun UmarMasjidNavigationRailPreview() {
    val items = TOP_LEVEL_NAV_ITEMS.values.toList()

    UmarMasjidTheme {
        UmarMasjidNavigationRail {
            items.forEachIndexed { index, item ->
                UmarMasjidNavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = item.unselectedIcon,
                            contentDescription = item.title,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = item.selectedIcon,
                            contentDescription = item.title,
                        )
                    },
                    label = { Text(item.title) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}
