package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.utils.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    onSplashComplete: () -> Unit
) {
    val context = LocalContext.current
    val isDarkMode = MaterialTheme.colorScheme.background == DarkBackground
    
    // Animation states
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        // Animate logo scale
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    
    LaunchedEffect(Unit) {
        // Animate logo alpha
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800)
        )
        
        delay(200)
        
        // Animate text
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600)
        )
        
        // Wait and navigate
        delay(1500)
        onSplashComplete()
        
        if (isLoggedIn) {
            val sessionManager = SessionManager.getInstance(context)
            val destination = if (sessionManager.getRole()?.equals("admin", ignoreCase = true) == true) {
                sessionManager.logout()
                "login"
            } else {
                "home"
            }
            
            navController.navigate(destination) {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDarkMode) {
                        listOf(DarkBackground, Color(0xFF0A1A0E))
                    } else {
                        listOf(LightBackground, Color(0xFFE2E8F0))
                    }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with shield icon
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                HalalGreen.copy(alpha = 0.3f),
                                HalalGreen.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    HalalGreen,
                                    HalalGreenDark
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = "Halalytics",
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name
            Text(
                text = "Halalytics",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.alpha(textAlpha.value),
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tagline
            Text(
                text = "Halal & Health Intelligence",
                fontSize = 14.sp,
                color = if (isDarkMode) TextGray else TextGrayDark,
                modifier = Modifier.alpha(textAlpha.value),
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .alpha(textAlpha.value),
                color = HalalGreen,
                strokeWidth = 2.dp
            )
        }
        
        // Footer
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(textAlpha.value)
        ) {
            Text(
                text = "© 2026 Halalytics",
                fontSize = 12.sp,
                color = if (isDarkMode) TextMuted else TextMutedDark
            )
        }
    }
}
