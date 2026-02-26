package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isEmailSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var countdown by remember { mutableStateOf(60) }
    
    // Animation for content appearance
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, easing = FastOutSlowInEasing)
    )
    
    // Handle countdown
    LaunchedEffect(isEmailSent) {
        if (isEmailSent && countdown > 0) {
            delay(1000)
            countdown--
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color(0xFF667EEA).copy(alpha = 0.1f),
                                androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Reset Password",
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF667EEA)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = if (isEmailSent) "Check Your Email" else "Reset Password",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = if (isEmailSent) {
                            "We've sent a password reset link to your email address. Please check your inbox and follow the instructions."
                        } else {
                            "Enter your email address and we'll send you a link to reset your password."
                        },
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    if (!isEmailSent) {
                        // Email input
                        OutlinedTextField(
                            value = email,
                            onValueChange = { 
                                email = it
                                errorMessage = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Email Address") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = Color(0xFF667EEA)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667EEA),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Error message
                        if (errorMessage.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFF5252).copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = errorMessage,
                                    modifier = Modifier.padding(12.dp),
                                    color = Color(0xFFFF5252),
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        // Send button
                        Button(
                            onClick = {
                                if (email.isEmpty()) {
                                    errorMessage = "Please enter your email address"
                                    return@Button
                                }
                                
                                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    errorMessage = "Please enter a valid email address"
                                } else {
                                    isLoading = true
                                    errorMessage = ""
                                    
                                    // Simulate API call logic should be handled by ViewModel or properly
                                    isEmailSent = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667EEA)
                            ),
                            enabled = email.isNotEmpty() && !isLoading
                        ) {
                            if (isLoading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Text(
                                        text = "Sending...",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Text(
                                    text = "Send Reset Link",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        // Email sent state
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF22C55E).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.MarkEmailRead,
                                    contentDescription = "Email Sent",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFF22C55E)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = "Email Sent Successfully!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF22C55E)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Check your inbox for the reset link",
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                if (countdown > 0) {
                                    Text(
                                        text = "Resend available in ${countdown}s",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                } else {
                                    Button(
                                        onClick = {
                                            isEmailSent = false
                                            countdown = 60
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent
                                        ),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = Color(0xFF667EEA)
                                        )
                                    ) {
                                        Text(
                                            text = "Resend Email",
                                            color = Color(0xFF667EEA),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Back to login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remember your password? ",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        
                        Text(
                            text = "Sign In",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("login")
                                },
                            color = Color(0xFF667EEA),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
