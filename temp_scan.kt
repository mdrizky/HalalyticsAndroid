package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0 = Barcode, 1 = Ingredients
    var isScanning by remember { mutableStateOf(false) }
    var scannedCode by remember { mutableStateOf("") }
    var showFlash by remember { mutableStateOf(false) }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
    
    // Reset state when screen is opened
    LaunchedEffect(Unit) {
        isScanning = false
        scannedCode = ""
        showFlash = false
        selectedTab = 0
    }
    
    // Animation for scan line
    val infiniteTransition = rememberInfiniteTransition()
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }
                
                Text(
                            text = "Scan Produk",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                
                // Flash toggle
                IconButton(
                    onClick = { showFlash = !showFlash }
                ) {
                    Icon(
                        if (showFlash) Icons.Filled.FlashOn else Icons.Outlined.FlashOff,
                        contentDescription = "Toggle Flash",
                        tint = if (showFlash) HalalGreen else TextWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            
            // Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TabButton(
                    text = "Barcode",
                    icon = Icons.Outlined.QrCodeScanner,
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                TabButton(
                    text = "Ingredients",
                    icon = Icons.Outlined.Description,
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
                // Camera preview area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1A2E1E)),
                contentAlignment = Alignment.Center
            ) {
                if (isScanning && hasCameraPermission) {
                    CameraPreview(
                        onBarcodeDetected = { barcode ->
                            if (isScanning && scannedCode.isEmpty()) {
                                println("📸 Barcode detected: $barcode")
                                scannedCode = barcode
                                isScanning = false
                                println("🔄 Navigating to product detail for barcode: $barcode")
                                navController.navigate("product_detail/$barcode")
                            }
                        },
                        showFlash = showFlash
                    )
                } else if (!hasCameraPermission) {
                     Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             Icon(
                                Icons.Outlined.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                             Spacer(modifier = Modifier.height(16.dp))
                             Text("Izin kamera diperlukan", color = Color.White, fontSize = 16.sp)
                             Spacer(modifier = Modifier.height(8.dp))
                             Text("Untuk scan barcode, izinkan akses kamera", color = Color.Gray, fontSize = 12.sp)
                             Spacer(modifier = Modifier.height(16.dp))
                             Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                                 Text("Berikan Izin")
                             }
                         }
                    }
                } else {
                    // Placeholder when not scanning
                     Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             Icon(
                                if (selectedTab == 0) Icons.Outlined.QrCodeScanner else Icons.Outlined.Description,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                             Spacer(modifier = Modifier.height(16.dp))
                             Text("Kamera Siap", color = Color.White, fontSize = 16.sp)
                             Spacer(modifier = Modifier.height(8.dp))
                             Text("Tekan 'Mulai Scan' untuk mengaktifkan kamera", color = Color.Gray, fontSize = 12.sp)
                         }
                    }
                }

                // Overlay UI (Scan Frame, etc)
                // LIVE badge etc... (Keeping existing overlay logic visually)
                   Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                   ) {
                       // ... (Reuse existing overlay components if possible, or re-declare them here for clarity)
                       // Moving the overlay components outside this conditional in the next logical block or keeping them superimposed
                       
                    // LIVE badge
                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .background(
                                    HaramColor,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color.White, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                     // Scanning frame
                    Box(
                        modifier = Modifier.size(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scan line
                        if (isScanning) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .offset(y = (scanLinePosition * 200 - 100).dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                HalalGreen,
                                                HalalGreen,
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }
                        
                        // Corner brackets
                        CornerBracket(Alignment.TopStart)
                        CornerBracket(Alignment.TopEnd)
                        CornerBracket(Alignment.BottomStart)
                        CornerBracket(Alignment.BottomEnd)
                        
                        // Instructions
                         if (!isScanning) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    if (selectedTab == 0) Icons.Outlined.QrCodeScanner
                                    else Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (selectedTab == 0)
                                        "Position barcode\nwithin the frame"
                                    else
                                        "Position ingredient list\nwithin the frame",
                                    fontSize = 13.sp,
                                    color = TextGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                   }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bottom buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gallery button
                OutlinedButton(
                    onClick = { /* TODO: Open gallery */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextWhite
                    )
                ) {
                    Icon(
                        Icons.Outlined.PhotoLibrary,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Import dari Galeri",
                        fontSize = 14.sp,
                        color = TextGray
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Mulai Scan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBackground
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Manual input link
                Text(
                    text = "atau masukkan barcode manual",
                    fontSize = 13.sp,
                    color = HalalGreen,
                    modifier = Modifier.clickable {
                        navController.navigate("manual_input")
                    }
                )
            }
        }
    }
}
