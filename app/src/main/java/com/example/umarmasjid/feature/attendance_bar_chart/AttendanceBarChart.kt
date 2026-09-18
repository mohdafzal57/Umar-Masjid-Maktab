package com.example.umarmasjid.feature.attendance_bar_chart

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.umarmasjid.data.local_database.model.AttendanceStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceBarCharScreen(
    viewModel: AttendanceBarChartViewModel = hiltViewModel()
) {
    val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    val attendanceList by viewModel.attendanceState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (attendanceList.isEmpty()) {
            Text("No attendance data available")
        }
        else {
            AttendanceBarChart(
                attendanceData = attendanceList.map { it.attendanceStatus },
                monthName = dateFormat.format(Date())
            )

        }
    }
}
data class AttendanceChartData(
    val dayLabel: String,
    val date: Long,
    val isPresent: Boolean,
    val dayOfWeek: String
)

@Composable
fun AttendanceChart(
    attendanceData: List<AttendanceChartData>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (attendanceData.isEmpty()) return@Canvas

        val barWidth = (canvasWidth - 32.dp.toPx()) / attendanceData.size
        val maxBarHeight = canvasHeight - 80.dp.toPx()
        val barHeight = maxBarHeight * 0.8f

        attendanceData.forEachIndexed { index, data ->
            val startX = 16.dp.toPx() + index * barWidth + barWidth * 0.1f
            val barWidthActual = barWidth * 0.8f

            // Draw bar
            val barColor = if (data.isPresent) Color(0xFF4CAF50) else Color(0xFFFF5722)
            val currentBarHeight = if (data.isPresent) barHeight else barHeight * 0.3f

            drawRoundRect(
                color = barColor,
                topLeft = Offset(startX, maxBarHeight - currentBarHeight + 20.dp.toPx()),
                size = Size(barWidthActual, currentBarHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            // Draw connecting line from bar to label
            val lineStartX = startX + barWidthActual / 2
            val lineStartY = maxBarHeight + 20.dp.toPx()
            val lineEndY = lineStartY + 10.dp.toPx()

            drawLine(
                color = Color.Gray,
                start = Offset(lineStartX, lineStartY),
                end = Offset(lineStartX, lineEndY),
                strokeWidth = 1.dp.toPx()
            )

            // Draw day label
            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = Paint.Align.CENTER
                    textSize = 12.sp.toPx()
                }
                drawText(
                    data.dayLabel,
                    lineStartX,
                    lineEndY + 20.dp.toPx(),
                    paint
                )
            }
        }

        // Draw legend
        val legendY = canvasHeight - 10.dp.toPx()
        val legendItemWidth = 80.dp.toPx()

        // Present legend
        drawRoundRect(
            color = Color(0xFF4CAF50),
            topLeft = Offset(16.dp.toPx(), legendY - 12.dp.toPx()),
            size = Size(12.dp.toPx(), 12.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx())
        )

        // Absent legend
        drawRoundRect(
            color = Color(0xFFFF5722),
            topLeft = Offset(16.dp.toPx() + legendItemWidth, legendY - 12.dp.toPx()),
            size = Size(12.dp.toPx(), 12.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx())
        )

        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 12.sp.toPx()
            }
            drawText("Present", 34.dp.toPx(), legendY, paint)
            drawText("Absent", 16.dp.toPx() + legendItemWidth + 18.dp.toPx(), legendY, paint)
        }
    }
}

@Composable
fun AttendanceBarChart(
    attendanceData: List<AttendanceStatus>,
    monthName: String
) {
    val presentDays = attendanceData.count { it == AttendanceStatus.PRESENT }
    val absentDays = attendanceData.count { it == AttendanceStatus.ABSENT }
    val lateDays = attendanceData.count { it == AttendanceStatus.LATE }

    val totalDays = attendanceData.size
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Attendance for $monthName",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 8.dp)
        ) {
            var chartWidth by remember { mutableStateOf(0f) }
            var chartHeight by remember { mutableStateOf(0f) }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        chartWidth = it.size.width.toFloat()
                        chartHeight = it.size.height.toFloat()
                    }
            ) {
                val barWidth = size.width / attendanceData.size

                attendanceData.forEachIndexed { index, status ->

                    val color = when (status) {
                        AttendanceStatus.PRESENT -> Color(0xFF4CAF50)
                        AttendanceStatus.ABSENT -> Color(0xFFF44336)
                        AttendanceStatus.LATE -> Color(0xFFFF9800)
                    }

                    val barHeight = when (status) {
                        AttendanceStatus.PRESENT -> size.height * 0.4f
                        AttendanceStatus.LATE -> size.height * 0.25f
                        AttendanceStatus.ABSENT -> size.height * 0.05f
                    }

                    val topY = size.height * 0.5f - barHeight

                    // Bar
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(
                            index * barWidth,
                            topY
                        ),
                        size = Size(
                            barWidth * 0.8f,
                            barHeight
                        ),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )

                    // Vertical line
                    val centerX = index * barWidth + (barWidth * 0.4f)

                    drawLine(
                        color = Color.Gray,
                        start = Offset(
                            centerX,
                            size.height * 0.5f
                        ),
                        end = Offset(
                            centerX,
                            size.height * 0.55f
                        ),
                        strokeWidth = 2f
                    )
                }
            }

            // Day labels
            if (chartWidth > 0f && attendanceData.isNotEmpty()) {
                val barWidth = chartWidth / attendanceData.size

                attendanceData.forEachIndexed { index, _ ->
                    val centerX = index * barWidth + (barWidth * 0.4f)

                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(density) {
                                    centerX.toDp()
                                },
                                y = with(density) {
                                    (chartHeight * 0.55f).toDp()
                                }
                            )
                            .wrapContentSize(Alignment.TopCenter)
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.graphicsLayer {
                                rotationZ = 15f
                                transformOrigin = TransformOrigin(0.5f, 0f)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary
        Text(
            text = "Present: $presentDays / $totalDays days",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Absent: $absentDays  •  Late: $lateDays",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Legend
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendItem(
                color = Color(0xFF4CAF50),
                label = "Present"
            )

            LegendItem(
                color = Color(0xFFF44336),
                label = "Absent"
            )

            LegendItem(
                color = Color(0xFFFF9800),
                label = "Late"
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

