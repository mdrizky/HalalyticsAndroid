package com.example.halalyticscompose.healthcare.screens

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.halalyticscompose.healthcare.viewmodel.HealthScannerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScannerScreen(
    navController: NavController,
    viewModel: HealthScannerViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isProcessing by viewModel.isProcessing.collectAsState()
    
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    // Scan line animation
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                    } catch (e: Exception) {}
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .border(2.dp, Color(0xFF0EA5E9), RoundedCornerShape(24.dp))
        ) {
            // Scan line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color(0xFF0EA5E9))
                    .offset(y = (scanProgress * 600).dp)
            )
        }

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Health Scanner",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isProcessing) {
                CircularProgressIndicator(color = Color(0xFF0EA5E9))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Analyzing Health Impact...", color = Color.White)
            } else {
                Button(
                    onClick = { 
                        // In a real app, we'd capture image and call OCR here
                        viewModel.analyzeProduct("Sample Product", listOf("Sugar", "Salt", "Palm Oil"))
                        navController.navigate("health_analysis")
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Composition", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // File Upload Button
                val launcher = rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
                ) { uri: android.net.Uri? ->
                    uri?.let {
                        viewModel.analyzeProduct("Medical Report", listOf("Analysis from uploaded file"))
                        navController.navigate("health_analysis")
                    }
                }

                OutlinedButton(
                    onClick = { launcher.launch("*/*") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Report/Image")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "AI Analysis: Not a substitute for medical advice",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
