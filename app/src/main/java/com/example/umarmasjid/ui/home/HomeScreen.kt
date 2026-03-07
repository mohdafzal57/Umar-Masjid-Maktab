package com.example.umarmasjid.ui.home

import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.umarmasjid.R
import com.example.umarmasjid.SettingsActivity
import com.example.umarmasjid.ui.home.components.QuickStatsCard
import com.example.umarmasjid.ui.home.components.TopBanner
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val onClick: () -> Unit,
    val gradient: List<Color>
)

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navigateToHome: () -> Unit,
    navigateToAttendance: () -> Unit,
    navigateToStudentManagement: () -> Unit,
    navigateToFeeManagement: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
    ) {
        TopBanner()

        EnhancedHeaderContent(
            modifier = Modifier.padding(/*top = paddingValues.calculateTopPadding()*/),
            animatedOffset = animatedOffset
        )

        MainContainer(
            modifier = Modifier.align(Alignment.BottomCenter),
            navigateToHome = navigateToHome,
            navigateToAttendance = navigateToAttendance,
            navigateToStudentManagement = navigateToStudentManagement,
            navigateToFeeManagement = navigateToFeeManagement,
        )
    }
}

@Composable
fun EnhancedHeaderContent(
    modifier: Modifier = Modifier,
    animatedOffset: Float
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(1f - 0.78f)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Modern glassmorphism card header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Innovative floating masjid icon
                InnovativeMasjidIcon(animatedOffset)

                // Modern info section
                ModernMasjidInfo(
                    modifier = Modifier.weight(1f)
                )
            }
        }
        IconButton(
            onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun InnovativeMasjidIcon(animatedOffset: Float) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        // Animated rotating ring background
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        val rotationAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Canvas(
            modifier = Modifier.size(76.dp)
                .graphicsLayer {
                    rotationZ = -rotationAngle
                }
        ) {
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.7f),
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.7f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                radius = size.minDimension / 2 - 1.dp.toPx(),
                style = Stroke(width = 2.dp.toPx()),
                center = center
            )
        }

        // Inner decorative dots (rotating)
//        Box(
//            modifier = Modifier
//                .size(68.dp)
//                .graphicsLayer { rotationZ = rotationAngle }
//        ) {
//            repeat(8) { index ->
//                val angleDegrees = index * 45f
//                // Convert degrees to radians
//                val angleRadians = Math.toRadians(angleDegrees.toDouble())
//                // Or manually: val angleRadians = angleDegrees * (PI / 180.0)
//
//
//                Box(
//                    modifier = Modifier
//                        .size(4.dp)
//                        .offset(
//                            x = 32.dp * cos(angleRadians).toFloat(), // Multiply Dp by Float/Double
//                            y = 32.dp * sin(angleRadians).toFloat()  // Multiply Dp by Float/Double
//                        )
//                        .background(
//                            Color.White.copy(alpha = 0.6f),
//                            CircleShape
//                        )
//                        .align(Alignment.Center)
//                )
//            }
//        }
        // Main icon container with subtle animation
        Surface(
            modifier = Modifier
                .size(68.dp)
                .scale(1f + animatedOffset * 0.05f),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.9f),
            shadowElevation = 8.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.umar_masjid_ic),
                    contentDescription = "umar masjid icon",
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun InnovativeMasjidIconPreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "background_preview")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_preview"
    )
    InnovativeMasjidIcon(animatedOffset = animatedOffset)
}

@Composable
fun ModernMasjidInfo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Main title with elegant typography
        Text(
            text = "Umar Masjid",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 0.5.sp
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.2f)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .widthIn(max = 130.dp),
            ) {
                Text(
                    text = "🕌 Assalamualaikum!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                )
                Text(
                    text = "Welcome to Maktab-e-Baligana",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.75f),
                    lineHeight = 2.sp,
                    modifier = Modifier.basicMarquee()
                )
            }
        }
    }
}

@Composable
fun MainContainer(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    navigateToAttendance: () -> Unit,
    navigateToStudentManagement: () -> Unit,
    navigateToFeeManagement: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.80f)
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Enhanced DateTime Card (from UI 1 but styled like UI 2)
        item {
            EnhancedDateTimeCard()
        }

        // Quick Stats Card (from UI 2)
        item {
            QuickStatsCard()
        }

        // Enhanced Navigation Section
        item {
            NavigationSection(
                navigateToHome = navigateToHome,
                navigateToAttendance = navigateToAttendance,
                navigateToStudentManagement = navigateToStudentManagement,
                navigateToFeeManagement = navigateToFeeManagement
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun EnhancedDateTimeCard() {
    val currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Gregorian formats
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // Islamic (Hijri) date
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("ar"))

    val islamicDate = remember { HijrahDate.now().format(formatter) }

    // 🎇 Twinkle animation for moon icon
    val infiniteTransition = rememberInfiniteTransition(label = "twinkle")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF006C7F).copy(alpha = 0.1f) // light teal surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 📅 Small Gregorian date in the corner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = "Gregorian Date",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = dateFormat.format(Date(currentTime)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🌙 Dominant Hijri Date with twinkling moon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.NightsStay,
                    contentDescription = "Islamic Date",
                    tint = Color(0xFF0096B1),
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer(
                            alpha = alpha,
                            scaleX = scale,
                            scaleY = scale
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = islamicDate,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006C7F)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Small subtitle (optional inspiration line or Quranic vibe)
            Text(
                text = "التاريخ الهجري",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun NavigationSection(
    navigateToHome: () -> Unit,
    navigateToAttendance: () -> Unit,
    navigateToStudentManagement: () -> Unit,
    navigateToFeeManagement: () -> Unit,
) {
    val navigationItems = remember {
        listOf(
            NavigationItem(
                title = "Home",
                icon = Icons.Default.Home,
                description = "Dashboard",
                onClick = navigateToHome,
                gradient = listOf(
                    Color(0xFF6366F1),
                    Color(0xFF8B5CF6)
                )
            ),
            NavigationItem(
                title = "Students",
                icon = Icons.Default.Person,
                description = "Manage students",
                onClick = navigateToStudentManagement,
                gradient = listOf(
                    Color(0xFF3B82F6),
                    Color(0xFF1D4ED8)
                )
            ),
            NavigationItem(
                title = "Attendance",
                icon = Icons.Default.CheckCircle,
                description = "Track presence",
                onClick = navigateToAttendance,
                gradient = listOf(
                    Color(0xFF10B981),
                    Color(0xFF059669)
                )
            ),
            NavigationItem(
                title = "Fee Management",
                icon = Icons.Default.AttachMoney,
                description = "Manage fees",
                onClick = navigateToFeeManagement,
                gradient = listOf(
                    Color(0xFFF59E0B),
                    Color(0xFFD97706)
                )
            )
        )
    }

    Column {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Shagird Management System",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(320.dp)
        ) {
            items(navigationItems) { item ->
                EnhancedNavigationCard(item = item)
            }
        }
    }
}

@Composable
fun EnhancedNavigationCard(
    item: NavigationItem
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        onClick = {
            isPressed = true
            item.onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 16.dp
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Enhanced gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = item.gradient.map { it.copy(alpha = 0.12f) },
                            start = Offset(0f, 0f),
                            end = Offset(200f, 200f)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = item.gradient[0].copy(alpha = 0.2f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(28.dp),
                            tint = item.gradient[0]
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Enhanced decorative elements
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(item.gradient[1].copy(alpha = 0.15f))
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(item.gradient[0].copy(alpha = 0.1f))
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}