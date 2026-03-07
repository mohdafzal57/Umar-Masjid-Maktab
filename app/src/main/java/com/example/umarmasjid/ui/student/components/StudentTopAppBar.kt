package com.example.umarmasjid.ui.student.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentTopAppBar(
    popToHome: () -> Unit,
    showSearchBar: Boolean,
    onSearchToggle: () -> Unit,
    studentCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    AnimatedContent(
        targetState = showSearchBar,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { if (targetState) it else -it },
                animationSpec = tween(300)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { if (targetState) -it else it },
                animationSpec = tween(300)
            )
        }
    ) { isSearching ->
        if (isSearching) {
            SearchTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onBackClick = onSearchToggle
            )
        }
        else {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Students",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = popToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (studentCount > 0) {
                        IconButton(onClick = onSearchToggle) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

        }
    }
}