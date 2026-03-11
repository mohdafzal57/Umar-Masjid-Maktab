package com.example.umarmasjid.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    navigateToAttendance: () -> Unit,
    navigateToStudentManagement: () -> Unit,
    navigateToFeeManagement: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreenContent(
        state = state,
        onAction = { action ->
            when (action) {
                is HomeAction.NavigateToAttendance -> navigateToAttendance()
                is HomeAction.NavigateToStudents -> navigateToStudentManagement()
                is HomeAction.NavigateToFees -> navigateToFeeManagement()
                is HomeAction.NavigateToStudent -> navigateToStudentManagement()
                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
internal fun HomeScreenContent(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    val lazyState = rememberLazyListState()
    val showHeader by remember {
        derivedStateOf {
            lazyState.firstVisibleItemIndex == 0 &&
                    lazyState.firstVisibleItemScrollOffset < 120
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                ) {
                    HomeHeader(state = state, onAction = onAction)
                    StatsGrid(
                        stats = state.stats,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp, 16.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
            item {
                AttendanceTrendChart(
                    points = state.attendanceTrend,
                    selectedPeriod = state.attendancePeriod,
                    onPeriodSelected = { onAction(HomeAction.SelectAttendancePeriod(it)) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                )
            }
            item {
                RecentStudentsSection(
                    students = state.recentStudents,
                    onViewAll = { onAction(HomeAction.NavigateToStudents) },
                    onStudentClick = { id -> onAction(HomeAction.NavigateToStudent(id)) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                )
            }
            item {
                HomeActionButtons(
                    onAddStudent = { onAction(HomeAction.NavigateToStudents) },
                    onViewReports = { onAction(HomeAction.NavigateToAttendance) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
            )
        }
        if (!showHeader) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun HomeHeader(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp, bottom = 24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = state.greeting,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Admin Dashboard",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                    )
                }
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)),
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            IslamicDateCard(
                islamicDate = state.islamicDate,
                onToggleDarkMode = { onAction(HomeAction.ToggleDarkMode) },
            )
            PrayerTimesCard(prayerTimes = state.prayerTimes)
        }
    }
}

@Composable
fun StatsGrid(
    stats: DashboardStats,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 2
    ) {
        StatCard(
            icon = "👥",
            label = "Total Students",
            value = stats.totalStudents.toString(),
            subtitle = "↑ ${stats.studentDelta} this month",
            chart = { LineSpark(MaterialTheme.colorScheme.primary) },
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = "🕐",
            label = "Present Today",
            value = stats.presentToday.toString(),
            subtitle = stats.attendanceRate,
            chart = { BarMini(MaterialTheme.colorScheme.secondary) },
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = "💰",
            label = "Fees Collected",
            value = stats.feesCollected,
            subtitle = stats.feesCollectedPeriod,
            chart = { CircleMini(.72f, MaterialTheme.colorScheme.tertiary) },
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = "⏰",
            label = "Pending Fees",
            value = stats.pendingFees,
            subtitle = "${stats.pendingStudentsCount} students",
            chart = { GridMini(MaterialTheme.colorScheme.error) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RecentStudentsSection(
    students: List<StudentSummary>,
    onViewAll: () -> Unit,
    onStudentClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Recent Students",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onViewAll) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            students.forEach { student ->
                StudentEntryRow(student = student, onClick = { onStudentClick(student.id) })
                if (student != students.last()) HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun HomeActionButtons(
    onAddStudent: () -> Unit,
    onViewReports: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onAddStudent,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text(
                text = "👤  Add Student",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        Button(
            onClick = onViewReports,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        ) {
            Text(
                text = "📊  View Reports",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
