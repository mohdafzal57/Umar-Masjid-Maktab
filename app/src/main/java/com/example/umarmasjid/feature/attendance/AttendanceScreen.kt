package com.example.umarmasjid.feature.attendance

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.umarmasjid.data.local_database.model.StudentWithAttendance

@Composable
fun AttendanceScreen(
    popToHome: () -> Unit,
    uiState: AttendanceState,
    onEvent: (AttendanceAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        AttendanceState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is AttendanceState.Success -> {
            val students = uiState.students
            val presentCount = students.count { it.present == true }
            val absentCount = students.count { it.present == false }
            val totalStudents = students.size

            Scaffold(
                topBar = {
                    AttendanceTopAppBar(
                        onBack = popToHome,
                        presentCount = presentCount,
                        totalCount = totalStudents
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        AttendanceSummaryCard(
                            presentCount = presentCount,
                            absentCount = absentCount,
                            totalStudents = totalStudents
                        )
                    }
                    if (students.isEmpty()) {
                        item { EmptyAttendanceState() }
                    } else {
                        item { AttendanceListHeader(totalStudents = totalStudents) }
                        items(
                            items = students,
                            key = { it.studentId }
                        ) { studentAttendance ->
                            ModernStudentAttendanceCard(
                                modifier = Modifier.animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null,
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessMediumLow,
                                        visibilityThreshold = IntOffset.VisibilityThreshold
                                    )
                                ),
                                student = studentAttendance,
                                onToggle = {
                                    onEvent(
                                        AttendanceAction.MarkAttendance(
                                            studentAttendance.studentId,
                                            !(studentAttendance.present ?: false)
                                        )
                                    )
                                }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceTopAppBar(
    onBack: () -> Unit,
    presentCount: Int,
    totalCount: Int
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Attendance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (totalCount > 0) {
                    Text(
                        text = "$presentCount/$totalCount present",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun AttendanceSummaryCard(
    presentCount: Int,
    absentCount: Int,
    totalStudents: Int
) {
    val attendanceRate = remember(presentCount, totalStudents) {
        if (totalStudents > 0) ((presentCount.toFloat() / totalStudents) * 100).toInt() else 0
    }

    val gradientColors = remember(attendanceRate) {
        when {
            attendanceRate >= 85 -> listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
            attendanceRate >= 70 -> listOf(Color(0xFF2196F3), Color(0xFF03A9F4))
            else -> listOf(Color(0xFFF44336), Color(0xFFFF9800))
        }
    }

    val animatedRate by animateIntAsState(
        targetValue = attendanceRate,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "RateTicker"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT SIDE: Text and Stats aligned in a compact column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title and Total Enrolled
                Column {
                    Text(
                        text = "Today's Attendance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Total Enrolled: $totalStudents",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Inline Present / Absent Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactStatPill(
                        count = presentCount,
                        color = Color(0xFF4CAF50),
                        icon = Icons.Rounded.CheckCircle
                    )
                    CompactStatPill(
                        count = absentCount,
                        color = Color(0xFFF44336),
                        icon = Icons.Rounded.Cancel
                    )
                }
            }

            // RIGHT SIDE: The Compact Arc Gauge
            CompactArcGauge(
                percentage = attendanceRate / 100f,
                colors = gradientColors,
                modifier = Modifier.size(72.dp),
                strokeWidth = 8.dp
            ) {
                Text(
                    text = "$animatedRate%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CompactStatPill(count: Int, color: Color, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CompactArcGauge(
    percentage: Float,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 8.dp,
    centerContent: @Composable () -> Unit
) {
    val animatedSweep by animateFloatAsState(
        targetValue = 240f * percentage,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "ArcSweep"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val sizeOffset = strokeWidthPx / 2
            val arcSize = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)

            drawArc(
                color = trackColor,
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = Offset(sizeOffset, sizeOffset),
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            if (animatedSweep > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(colors),
                    startAngle = 150f,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = Offset(sizeOffset, sizeOffset),
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }
        centerContent()
    }
}

@Composable
fun ColumnScope.BentoStatBox(
    title: String,
    count: Int,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    // Animate the numbers in the side boxes too!
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "CountTicker"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$animatedCount",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AttendanceListHeader(totalStudents: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Students",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Text(
                text = "$totalStudents students",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ModernStudentAttendanceCard(
    modifier: Modifier = Modifier,
    student: StudentWithAttendance,
    onToggle: () -> Unit
) {
    val isPresent = student.present ?: false
    val cardColor by animateColorAsState(
        targetValue = when {
            isPresent -> MaterialTheme.colorScheme.primaryContainer
            student.present == false -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        label = "card_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = isPresent,
                role = Role.Checkbox,
                onValueChange = { onToggle() }
            ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = when {
                    isPresent -> MaterialTheme.colorScheme.primary
                    student.present == false -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                },
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = student.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isPresent -> MaterialTheme.colorScheme.onPrimary
                            student.present == false -> MaterialTheme.colorScheme.onError
                            else -> MaterialTheme.colorScheme.surface
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when {
                        isPresent -> "Present"
                        student.present == false -> "Absent"
                        else -> "Not marked"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isPresent -> MaterialTheme.colorScheme.primary
                        student.present == false -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            FilledTonalButton(
                onClick = onToggle,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = when {
                        isPresent -> MaterialTheme.colorScheme.primary
                        student.present == false -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = when {
                        isPresent -> MaterialTheme.colorScheme.onPrimary
                        student.present == false -> MaterialTheme.colorScheme.onError
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = when {
                        isPresent -> Icons.Default.CheckCircle
                        student.present == false -> Icons.Default.Cancel
                        else -> Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyAttendanceState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No students enrolled",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add students first to start taking attendance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
