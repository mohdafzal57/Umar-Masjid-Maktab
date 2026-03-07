//package com.example.umarmasjid.ui.home
//
//import androidx.compose.animation.core.LinearEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.Spring
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.spring
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccessTime
//import androidx.compose.material.icons.filled.AttachMoney
//import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Mosque
//import androidx.compose.material.icons.filled.Payment
//import androidx.compose.material.icons.filled.School
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import kotlinx.coroutines.delay
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//data class NavigationItem(
//    val title: String,
//    val icon: ImageVector,
//    val description: String,
//    val onClick: () -> Unit,
//    val gradient: List<Color>
//)
//
//@Composable
//fun HomeScreen2(
//    navigateToHome: () -> Unit,
//    navigateToAttendance: () -> Unit,
//    navigateToStudentManagement: () -> Unit,
//    navigateToFeeManagement: () -> Unit,
//    paddingValues: PaddingValues,
//) {
//    val infiniteTransition = rememberInfiniteTransition(label = "background")
//    val animatedOffset by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(20000, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "gradient"
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.linearGradient(
//                    colors = listOf(
//                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f + animatedOffset * 0.1f),
//                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
//                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f - animatedOffset * 0.05f)
//                    ),
//                    start = Offset(0f, 0f),
//                    end = Offset(1000f, 1000f)
//                )
//            )
//    ) {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(20.dp)
//        ) {
//            item {
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            item {
//                WelcomeCard()
//            }
//
//            item {
//                DateTimeCard()
//            }
//
//            item {
//                QuickStatsCard()
//            }
//
//            item {
//                NavigationGrid(
//                    navigateToHome = navigateToHome,
//                    navigateToAttendance = navigateToAttendance,
//                    navigateToStudentManagement = navigateToStudentManagement,
//                    navigateToFeeManagement = navigateToFeeManagement
//                )
//            }
//
//            item {
//                Spacer(modifier = Modifier.height(32.dp))
//            }
//        }
//    }
//}
//
//@Composable
//fun WelcomeCard() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
//        shape = MaterialTheme.shapes.extraLarge
//    ) {
//        Row(
//            modifier = Modifier.padding(24.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(72.dp)
//                    .clip(CircleShape)
//                    .background(
//                        brush = Brush.radialGradient(
//                            colors = listOf(
//                                MaterialTheme.colorScheme.primary,
//                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
//                            )
//                        )
//                    )
//                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Mosque,
//                    contentDescription = "Mosque Icon",
//                    modifier = Modifier.size(36.dp),
//                    tint = MaterialTheme.colorScheme.onPrimary
//                )
//            }
//
//            Spacer(modifier = Modifier.width(20.dp))
//
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = "Umar Masjid App",
//                    style = MaterialTheme.typography.headlineSmall,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.background
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = "Assalamualaikum!",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Medium,
//                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
//                )
//                Spacer(modifier = Modifier.height(2.dp))
//                Text(
//                    text = "Welcome to Maktab-e-Baligana",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
//                    lineHeight = 18.sp
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun DateTimeCard() {
//    val currentTime by remember {
//        mutableStateOf(System.currentTimeMillis())
//    }
//
//    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
//    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
//    val islamicDate = "15 Safar 1446 AH" // You can integrate Islamic calendar API
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = MaterialTheme.shapes.large
//    ) {
//        Column(
//            modifier = Modifier.padding(20.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.DateRange,
//                            contentDescription = "Date",
//                            tint = MaterialTheme.colorScheme.primary,
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = dateFormat.format(Date(currentTime)),
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.SemiBold,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text(
//                        text = islamicDate,
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//
//                Surface(
//                    shape = MaterialTheme.shapes.medium,
//                    color = MaterialTheme.colorScheme.primaryContainer,
//                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                ) {
//                    Row(
//                        modifier = Modifier.padding(12.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.AccessTime,
//                            contentDescription = "Time",
//                            modifier = Modifier.size(18.dp),
//                            tint = MaterialTheme.colorScheme.surface
//                        )
//                        Spacer(modifier = Modifier.width(6.dp))
//                        Text(
//                            text = timeFormat.format(Date(currentTime)),
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.surface
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun QuickStatsCard() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        shape = MaterialTheme.shapes.large
//    ) {
//        Column(
//            modifier = Modifier.padding(20.dp)
//        ) {
//            Text(
//                text = "Quick Overview",
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                QuickStatItem(
//                    icon = Icons.Default.School,
//                    value = "24",
//                    label = "Students",
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                QuickStatItem(
//                    icon = Icons.Default.CheckCircle,
//                    value = "18",
//                    label = "Present Today",
//                    color = MaterialTheme.colorScheme.tertiary
//                )
//
//                QuickStatItem(
//                    icon = Icons.Default.AttachMoney,
//                    value = "$2,450",
//                    label = "This Month",
//                    color = MaterialTheme.colorScheme.secondary
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun QuickStatItem(
//    icon: ImageVector,
//    value: String,
//    label: String,
//    color: Color
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Surface(
//            shape = CircleShape,
//            color = color.copy(alpha = 0.1f),
//            modifier = Modifier.size(48.dp)
//        ) {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = null,
//                    modifier = Modifier.size(24.dp),
//                    tint = color
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = value,
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//
//        Text(
//            text = label,
//            style = MaterialTheme.typography.labelMedium,
//            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
//            textAlign = TextAlign.Center
//        )
//    }
//}
//
//@Composable
//fun NavigationGrid(
//    navigateToHome: () -> Unit,
//    navigateToAttendance: () -> Unit,
//    navigateToStudentManagement: () -> Unit,
//    navigateToFeeManagement: () -> Unit,
//) {
//    val navigationItems = remember {
//        listOf(
//            NavigationItem(
//                title = "Home",
//                icon = Icons.Default.Home,
//                description = "Main dashboard",
//                onClick = navigateToHome,
//                gradient = listOf(
//                    Color(0xFF6366F1),
//                    Color(0xFF8B5CF6)
//                )
//            ),
//            NavigationItem(
//                title = "Students",
//                icon = Icons.Default.School,
//                description = "Manage students",
//                onClick = navigateToStudentManagement,
//                gradient = listOf(
//                    Color(0xFF3B82F6),
//                    Color(0xFF1D4ED8)
//                )
//            ),
//            NavigationItem(
//                title = "Attendance",
//                icon = Icons.Default.CheckCircle,
//                description = "Track presence",
//                onClick = navigateToAttendance,
//                gradient = listOf(
//                    Color(0xFF10B981),
//                    Color(0xFF059669)
//                )
//            ),
//            NavigationItem(
//                title = "Payments",
//                icon = Icons.Default.Payment,
//                description = "Fee management",
//                onClick = navigateToFeeManagement,
//                gradient = listOf(
//                    Color(0xFFF59E0B),
//                    Color(0xFFD97706)
//                )
//            )
//        )
//    }
//
//    Column {
//        Text(
//            text = "Navigation",
//            style = MaterialTheme.typography.titleLarge,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//            modifier = Modifier.height(320.dp) // Fixed height to prevent infinite expansion
//        ) {
//            items(navigationItems) { item ->
//                ModernNavigationCard(item = item)
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun ModernNavigationCardPreview() {
//    ModernNavigationCard(
//        item = NavigationItem(
//            title = "Home",
//            icon = Icons.Default.Home,
//            description = "its very decorative home",
//            onClick = {  },
//            gradient = listOf(
//                Color(0xFF3B82F6),
//                Color(0xFF1D4ED8)
//            )
//        )
//    )
//}
//
//@Composable
//fun ModernNavigationCard(
//    item: NavigationItem
//) {
//    var isPressed by remember { mutableStateOf(false) }
//    val scale by animateFloatAsState(
//        targetValue = if (isPressed) 0.95f else 1f,
//        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
//        label = "scale"
//    )
//
//    Card(
//        onClick = {
//            isPressed = true
//            item.onClick()
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(150.dp)
//            .scale(scale),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        ),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 6.dp,
//            pressedElevation = 12.dp
//        ),
//        shape = MaterialTheme.shapes.extraLarge
//    ) {
//        Box(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            // Gradient background
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        brush = Brush.linearGradient(
//                            colors = item.gradient.map { it.copy(alpha = 0.1f) },
//                            start = Offset(0f, 0f),
//                            end = Offset(200f, 200f)
//                        )
//                    )
//            )
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Surface(
//                    shape = CircleShape,
//                    color = item.gradient[0].copy(alpha = 0.15f),
//                    modifier = Modifier.size(56.dp)
//                ) {
//                    Box(
//                        contentAlignment = Alignment.Center,
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//                        Icon(
//                            imageVector = item.icon,
//                            contentDescription = item.title,
//                            modifier = Modifier.size(28.dp),
//                            tint = item.gradient[0]
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                Text(
//                    text = item.title,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    textAlign = TextAlign.Center
//                )
//
//                Text(
//                    text = item.description,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    textAlign = TextAlign.Center
//                )
//            }
//
//            // Decorative element
//            Box(
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .offset(x = 8.dp, y = (-8).dp)
//                    .size(32.dp)
//                    .clip(CircleShape)
//                    .background(item.gradient[1].copy(alpha = 0.1f))
//            )
//        }
//    }
//
//    LaunchedEffect(isPressed) {
//        if (isPressed) {
//            delay(150)
//            isPressed = false
//        }
//    }
//}
//
///*@Composable
//fun DateTimeCard() {
//    val currentTime by remember {
//        mutableStateOf(System.currentTimeMillis())
//    }
//
//    // Auto-update time every minute
//    LaunchedEffect(Unit) {
//        while (true) {
//            kotlinx.coroutines.delay(60000) // Update every minute
//            // You can trigger recomposition here if needed
//        }
//    }
//
//    val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
//    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
//    val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
//    val islamicDate = "15 Safar 1446 AH" // Integrate with Islamic calendar API
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.secondaryContainer
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = MaterialTheme.shapes.large
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = dateFormat.format(Date(currentTime)),
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//                Text(
//                    text = yearFormat.format(Date(currentTime)),
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = islamicDate,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.primary,
//                    fontWeight = FontWeight.Medium
//                )
//            }
//
//            Surface(
//                shape = MaterialTheme.shapes.large,
//                color = MaterialTheme.colorScheme.primary,
//                contentColor = MaterialTheme.colorScheme.onPrimary
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.AccessTime,
//                        contentDescription = "Time",
//                        modifier = Modifier.size(24.dp)
//                    )
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = timeFormat.format(Date(currentTime)),
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun QuickStatsCard() {
//    // These would be passed as parameters in real implementation
//    val totalStudents = 24
//    val presentToday = 18
//    val monthlyCollection = 2450.0
//    val pendingFees = 850.0
//
//    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.tertiaryContainer
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = MaterialTheme.shapes.large
//    ) {
//        Column(
//            modifier = Modifier.padding(20.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Analytics,
//                    contentDescription = "Analytics",
//                    modifier = Modifier.size(24.dp),
//                    tint = MaterialTheme.colorScheme.onTertiaryContainer
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Today's Overview",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onTertiaryContainer
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                StatItem(
//                    value = "$totalStudents",
//                    label = "Total Students",
//                    icon = Icons.Default.School,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                StatItem(
//                    value = "$presentToday",
//                    label = "Present Today",
//                    icon = Icons.Default.CheckCircle,
//                    color = MaterialTheme.colorScheme.tertiary
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                StatItem(
//                    value = currencyFormat.format(monthlyCollection),
//                    label = "This Month",
//                    icon = Icons.Default.TrendingUp,
//                    color = MaterialTheme.colorScheme.secondary
//                )
//
//                StatItem(
//                    value = currencyFormat.format(pendingFees),
//                    label = "Pending",
//                    icon = Icons.Default.Schedule,
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
//        }
//    }
//}*/
//
//@Composable
//fun StatItem(
//    value: String,
//    label: String,
//    icon: ImageVector,
//    color: Color
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = null,
//            modifier = Modifier.size(20.dp),
//            tint = color
//        )
//        Spacer(modifier = Modifier.height(4.dp))
//        Text(
//            text = value,
//            style = MaterialTheme.typography.titleSmall,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onTertiaryContainer
//        )
//        Text(
//            text = label,
//            style = MaterialTheme.typography.labelSmall,
//            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
//            textAlign = TextAlign.Center
//        )
//    }
//}