package com.example.umarmasjid.feature.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.random.Random

// ─── Islamic Date Card ────────────────────────────────────────────────────────

@Composable
fun IslamicDateCard(
    islamicDate: IslamicDate,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Islamic Date",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Text(
                    text = islamicDate.hijriDate,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = islamicDate.gregorianDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        CircleShape
                    ),
            ) {
                Text(if (isDarkMode) "☀️" else "🌙", fontSize = 20.sp)
            }
        }
    }
}

// ─── Prayer Times Card ────────────────────────────────────────────────────────

@Composable
fun PrayerTimesCard(
    prayerTimes: List<PrayerTime>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Prayer Times",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                prayerTimes.forEach { PrayerTimeItem(it) }
            }
        }
    }
}

@Composable
private fun PrayerTimeItem(prayer: PrayerTime) {
    if (prayer.isNext) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = prayer.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = prayer.time,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Next",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = prayer.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = prayer.time.replace(" ", "\n"),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.5).sp
            )
        }
    }
}

// ─── Stat Card ────────────────────────────────────────────────────────────────

@Composable
fun StatCard(
    icon: String,
    label: String,
    value: String,
    subtitle: String,
    chart: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = icon,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .widthIn(min = 70.dp, max = 90.dp)
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                chart()
            }
        }
    }
}

/* ---------- Charts ---------- */

@Composable
fun LineSpark(color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .width(72.dp)
    ) {
        val points = listOf(
            Offset(0f, size.height * .7f),
            Offset(size.width * .2f, size.height * .5f),
            Offset(size.width * .4f, size.height * .6f),
            Offset(size.width * .6f, size.height * .3f),
            Offset(size.width * .8f, size.height * .4f),
            Offset(size.width, size.height * .2f)
        )

        for (i in 0 until points.lastIndex) {
            drawLine(
                color = color,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun BarMini(color: Color) {
    val heights = listOf(10, 20, 14, 28, 22)

    Row(
        modifier = Modifier.height(40.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        heights.forEach {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(it.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(color)
            )

            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
fun CircleMini(progress: Float, color: Color) {
    Box(
        modifier = Modifier.size(42.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            strokeWidth = 4.dp,
            color = color,
            trackColor = color.copy(alpha = .2f)
        )
    }
}

@Composable
fun GridMini(color: Color) {
    Column {
        repeat(3) {
            Row {
                repeat(4) {
                    val alpha = 0.3f + Random.nextFloat() * 0.7f
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(color.copy(alpha = alpha))
                    )
                    Spacer(Modifier.width(3.dp))
                }
            }
            Spacer(Modifier.height(3.dp))
        }
    }
}


@Composable
fun AttendanceTrendChart(
    points: List<AttendancePoint>,
    selectedPeriod: AttendancePeriod,
    onPeriodSelected: (AttendancePeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    // State to control the dropdown menu visibility
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Attendance Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // Dropdown anchor and menu
                Box {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable { expanded = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = selectedPeriod.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Period",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        AttendancePeriod.entries.forEach { period ->
                            DropdownMenuItem(
                                text = { Text(period.label) },
                                onClick = {
                                    onPeriodSelected(period)
                                    expanded = false
                                },
                                trailingIcon = if (period == selectedPeriod) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            AttendanceLineChart(
                points = points,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )
        }
    }
}


@Composable
fun AttendanceLineChart(
    points: List<AttendancePoint>,
    modifier: Modifier = Modifier,
) {
    if (points.isEmpty()) {
        Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No attendance data available", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    // Dynamic Y-Axis bounds calculation
    val minPercentage = points.minOfOrNull { it.percentage } ?: 0f
    val maxPercentage = points.maxOfOrNull { it.percentage } ?: 100f

    // Snap min/max to nearest 10s for clean labels, ensuring at least a 20% gap
    val yMin = (minPercentage - 10f).coerceAtLeast(0f).roundToInt() / 10 * 10
    val yMax = (maxPercentage + 10f).coerceAtMost(100f).roundToInt() / 10 * 10
    val finalYMax = if (yMax == yMin) (yMin + 20).coerceAtMost(100) else yMax

    val yLabels = listOf(yMin, yMin + (finalYMax - yMin) / 2, finalYMax)
    val yRange = (finalYMax - yMin).toFloat().coerceAtLeast(1f)

    // Animation for line drawing
    var animationTriggered by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "ChartAnimation"
    )

    LaunchedEffect(points) {
        animationTriggered = true
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    Canvas(modifier = modifier) {
        val yLabelWidth = 32.dp.toPx()
        val xAxisHeight = 24.dp.toPx()
        val chartLeft = yLabelWidth + 8.dp.toPx()
        val chartRight = size.width
        val chartTop = 16.dp.toPx()
        val chartBottom = size.height - xAxisHeight
        val chartWidth = chartRight - chartLeft
        val chartHeight = chartBottom - chartTop

        // 1. Draw Y-Axis Labels & Grid Lines
        yLabels.forEach { value ->
            val fraction = (value - yMin).toFloat() / yRange
            val y = chartBottom - fraction * chartHeight

            drawLine(
                color = outlineColor.copy(alpha = 0.3f),
                start = Offset(chartLeft, y),
                end = Offset(chartRight, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(10f, 10f)
                )
            )

            val measured = textMeasurer.measure("${value}%", labelStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    x = yLabelWidth - measured.size.width,
                    y = y - measured.size.height / 2f,
                ),
            )
        }

        // 2. Calculate coordinates
        val xStep = chartWidth / (points.size - 1).coerceAtLeast(1)
        val coords = points.mapIndexed { i, p ->
            val x = chartLeft + i * xStep
            val fraction = (p.percentage - yMin) / yRange
            // Use animation progress to grow points from the bottom
            val animatedFraction = fraction * animationProgress
            val y = chartBottom - animatedFraction.coerceIn(0f, 1f) * chartHeight
            Offset(x, y)
        }

        // 3. Draw X-Axis Labels
        points.forEachIndexed { i, p ->
            val measured = textMeasurer.measure(p.day, labelStyle)
            // Prevent text overlapping on edges
            val xOffset = when (i) {
                0 -> coords[i].x
                points.lastIndex -> coords[i].x - measured.size.width
                else -> coords[i].x - measured.size.width / 2f
            }
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(x = xOffset, y = chartBottom + 8.dp.toPx()),
            )
        }

        // 4. Draw Line & Gradient Fill
        if (coords.size > 1) {
            val linePath = Path().apply {
                moveTo(coords.first().x, coords.first().y)
                for (i in 0 until coords.size - 1) {
                    val cur = coords[i]
                    val next = coords[i + 1]
                    val cpX = cur.x + (next.x - cur.x) / 2f
                    cubicTo(cpX, cur.y, cpX, next.y, next.x, next.y)
                }
            }

            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(coords.last().x, chartBottom)
                lineTo(coords.first().x, chartBottom)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent),
                    startY = chartTop, endY = chartBottom,
                ),
            )

            drawPath(
                path = linePath,
                color = primaryColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }

        // 5. Draw Points
        coords.forEach { offset ->
            drawCircle(color = Color.White, radius = 5.dp.toPx(), center = offset)
            drawCircle(color = primaryColor, radius = 3.dp.toPx(), center = offset)
        }
    }
}
// ─── Student Entry Row ────────────────────────────────────────────────────────

@Composable
fun StudentEntryRow(
    student: StudentSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = student.initials,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = student.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (student.feeStatus == FeeStatus.PAID) "Paid" else "Pending",
            style = MaterialTheme.typography.bodySmall,
            color = if (student.feeStatus == FeeStatus.PAID) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
            modifier = Modifier
        )
    }
}

@Composable
fun PrayerTimingsLoadingCard(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
        ),
        label = "shimmerOffset",
    )
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dotAlpha",
    )
    val crescentOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "crescentFloat",
    )

    val shimmerBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.07f),
            Color.White.copy(alpha = 0.18f),
            Color.White.copy(alpha = 0.07f),
        ),
        startX = shimmerOffset * 400f,
        endX = shimmerOffset * 400f + 400f,
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.08f)
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Top row: mosque name + fetching badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "☽",
                        fontSize = 18.sp,
                        modifier = Modifier.offset(y = crescentOffset.dp),
                    )
                    Text(
                        text = "Umar Masjid",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                        .border(
                            0.5.dp,
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                            CircleShape,
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7DD3A8).copy(alpha = dotAlpha)),
                    )
                    Text(
                        text = "Fetching timings",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
            }

            // Islamic date shimmer lines
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.38f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush),
                )
            }

            // Prayer time slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(5) { i ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.06f))
                            .border(
                                0.5.dp,
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                                RoundedCornerShape(10.dp),
                            )
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(9.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(shimmerBrush),
                        )
                        Box(
                            modifier = Modifier
                                .width(44.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(shimmerBrush),
                        )
                    }
                }
            }
        }
    }
}