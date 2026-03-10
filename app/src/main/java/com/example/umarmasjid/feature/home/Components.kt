package com.example.umarmasjid.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// ─── Islamic Date Card ────────────────────────────────────────────────────────

@Composable
fun IslamicDateCard(
    islamicDate: IslamicDate,
    onToggleDarkMode: () -> Unit,
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
                Text(if (islamicDate.isDarkMode) "☀️" else "🌙", fontSize = 20.sp)
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
                horizontalArrangement = Arrangement.SpaceEvenly
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
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = prayer.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = prayer.time,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Next",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = prayer.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = prayer.time,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Text(
                        text = selectedPeriod.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            AttendanceLineChart(
                points = points,
                yMin = 90,
                yMax = 100,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )
        }
    }
}

@Composable
private fun AttendanceLineChart(
    points: List<AttendancePoint>,
    yMin: Int,
    yMax: Int,
    modifier: Modifier = Modifier,
) {
    if (points.isEmpty()) return

    val yLabels = (yMin..yMax).toList()
    val yRange = (yMax - yMin).toFloat()

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    Canvas(modifier = modifier) {
        val yLabelWidth = 28.dp.toPx()
        val xAxisHeight = 20.dp.toPx()
        val chartLeft = yLabelWidth + 4.dp.toPx()
        val chartRight = size.width
        val chartTop = 0f
        val chartBottom = size.height - xAxisHeight
        val chartWidth = chartRight - chartLeft
        val chartHeight = chartBottom - chartTop

        yLabels.forEach { value ->
            val fraction = (value - yMin).toFloat() / yRange
            val y = chartBottom - fraction * chartHeight

            drawLine(
                color = outlineColor.copy(alpha = 0.5f),
                start = Offset(chartLeft, y),
                end = Offset(chartRight - 4.dp.toPx(), y),
                strokeWidth = 0.8.dp.toPx(),
            )

            val measured = textMeasurer.measure(value.toString(), labelStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    x = yLabelWidth - measured.size.width - 4.dp.toPx(),
                    y = y - measured.size.height / 2f,
                ),
            )
        }

        val xStep = chartWidth / (points.size - 1).coerceAtLeast(1)
        val coords = points.mapIndexed { i, p ->
            val x = chartLeft + i * xStep
            val fraction = (p.percentage - yMin) / yRange
            val y = chartBottom - fraction.coerceIn(0f, 1f) * chartHeight
            Offset(x, y)
        }

        points.forEachIndexed { i, p ->
            val measured = textMeasurer.measure(p.day, labelStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    x = coords[i].x - measured.size.width / 2f,
                    y = chartBottom + 6.dp.toPx(),
                ),
            )
        }

        fun smoothPath(pts: List<Offset>): Path = Path().apply {
            if (pts.size < 2) return@apply
            moveTo(pts.first().x, pts.first().y)
            for (i in 0 until pts.size - 1) {
                val cur = pts[i]
                val next = pts[i + 1]
                val cpX1 = cur.x + (next.x - cur.x) / 2f
                val cpX2 = next.x - (next.x - cur.x) / 2f
                cubicTo(cpX1, cur.y, cpX2, next.y, next.x, next.y)
            }
        }

        val linePath = smoothPath(coords)

        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(coords.last().x, chartBottom)
            lineTo(coords.first().x, chartBottom)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent),
                startY = chartTop, endY = chartBottom,
            ),
        )

        drawPath(
            path = linePath,
            color = primaryColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round,
            ),
        )

        val dashOn = 4.dp.toPx()
        val dashOff = 3.dp.toPx()
        coords.forEach { offset ->
            drawLine(
                color = primaryColor.copy(alpha = 0.3f),
                start = Offset(offset.x, offset.y + 4.dp.toPx()),
                end = Offset(offset.x, chartBottom),
                strokeWidth = 1.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    intervals = floatArrayOf(dashOn, dashOff),
                    phase = 0f,
                ),
            )
        }

        coords.forEach { offset ->
            drawCircle(color = primaryColor, radius = 4.5.dp.toPx(), center = offset)
            drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = offset)
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = student.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${student.grade} • ${student.studentCode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${student.attendancePercent}%",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (student.attendancePercent >= 90) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
            Text(
                text = if (student.feeStatus == FeeStatus.PAID) "Paid" else "Pending",
                style = MaterialTheme.typography.bodySmall,
                color = if (student.feeStatus == FeeStatus.PAID) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
            )
        }
    }
}
