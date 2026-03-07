package com.example.umarmasjid.ui.student.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.umarmasjid.data.local_database.model.StudentEntity
import com.example.umarmasjid.ui.student.ShimmerBox

@Composable
fun StudentsList(
    isLoading: Boolean,
    students: List<StudentEntity>,
    onEdit: (StudentEntity) -> Unit,
    onDelete: (StudentEntity) -> Unit,
    navigateToPayment: (Int) -> Unit,
    navigateToAttendance: (Int) -> Unit,
    searchQuery: String
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // permission granted, you can call later
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header with animation
        item {
            StudentsListHeader(
                count = students.size,
                searchQuery = searchQuery
            )
        }
        if (isLoading) {
            items(6) { // 6 shimmer placeholders
                StudentCardShimmer()
            }
        } else {
            items(
                items = students,
                key = { it.id }
            ) { student ->
                StudentCard(
                    modifier = Modifier.animateItem(
                        fadeInSpec = null,
                        fadeOutSpec = null,
                        placementSpec = spring(
                            stiffness = Spring.StiffnessMediumLow,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )
                    ),
                    student = student,
                    onEdit = { onEdit(student) },
                    onDelete = { onDelete(student) },
                    navigateToPayment = { navigateToPayment(student.id) },
                    navigateToAttendance = { navigateToAttendance(student.id) },
                    navigateToWhatsApp = {
                        try {
                            val uri = Uri.parse("https://wa.me/${student.phoneNumber}")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.whatsapp")
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context, "Whatsapp not installed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    navigateToPhoneCall = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CALL_PHONE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // ✅ Permission granted → make the call
                            val intent =
                                Intent(Intent.ACTION_CALL, Uri.parse("tel:${student.phoneNumber}"))
                            context.startActivity(intent)
                        } else {
                            // ❌ Else → ask for permission
                            permissionLauncher.launch(Manifest.permission.CALL_PHONE)
                        }
                    }
                )
            }
            // Bottom padding for FAB
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun StudentCardShimmer(modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar shimmer
            ShimmerBox(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Name shimmer
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Phone shimmer
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun StudentCard(
    modifier: Modifier = Modifier,
    student: StudentEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    navigateToPayment: () -> Unit,
    navigateToAttendance: () -> Unit,
    navigateToWhatsApp: () -> Unit,
    navigateToPhoneCall: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() } // Needed for clickable

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy, // Softer bounce
                    stiffness = Spring.StiffnessMediumLow // Softer stiffness
                )
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp, // Slightly lower default elevation for a softer look
            pressedElevation = 4.dp,
            hoveredElevation = 2.dp
        ),
        shape = RoundedCornerShape(20.dp), // Slightly more rounded corners
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer // Base container color
        )
    ) {
        Column {
            // Main clickable content area
            Surface(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null, // This disables the ripple
                    onClick = { isExpanded = !isExpanded }
                ),
                color = Color.Transparent, // Inherit from card
                // Apply shape only to top corners, or all if not expanded (though Column manages overall shape)
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp), // Balanced padding
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Surface(
                        modifier = Modifier.size(52.dp), // Slightly smaller avatar
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        tonalElevation = 1.dp // Add subtle elevation to avatar if desired
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name.firstOrNull()?.uppercase() ?: "S",
                                style = MaterialTheme.typography.titleMedium.copy( // Adjusted for smaller avatar
                                    fontWeight = FontWeight.SemiBold // Slightly less bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Student info
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold, // Consistent with avatar
                                letterSpacing = 0.sp // Neutral letter spacing
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp)) // Slightly more space

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Phone",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Increased spacing
                            Text(
                                text = student.phoneNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Expand arrow
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp) // Slightly larger icon for easier tap
                    )
                }
            }

            // Expanded actions
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        dampingRatio = Spring.DampingRatioNoBouncy
                    )
                ) + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)),
                exit = shrinkVertically(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        dampingRatio = Spring.DampingRatioNoBouncy
                    )
                ) + fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow))
            ) {
                // Use a slightly different background for the expanded section for subtle separation
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer, // Differentiates from card base
                    // Apply bottom rounded corners
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ), // Consistent horizontal, adjusted vertical
                        horizontalArrangement = Arrangement.spacedBy(10.dp), // Slightly less spacing between buttons
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // View Payments button
                        OutlinedButton(
                            onClick = navigateToPayment,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) // Slightly more visible border
                            ),
                            contentPadding = PaddingValues(vertical = 10.dp) // Adjust button padding
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = "Payments",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Payments",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }



                        IconButton(
                            onClick = navigateToWhatsApp,
                            modifier = Modifier
                                .clip(CircleShape) // Clip to shape before background
                                .background(
                                    MaterialTheme.colorScheme.primary // Softer background
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatsapp,
                                contentDescription = "whatsapp",
                                tint = Color.Green.copy(0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        // Attendance button
                        IconButton(
                            onClick = navigateToAttendance,
                            modifier = Modifier
                                .clip(CircleShape) // Clip to shape before background
                                .background(
                                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f) // Softer background
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DonutSmall,
                                contentDescription = "Attendance",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f), // Slightly softer tint
                                modifier = Modifier.size(20.dp)
                            )
                        }


                        // Edit button
                        FilledTonalButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(0.5f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(18.dp)
                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(
//                                text = "Edit",
//                                style = MaterialTheme.typography.labelLarge
//                            )
                        }

                        // Phone Call button
                        IconButton(
                            onClick = navigateToPhoneCall,
                            modifier = Modifier
                                .clip(CircleShape) // Clip to shape before background
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f) // Softer background
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f), // Slightly softer tint
                                modifier = Modifier.size(20.dp)
                            )
                        }


                        // Delete button
//                        IconButton(
//                            onClick = { showDeleteDialog = true },
//                            modifier = Modifier
//                                .clip(CircleShape) // Clip to shape before background
//                                .background(
//                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) // Softer background
//                                )
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Delete,
//                                contentDescription = "Delete",
//                                tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f), // Slightly softer tint
//                                modifier = Modifier.size(20.dp)
//                            )
//                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest, // Dialog specific container color
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = {
                Text(
                    text = "Delete Student",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete ${student.name}? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium // Softer than bodyLarge for dialog text
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(28.dp) // M3 Dialogs often have larger corner radii
        )
    }
}