package com.example.umarmasjid.feature.payments.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.data.local_database.model.StudentWithPayment
import com.example.umarmasjid.feature.payments.PaymentState
import com.example.umarmasjid.feature.payments.PaymentStatus
import com.example.umarmasjid.feature.student_payment.AddPaymentBottomSheet
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BasicPaymentScreen(
    popToHome: () -> Unit,
    uiState: PaymentState,
    onPaymentDone: (Int, Double, String, String?) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { PaymentStatus.entries.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { popToHome() }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        "Payment Management",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tabs
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    PaymentStatus.entries.forEachIndexed { index, destination ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(destination.label) }
                        )
                    }
                }

                // Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        // PAID tab
                        0 -> {
                            if (uiState.paidStudents.isEmpty() && !uiState.isLoading) {
                                EmptyPaidState()
                            } else {
                                LazyColumn {
                                    items(uiState.paidStudents) { student ->
                                        PaidStudentCard(student = student)
                                    }
                                }
                            }
                        }

                        // UNPAID tab
                        1 -> {
                            if (uiState.unpaidStudents.isEmpty() && !uiState.isLoading) {
                                EmptyUnpaidState(totalStudents = uiState.paidStudents.size + uiState.unpaidStudents.size)
                            } else {
                                LazyColumn {
                                    items(uiState.unpaidStudents) { student ->
                                        UnpaidStudentCard(
                                            student = student,
                                            onPaymentDone = { id, amount, date, receiptNumber ->
                                                onPaymentDone(id, amount, date, receiptNumber)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Loading overlay
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun PaidStudentCard(
    student: StudentWithPayment
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = student.studentName, style = MaterialTheme.typography.titleMedium)
            Text(text = "Amount: ₹${student.amount}")
            Text(text = "Date: ${dateFormat.format(Date(student.paymentDate))}") // format date as needed
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnpaidStudentCard(
    student: StudentEntity,
    onPaymentDone: (Int, Double, String, String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddPaymentSheet by remember { mutableStateOf(false) }


    // Main Card UI
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = student.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Unpaid", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { showAddPaymentSheet = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Pay Now")
            }
        }
    }
    if (showAddPaymentSheet) {
        AddPaymentBottomSheet(
            sheetState = sheetState,
            studentName = student.name,
            onDismiss = { showAddPaymentSheet = false },
            onAddPayment = { amount, method, receiptNumber ->
                onPaymentDone(student.id, amount, method, receiptNumber)
            }
        )
    }
}
