package com.example.umarmasjid.ui.payment.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.umarmasjid.ui.payment.PaymentStatus
import kotlinx.coroutines.CoroutineScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedPrimaryTabRow(
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    tabs: List<TabItem>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.padding(4.dp),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(selectedTabIndex)
                        .padding(horizontal = 8.dp),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                EnhancedTab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabClick(index) },
                    tab = tab
                )
            }
        }
    }
}

@Composable
private fun EnhancedTab(
    selected: Boolean,
    onClick: () -> Unit,
    tab: TabItem
) {
    val animatedTextColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250, easing = EaseInOut)
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
    )

    Tab(
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .scale(animatedScale)
            .padding(vertical = 8.dp),
        interactionSource = remember { MutableInteractionSource() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            if (tab.icon != null) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = null,
                    tint = animatedTextColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = animatedTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Subtle badge for notifications or counts
            if (tab.badge != null && tab.badge > 0) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(6.dp)
                ) {}
            }
        }
    }
}

data class TabItem(
    val label: String,
    val icon: ImageVector? = null,
    val badge: Int? = null
)

// Usage example with your PaymentStatus
@Composable
fun PaymentTabRow(
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
//    val paymentTabs = remember {
//        PaymentStatus.entries.map { status ->
//            TabItem(
//                label = status.label,
//                icon = when (status) {
//                    PaymentStatus.PENDING -> Icons.Default.Schedule
//                    PaymentStatus.COMPLETED -> Icons.Default.CheckCircle
//                    PaymentStatus.FAILED -> Icons.Default.Error
//                    // Add more cases as needed
//                },
//                badge = getNotificationCount(status) // Your logic here
//            )
//        }
//    }

//    EnhancedPrimaryTabRow(
//        selectedTabIndex = pagerState.currentPage,
//        onTabClick = { index ->
//            coroutineScope.launch {
//                pagerState.animateScrollToPage(index)
//            }
//        },
//        tabs = paymentTabs
//    )
}

// Helper function - implement based on your needs
private fun getNotificationCount(status: PaymentStatus): Int? {
    // Return notification count or null if no notifications
    return null
}