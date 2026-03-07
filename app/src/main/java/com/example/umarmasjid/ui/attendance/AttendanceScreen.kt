package com.example.umarmasjid.ui.attendance


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.umarmasjid.data.local_database.model.StudentWithAttendance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun AttendanceScreen(
    popToHome: () -> Unit,
    uiState: AttendanceState,
    onEvent: (AttendanceEvent) -> Unit,
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

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    AttendanceTopAppBar(
                        onBack = popToHome,
                        presentCount = presentCount,
                        totalCount = totalStudents
                    )
                }

                item {
                    AttendanceDateCard()
                }

                item {
                    AttendanceSummaryCard(
                        presentCount = presentCount,
                        absentCount = absentCount,
                        totalStudents = totalStudents
                    )
                }

                if (students.isEmpty()) {

                    item {
                        EmptyAttendanceState()
                    }

                } else {

                    item {
                        AttendanceListHeader(totalStudents = totalStudents)
                    }

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
                                    AttendanceEvent.MarkAttendance(
                                        studentAttendance.studentId,
                                        !(studentAttendance.present ?: false)
                                    )
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
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
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun AttendanceDateCard() {
    val currentDate = remember { System.currentTimeMillis() }
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    val islamicDate = "15 Safar 1446 AH" // You can integrate Islamic calendar

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
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
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = "Today",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateFormat.format(Date(currentDate)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = islamicDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun AttendanceSummaryCard(
    presentCount: Int,
    absentCount: Int,
    totalStudents: Int
) {
    val attendanceRate = if (totalStudents > 0) {
        (presentCount.toFloat() / totalStudents * 100).toInt()
    } else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "Analytics",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Today's Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when {
                        attendanceRate >= 80 -> MaterialTheme.colorScheme.primaryContainer
                        attendanceRate >= 60 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.errorContainer
                    }
                ) {
                    Text(
                        text = "$attendanceRate%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = when {
                            attendanceRate >= 80 -> MaterialTheme.colorScheme.onPrimaryContainer
                            attendanceRate >= 60 -> MaterialTheme.colorScheme.onTertiaryContainer
                            else -> MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttendanceStatItem(
                    icon = Icons.Default.CheckCircle,
                    value = "$presentCount",
                    label = "Present",
                    color = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                )

                AttendanceStatItem(
                    icon = Icons.Default.Cancel,
                    value = "$absentCount",
                    label = "Absent",
                    color = MaterialTheme.colorScheme.error,
                    backgroundColor = MaterialTheme.colorScheme.errorContainer
                )

                AttendanceStatItem(
                    icon = Icons.Default.Group,
                    value = "$totalStudents",
                    label = "Total",
                    color = MaterialTheme.colorScheme.secondary,
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}

@Composable
fun AttendanceStatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    backgroundColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = backgroundColor,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = color
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
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
    val isMarked = student.present != null

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
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simple avatar with initials
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

            // Student name
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

            // Simple toggle button
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
fun StudentAvatar(
    name: String,
    isPresent: Boolean,
    isMarked: Boolean
) {
    val avatarColor by animateColorAsState(
        targetValue = when {
            isPresent -> MaterialTheme.colorScheme.primary
            isMarked -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(300),
        label = "avatar_color"
    )

    Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = avatarColor.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = avatarColor
                )
            }
        }

        // Status indicator
        if (isMarked) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPresent) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = if (isPresent) "Present" else "Absent",
                    modifier = Modifier.size(14.dp),
                    tint = if (isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AttendanceToggleButton(
    isPresent: Boolean,
    isMarked: Boolean,
    onToggle: () -> Unit
) {
    val buttonColor by animateColorAsState(
        targetValue = when {
            isPresent -> MaterialTheme.colorScheme.primary
            isMarked -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(300),
        label = "button_color"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isPresent -> MaterialTheme.colorScheme.onPrimary
            isMarked -> MaterialTheme.colorScheme.onError
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(300),
        label = "content_color"
    )

    FilledTonalButton(
        onClick = onToggle,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = buttonColor.copy(alpha = if (isMarked) 1f else 0.12f),
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.animateContentSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        )
    ) {
        Icon(
            imageVector = when {
                isPresent -> Icons.Default.CheckCircle
                isMarked -> Icons.Default.Cancel
                else -> Icons.Default.RadioButtonUnchecked
            },
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
                isPresent -> "Present"
                isMarked -> "Absent"
                else -> "Mark"
            },
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AttendanceActionMenu(
    onEvent: (AttendanceEvent) -> Unit,
    hasStudents: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    if (!hasStudents) return

    Column(
        horizontalAlignment = Alignment.End
    ) {
        // Action buttons that appear when expanded
        if (expanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        onEvent(AttendanceEvent.MarkAllPresent)
                        expanded = false
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Mark All Present"
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        onEvent(AttendanceEvent.MarkAllAbsent)
                        expanded = false
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Mark All Absent"
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        onEvent(AttendanceEvent.ResetAttendance)
                        expanded = false
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Attendance"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Main FAB
        ExtendedFloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.MoreVert,
                contentDescription = if (expanded) "Close Menu" else "Open Menu"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (expanded) "Close" else "Actions",
                style = MaterialTheme.typography.labelLarge
            )
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

// Utility function for date formatting
private fun getCurrentFormattedDate(): String {
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}

// Alternative compact attendance row for dense layouts
@Composable
fun CompactStudentAttendanceRow(
    student: StudentWithAttendance,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPresent = student.present ?: false

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isPresent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                student.present == false -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = isPresent,
                    role = Role.Switch,
                    onValueChange = { onToggle() }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = student.name.take(2).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = when {
                    isPresent -> MaterialTheme.colorScheme.primary
                    student.present == false -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isPresent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            student.present == false -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        }
                    )
                    .wrapContentSize()
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = student.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isPresent,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}