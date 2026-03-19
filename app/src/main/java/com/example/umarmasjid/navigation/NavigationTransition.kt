package com.example.umarmasjid.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent

fun NavDisplay.horizontalSlideTransition() =
    transitionSpec {
        slideInHorizontally(tween(350)) { it } togetherWith
                slideOutHorizontally(tween(350)) { -it / 3 }
    } + popTransitionSpec {
        slideInHorizontally(tween(350)) { -it / 3 } togetherWith
                slideOutHorizontally(tween(350)) { it }
    } + predictivePopTransitionSpec { _ ->
        slideInHorizontally(tween(350)) { -it / 2 } togetherWith
                slideOutHorizontally(tween(350)) { it }
    }

fun <T : Any> noneTransition():
        AnimatedContentTransitionScope<Scene<T>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform =
    {
        ContentTransform(
            targetContentEnter = fadeIn(
                spring(
                    dampingRatio = 1.0f,
                    stiffness = 1600.0f,
                )
            ),
            initialContentExit = ExitTransition.None
        )
    }

//            transitionSpec = {
//                slideInHorizontally(tween(350)) { it } togetherWith
//                        slideOutHorizontally(tween(350)) { -it / 3 }
//            },
//
//            popTransitionSpec = {
//                slideInHorizontally(tween(350)) { -it / 3 } togetherWith
//                        slideOutHorizontally(tween(350)) { it }
//            },
//
//            predictivePopTransitionSpec = { _ ->
//                slideInHorizontally(tween(350)) { -it / 2 } togetherWith
//                        slideOutHorizontally(tween(350)) { it }
//            }
//            transitionSpec = {
//                slideInHorizontally { it } togetherWith
//                        slideOutHorizontally { -it / 3 }
//            },
//
//            popTransitionSpec = {
//                slideInHorizontally { -it / 3 } togetherWith
//                        slideOutHorizontally { it }
//            },
//
//            predictivePopTransitionSpec = { edge ->
//                slideInHorizontally { -it / 2 } togetherWith
//                        slideOutHorizontally { it }
//            }