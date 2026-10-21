package com.example.umarmasjid.feature.attendance_bar_chart

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.umarmasjid.data.local_database.model.AttendanceStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceBarChartScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendanceBarChartViewModel = hiltViewModel(),
) {
    // 1. Lifecycle-aware state collection
    val attendanceList by viewModel.attendanceState.collectAsStateWithLifecycle()

    // 2. Memoize date formatting & list mapping so they don't re-run on every recomposition
    val currentMonthName = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    }
    val statusList = remember(attendanceList) {
        attendanceList.map { it.attendanceStatus }
    }

    // 3. Surface provides the automatic Dark/Light background & content color
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = statusList.isEmpty(),
                label = "AttendanceStateCrossfade"
            ) { isEmpty ->
                if (isEmpty) {
                    EmptyAttendanceState(monthName = currentMonthName)
                } else {
                    AttendanceBarChart(
                        attendanceData = statusList,
                        monthName = currentMonthName
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyAttendanceState(
    monthName: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Attendance Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "There are no attendance records logged for $monthName yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AttendanceBarChart(
    attendanceData: List<AttendanceStatus>,
    monthName: String,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // 1. Adaptive Dark / Light Mode Palette
    val presentColor = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
    val lateColor = if (isDark) Color(0xFFFFB74D) else Color(0xFFEF6C00)
    val absentColor = if (isDark) Color(0xFFE57373) else Color(0xFFC62828)
    val gridLineColor = MaterialTheme.colorScheme.outlineVariant
    val labelTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    // 2. Memoized Summary Calculations
    val (presentDays, lateDays, absentDays) = remember(attendanceData) {
        Triple(
            attendanceData.count { it == AttendanceStatus.PRESENT },
            attendanceData.count { it == AttendanceStatus.LATE },
            attendanceData.count { it == AttendanceStatus.ABSENT }
        )
    }
    val totalDays = attendanceData.size

    // 3. Interactive & Animation State
    var selectedDayIndex by remember { mutableStateOf<Int?>(null) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(attendanceData) {
        selectedDayIndex = null
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
    }

    val textMeasurer = rememberTextMeasurer()

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Attendance Overview",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Attendance Percentage Badge
                val attendanceRate = if (totalDays > 0) {
                    ((presentDays + (lateDays * 0.5f)) / totalDays * 100).toInt()
                } else 0

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$attendanceRate% Rate",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Selection Banner (Fixed height prevents layout jump)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedDayIndex != null && selectedDayIndex in attendanceData.indices) {
                    val day = selectedDayIndex!! + 1
                    val status = attendanceData[selectedDayIndex!!]
                    val statusColor = when (status) {
                        AttendanceStatus.PRESENT -> presentColor
                        AttendanceStatus.LATE -> lateColor
                        AttendanceStatus.ABSENT -> absentColor
                    }

                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(statusColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Day $day: ${status.name}",
                                style = MaterialTheme.typography.labelLarge,
                                color = statusColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Tap any bar to inspect day details",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Pure Canvas Chart (Bars, Gridlines, and X-Axis Labels)
            if (attendanceData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No attendance records for $monthName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(attendanceData) {
                            detectTapGestures { tapOffset ->
                                val slotWidth = size.width / attendanceData.size.toFloat()
                                val tappedIndex = (tapOffset.x / slotWidth)
                                    .toInt()
                                    .coerceIn(0, attendanceData.lastIndex)
                                selectedDayIndex = if (selectedDayIndex == tappedIndex) {
                                    null
                                } else {
                                    tappedIndex
                                }
                            }
                        }
                ) {
                    val bottomLabelAreaHeight = 28.dp.toPx()
                    val chartAreaHeight = size.height - bottomLabelAreaHeight
                    val slotWidth = size.width / totalDays

                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                    // Horizontal reference lines (Present = 90%, Late = 55% height)
                    val guideLevels = listOf(0.1f, 0.45f)
                    guideLevels.forEach { fraction ->
                        val y = chartAreaHeight * fraction
                        drawLine(
                            color = gridLineColor.copy(alpha = 0.6f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = dashEffect
                        )
                    }

                    // Draw each day's bar and label
                    attendanceData.forEachIndexed { index, status ->
                        val isSelected = index == selectedDayIndex

                        // Highlight column background if selected
                        if (isSelected) {
                            drawRoundRect(
                                color = highlightColor,
                                topLeft = Offset(index * slotWidth, 0f),
                                size = Size(slotWidth, chartAreaHeight),
                                cornerRadius = CornerRadius(6.dp.toPx())
                            )
                        }

                        val baseColor = when (status) {
                            AttendanceStatus.PRESENT -> presentColor
                            AttendanceStatus.LATE -> lateColor
                            AttendanceStatus.ABSENT -> absentColor
                        }

                        val targetHeightFactor = when (status) {
                            AttendanceStatus.PRESENT -> 0.90f
                            AttendanceStatus.LATE -> 0.55f
                            AttendanceStatus.ABSENT -> 0.15f
                        }

                        val barHeight =
                            chartAreaHeight * targetHeightFactor * animationProgress.value
                        val barWidth = (slotWidth * 0.65f).coerceAtLeast(3.dp.toPx())
                        val barX = index * slotWidth + (slotWidth - barWidth) / 2f
                        val barTopY = chartAreaHeight - barHeight

                        // Draw Bar
                        drawRoundRect(
                            color = if (selectedDayIndex == null || isSelected) {
                                baseColor
                            } else {
                                baseColor.copy(alpha = 0.4f) // Dim unselected bars when one is active
                            },
                            topLeft = Offset(barX, barTopY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4.dp.toPx())
                        )

                        // Smart X-Axis Labeling: Avoid overlapping on 30-day months
                        val shouldShowLabel = totalDays <= 15 ||
                                index == 0 ||
                                (index + 1) % 5 == 0 ||
                                index == totalDays - 1 ||
                                isSelected

                        if (shouldShowLabel) {
                            val labelText = "${index + 1}"
                            val textLayoutResult = textMeasurer.measure(
                                text = labelText,
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) {
                                        baseColor
                                    } else {
                                        labelTextColor
                                    }
                                )
                            )

                            val textX =
                                index * slotWidth + (slotWidth - textLayoutResult.size.width) / 2f
                            val textY = chartAreaHeight + 8.dp.toPx()

                            drawText(
                                textLayoutResult = textLayoutResult,
                                topLeft = Offset(textX, textY)
                            )
                        }
                    }

                    // Solid Baseline
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, chartAreaHeight),
                        end = Offset(size.width, chartAreaHeight),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // 5. Combined Summary & Legend Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryLegendChip(
                    color = presentColor,
                    label = "Present",
                    count = presentDays
                )
                SummaryLegendChip(
                    color = lateColor,
                    label = "Late",
                    count = lateDays
                )
                SummaryLegendChip(
                    color = absentColor,
                    label = "Absent",
                    count = absentDays
                )
            }
        }
    }
}

@Composable
private fun SummaryLegendChip(
    color: Color,
    label: String,
    count: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$count days",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}