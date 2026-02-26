package com.example.halalyticscompose.ui.components

import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Main layout wrapper that includes bottom navigation for main screens
 * with dynamic hide/show behavior
 */
@Composable
fun MainLayout(
    navController: NavController,
    showBottomNav: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    var isBottomNavVisible by remember { mutableStateOf(true) }

    LaunchedEffect(showBottomNav) {
        isBottomNavVisible = showBottomNav
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content with dynamic bottom padding
        val bottomPadding = if (showBottomNav && isBottomNavVisible) 80.dp else 0.dp
        content(
            PaddingValues(
                bottom = bottomPadding
            )
        )
        
        // Bottom Navigation Bar with animation
        if (showBottomNav) {
            AnimatedVisibility(
                visible = isBottomNavVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BottomNavBar(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { 
                                    // Show bottom nav when user starts dragging up from bottom
                                    isBottomNavVisible = true 
                                },
                                onDragEnd = { 
                                    // Keep bottom nav visible after drag ends
                                    isBottomNavVisible = true
                                }
                            ) { _, dragAmount ->
                                // Show bottom nav when dragging up
                                if (dragAmount.y < -50) {
                                    isBottomNavVisible = true
                                }
                            }
                        }
                )
            }
        }
    }
}
