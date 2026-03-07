package com.example.umarmasjid.ui.payment


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.data.local_database.model.StudentWithPayment
import com.example.umarmasjid.ui.payment.components.FuturisticPaymentBottomSheet
import com.example.umarmasjid.ui.payment.components.PaidStudentsPage
import com.example.umarmasjid.ui.payment.components.UnpaidStudentsPage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PaymentScreen(
    popToHome: () -> Unit,
    uiState: PaymentState,
    onPaymentDone: (Int, Double, String, String?) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { PaymentStatus.entries.size })
    val coroutineScope = rememberCoroutineScope()
    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition()
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF141D36),
                        Color(0xFF0F3460)
                    ),
                    startY = gradientOffset * 300f,
                    endY = gradientOffset * 800f + 500f
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Glassmorphic Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp) // This is effectively status bar spacing
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp
                    ), // Overall padding for the Surface card
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.1f),
                // Consider adding elevation or a border to the Surface itself if needed
                // shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 20.dp
                        ) // Consistent padding within the glassmorphic box
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button - ensure it has a defined size for SpaceBetween to work predictably
                        IconButton(
                            onClick = popToHome,
                            modifier = Modifier
                                .size(48.dp) // Consistent size for icon buttons
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        // Central Content
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between title and stats
                        ) {
                            Text(
                                "Payment Hub",
                                style = MaterialTheme.typography.headlineSmall.copy( // Consider M3 styles
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )

                            // Animated statistics
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                StatCard( // Assuming StatCard is defined
                                    title = "Paid",
                                    count = uiState.paidStudents.size,
                                    color = Color(0xFF4CAF50)
                                )
                                StatCard( // Assuming StatCard is defined
                                    title = "Pending",
                                    count = uiState.unpaidStudents.size,
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }

                        // Notification Bell - also ensure it has a similar defined size area for balance
                        // The Box for the badge will be aligned relative to this outer Box
                        Box(
                            modifier = Modifier.size(48.dp), // Match IconButton size for better balance
                            contentAlignment = Alignment.Center // Center the IconButton within this Box
                        ) {
                            // val pulseScale by infiniteTransition.animateFloat( ... ) // Define your animation value

                            IconButton(
                                onClick = { /* Handle notification click */ },
                                modifier = Modifier
                                    // .scale(pulseScale) // Apply scale if needed
                                    .size(40.dp) // The icon button itself can be slightly smaller if the outer Box is 48dp
                                    .background(
                                        color = Color(0xFFFF6B6B).copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White
                                )
                            }

                            if (uiState.unpaidStudents.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd) // Align badge to the top-end of the 48.dp Box
                                        .padding(
                                            top = 4.dp,
                                            end = 4.dp
                                        ) // Adjust padding for badge position
                                        .size(20.dp)
                                        .background(
                                            color = Color(0xFFFF4444),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${uiState.unpaidStudents.size}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall // Consistent typography
                                    )
                                }
                            }
                        }
                    }
                }
            }
            // Floating Tab System
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp)
                    ) {
                        PaymentStatus.entries.forEachIndexed { index, status ->
                            val isSelected = pagerState.currentPage == index
                            val animatedScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.1f else 1f,
                                animationSpec = tween(200)
                            )

                            Surface(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                shape = RoundedCornerShape(40.dp),
                                color = if (isSelected)
                                    Color.White.copy(alpha = 0.3f)
                                else
                                    Color.Transparent,
                                modifier = Modifier
                                    .scale(animatedScale)
                                    .weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (index == 0) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color.White.copy(
                                            alpha = 0.7f
                                        ),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = status.label,
                                        color = if (isSelected) Color.White else Color.White.copy(
                                            alpha = 0.7f
                                        ),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Content Area with Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> PaidStudentsPage(uiState.paidStudents, uiState.isLoading)
                    1 -> UnpaidStudentsPage(
                        students = uiState.unpaidStudents,
                        isLoading = uiState.isLoading,
                        onPaymentDone = onPaymentDone,
                        totalStudents = uiState.paidStudents.size + uiState.unpaidStudents.size
                    )
                }
            }
        }

        // Floating loading indicator
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF6366F1),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Loading payments...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, count: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun PaidStudentCard(student: StudentWithPayment) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4CAF50).copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Success animation
                val infiniteTransition = rememberInfiniteTransition()
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = 0.3f),
                                    Color(0xFF4CAF50).copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .scale(scale),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Paid",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.studentName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "₹${student.amount}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF4CAF50)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = dateFormat.format(Date(student.paymentDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Floating status indicator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF4CAF50)
                ) {
                    Text(
                        text = "PAID",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }
            }
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp), // Outer padding for the card
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.12f),
        // shadowElevation = 2.dp // Optional: add a subtle shadow to lift the card
    ) {
        Box(
            // This Box provides the horizontal gradient background that fades out
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF9800).copy(alpha = 0.1f), // Orange tint on the left
                            Color.Transparent // Fades to transparent on the right
                        )
                    )
                )
        ) {
            // Main content Row: Student Info on left, Pay Button on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp), // Padding inside the card, around all content
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Part: Student Icon and Details
                Row(
                    modifier = Modifier
                        .weight(1f) // Allows this Row to take available space, pushing Pay button to the end
                        .padding(end = 8.dp), // Add some padding before the Pay button
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between icon and text column
                ) {
                    // Pulsing warning indicator Box
                    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
                    val pulse by infiniteTransition.animateFloat(
                        initialValue = 0.8f, // Start slightly smaller
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseScale"
                    )

                    Box(
                        modifier = Modifier
                            .size(56.dp) // Slightly smaller for a tighter look if preferred
                            .scale(pulse)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF9800).copy(alpha = 0.25f),
                                        Color(0xFFFF9800).copy(alpha = 0.05f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Payment Pending", // More descriptive
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(28.dp) // Adjusted icon size
                        )
                    }

                    //Spacer(modifier = Modifier.width(16.dp)) // Replaced by Arrangement.spacedBy

                    // Student Name and Status Column
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp) // Space between name and status text
                    ) {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White,
                            maxLines = 1, // Ensure name doesn't wrap excessively
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )

                        //Spacer(modifier = Modifier.height(4.dp)) // Replaced by Arrangement.spacedBy

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFF9800).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Payment Pending",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium, // fontWeight often part of the style
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                }

                // Right Part: Morphing Pay Button
                Surface(
                    onClick = { showAddPaymentSheet = true },
                    shape = RoundedCornerShape(16.dp), // Pill shape for button
                    color = Color(0xFF6366F1), // Base color for the button
                    modifier = Modifier.animateContentSize(), // Animates size changes
                    tonalElevation = 2.dp // Add a slight lift to the button
                ) {
                    Box(
                        modifier = Modifier
                            .background( // Gradient for the button itself
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                )
                            )
                            // Consistent padding for the button content
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between icon and text
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null, // Button text describes action
                                tint = Color.White,
                                modifier = Modifier.size(18.dp) // Slightly smaller icon
                            )
                            //Spacer(modifier = Modifier.width(8.dp)) // Replaced by Arrangement.spacedBy
                            Text(
                                text = "Pay Now",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                //fontSize = 14.sp // Typically part of labelLarge
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddPaymentSheet) {
        FuturisticPaymentBottomSheet(
            sheetState = sheetState,
            studentName = student.name,
            onDismiss = { showAddPaymentSheet = false },
            onAddPayment = { amount, method, receiptNumber ->
                onPaymentDone(student.id, amount, method, receiptNumber)
            }
        )
    }
}



