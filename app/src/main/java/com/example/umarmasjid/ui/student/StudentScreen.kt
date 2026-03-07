package com.example.umarmasjid.ui.student

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.ui.student.components.AddStudentBottomSheet
import com.example.umarmasjid.ui.student.components.AddStudentFAB
import com.example.umarmasjid.ui.student.components.EmptySearchState
import com.example.umarmasjid.ui.student.components.EmptyStudentsState
import com.example.umarmasjid.ui.student.components.StudentTopAppBar
import com.example.umarmasjid.ui.student.components.StudentsList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StudentScreen(
    students: List<StudentEntity>,
    isLoading: Boolean,
    popToHome: () -> Unit,
    navigateToPayment: (Int) -> Unit,
    onEvent: (StudentEvent) -> Unit,
    navigateToAttendance: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredStudents = remember(students, searchQuery) {
        if (searchQuery.isBlank()) {
            students
        } else {
            students.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.phoneNumber.contains(searchQuery)
            }
        }
    }

    BackHandler(showSearchBar) {
        showSearchBar = showSearchBar.not()
        searchQuery = ""
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            StudentTopAppBar(
                popToHome = popToHome,
                showSearchBar = showSearchBar,
                onSearchToggle = { showSearchBar = showSearchBar.not(); searchQuery = "" },
                studentCount = students.size,
                searchQuery = searchQuery,
                onSearchQueryChange = { /*onEvent(StudentEvent.SearchStudents(it))*/
                    searchQuery = it
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !showSearchBar,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                AddStudentFAB(
                    onClick = { showSheet = true },
                    studentsCount = students.size,
                    modifier = Modifier
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (filteredStudents.isEmpty() && !isLoading) {
                if (searchQuery.isNotBlank()) {
                    EmptySearchState(
                        searchQuery = searchQuery,
                        onClearSearch = { searchQuery = "" }
                    )
                } else {
                    EmptyStudentsState(
                        onAddStudent = { showSheet = true }
                    )
                }
            } else {
                StudentsList(
                    isLoading = isLoading,
                    students = filteredStudents,
                    onEdit = { student ->
                        showSheet = true
                        selectedStudent = student
                    },
                    onDelete = { student ->
                        onEvent(StudentEvent.DeleteStudent(student))
                    },
                    navigateToPayment = { studentId ->
                        navigateToPayment(studentId)
                    },
                    navigateToAttendance = { studentId ->
                        navigateToAttendance(studentId)
                    },
                    searchQuery = searchQuery
                )

            }
            if (false) {
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Loading students...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

        }

        if (showSheet) {
            AddStudentBottomSheet(
                sheetState = sheetState,
                onDismiss = {
                    showSheet = false
                    selectedStudent = null
                },
                student = selectedStudent,
                onAdd = { name, phoneNumber ->
                    onEvent(StudentEvent.AddStudent(name, phoneNumber))
                },
                onUpdate = { student, name, phoneNumber ->
                    onEvent(StudentEvent.UpdateStudent(student, name, phoneNumber))
                },
                onDelete = { student ->
                    onEvent(StudentEvent.DeleteStudent(student))
                }
            )
        }
    }
}
@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f, // big enough to move across
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    return Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.Gray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )
}
@Composable
fun ShimmerBox(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(shimmerBrush(), shape = RoundedCornerShape(8.dp))
    )
}
