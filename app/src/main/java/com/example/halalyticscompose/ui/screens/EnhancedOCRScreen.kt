package com.example.halalyticscompose.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import com.example.halalyticscompose.ui.viewmodel.OCRViewModel
import com.example.halalyticscompose.utils.MlKitTextScanner
import com.example.halalyticscompose.utils.SessionManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.launch

private enum class IngredientLevel { HALAL, HARAM, WARNING }

private data class OverlayIngredient(
    val text: String,
    val level: IngredientLevel,
    val reason: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOCRScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    ocrViewModel: OCRViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val sessionManager = remember { SessionManager.getInstance(context) }

    val accessToken by mainViewModel.accessToken.collectAsState(initial = null)
    val isSubmitting by ocrViewModel.isSubmitting.collectAsState(initial = false)

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var capturedFrontPath by remember { mutableStateOf<String?>(null) }
    var capturedBackPath by remember { mutableStateOf<String?>(null) }
    var captureStep by remember { mutableStateOf(0) } // 0: Front, 1: Back, 2: Done
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var overlayIngredients by remember { mutableStateOf<List<OverlayIngredient>>(emptyList()) }
    var smartInventoryEnabled by remember { mutableStateOf(true) }
    var detectedProductName by remember { mutableStateOf("") }
    var detectedBrand by remember { mutableStateOf("") }
    var detectedBarcode by remember { mutableStateOf("") }
    var detectedIngredientsText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                analyzeCapturedImage(
                    context = context,
                    imageUri = it,
                    onDone = { overlays, rawText, guessedName ->
                        if (captureStep == 0) capturedFrontPath = it.toString() else capturedBackPath = it.toString()
                        overlayIngredients = overlays
                        detectedIngredientsText = rawText
                        if (detectedProductName.isBlank()) detectedProductName = guessedName
                    },
                    onError = { err -> errorMessage = err }
                )
            }
        }
    )

    val aggregateLevel = remember(overlayIngredients) {
        when {
            overlayIngredients.any { it.level == IngredientLevel.HARAM } -> IngredientLevel.HARAM
            overlayIngredients.any { it.level == IngredientLevel.WARNING } -> IngredientLevel.WARNING
            overlayIngredients.isNotEmpty() -> IngredientLevel.HALAL
            else -> IngredientLevel.WARNING
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val scanAnim = rememberInfiniteTransition(label = "scan")
    val scanY by scanAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2200, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "scanY"
    )

    Scaffold(
        containerColor = Color(0xFFF7FAF8),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF057A55)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("H", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scanner Hub", color = Color(0xFF057A55), fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(430.dp)
                        .background(Color(0xFF0B1220))
                ) {
                    val currentDisplayImage = when (captureStep) {
                        0 -> capturedFrontPath
                        1 -> capturedBackPath
                        else -> capturedBackPath ?: capturedFrontPath
                    }

                    if (currentDisplayImage != null && captureStep == 2) {
                        AsyncImage(
                            model = currentDisplayImage,
                            contentDescription = "Captured",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        LiveCameraPreview(
                            hasCameraPermission = hasCameraPermission,
                            lifecycleOwner = lifecycleOwner,
                            onCaptureReady = { imageCapture = it },
                            onRealtimeDetected = { overlays ->
                                if (captureStep == 1) { // Only analyze ingredients when on back side
                                    overlayIngredients = overlays
                                    if (detectedIngredientsText.isBlank()) {
                                        detectedIngredientsText = overlays.joinToString(", ") { it.text }
                                    }
                                }
                            }
                        )
                    }

                    // Scanner frame
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(0.75f)
                            .height(220.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    )

                    // Step Indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = when(captureStep) {
                                0 -> "STEP 1: FOTO DEPAN PRODUK"
                                1 -> "STEP 2: FOTO KOMPOSISI (BELAKANG)"
                                else -> "ANALISIS SIAP"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Scan line
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(0.78f)
                            .height(2.dp)
                            .offset(y = ((scanY - 0.5f) * 180f).dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0xFF06B6D4), Color.Transparent)
                                )
                            )
                    )

                    // Overlay ingredient boxes
                    if (overlayIngredients.isNotEmpty() && captureStep == 1) {
                        OverlayIngredientBoxes(items = overlayIngredients)
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.45f))
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Gallery", tint = Color.White)
                        }

                        IconButton(
                            onClick = {
                                captureCurrentFrame(
                                    capture = imageCapture,
                                    context = context,
                                    onCaptured = { path ->
                                        if (captureStep == 0) {
                                            capturedFrontPath = path
                                            captureStep = 1
                                            Toast.makeText(context, "Sekarang foto bagian Ingredients", Toast.LENGTH_SHORT).show()
                                        } else {
                                            capturedBackPath = path
                                            captureStep = 2
                                            analyzeCapturedImage(
                                                context = context,
                                                imageUri = Uri.fromFile(File(path)),
                                                onDone = { overlays, rawText, guessedName ->
                                                    overlayIngredients = overlays
                                                    detectedIngredientsText = rawText
                                                    if (detectedProductName.isBlank()) detectedProductName = guessedName
                                                },
                                                onError = { err -> errorMessage = err }
                                            )
                                        }
                                    },
                                    onError = { err -> errorMessage = err }
                                )
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (captureStep == 2) Color(0xFF10B981) else Color(0xFF00B894))
                        ) {
                            Icon(
                                imageVector = if (captureStep == 2) Icons.Default.CheckCircle else Icons.Default.QrCodeScanner,
                                contentDescription = "Capture",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = { 
                                capturedFrontPath = null
                                capturedBackPath = null
                                captureStep = 0
                                overlayIngredients = emptyList()
                                detectedIngredientsText = ""
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.45f))
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = "Reset", tint = Color.White)
                        }
                    }

                    if (!hasCameraPermission) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Izin kamera dibutuhkan", color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                                Text("Berikan Izin")
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Analysis Results", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF111827))
                            StatusPill(level = aggregateLevel)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (detectedProductName.isBlank()) "Produk belum dikenali" else "AI Detected: $detectedProductName",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4FBF7)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB7E9D3))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF059669))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("AI Deep Analysis", fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                                    Text("Menggunakan Gemini Vision untuk cek risiko", fontSize = 12.sp, color = Color(0xFF047857))
                                }
                                Switch(checked = smartInventoryEnabled, onCheckedChange = { smartInventoryEnabled = it })
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("INGREDIENTS INSIGHT", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (overlayIngredients.isEmpty()) {
                            Text(
                                text = "Foto kemasan depan lalu kemasan belakang untuk melihat hasil analisis AI.",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        } else {
                            overlayIngredients.take(6).forEach { item ->
                                IngredientResultRow(item)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (!errorMessage.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(errorMessage ?: "", color = Color(0xFFDC2626), fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (capturedFrontPath == null || capturedBackPath == null) {
                                    Toast.makeText(context, "Lengkapi foto depan dan belakang", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val token = accessToken ?: sessionManager.getAuthToken().orEmpty()
                                if (token.isBlank()) {
                                    Toast.makeText(context, "Sesi login tidak ditemukan", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                ocrViewModel.submitOCR(
                                    token = token,
                                    frontImage = File(capturedFrontPath!!),
                                    backImage = File(capturedBackPath!!),
                                    ocrText = detectedIngredientsText,
                                    familyMemberId = null,
                                    language = "id",
                                    onSuccess = {
                                        Toast.makeText(context, "Analisis AI Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                                        navController.navigate("history")
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            enabled = !isSubmitting && captureStep == 2,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Process AI Report", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveCameraPreview(
    hasCameraPermission: Boolean,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onCaptureReady: (ImageCapture) -> Unit,
    onRealtimeDetected: (List<OverlayIngredient>) -> Unit
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val analysisExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(hasCameraPermission) {
        val provider = cameraProviderFuture.get()
        if (hasCameraPermission) {
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageCapture = ImageCapture.Builder().build()
            onCaptureReady(imageCapture)

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(
                analysisExecutor,
                RealtimeIngredientAnalyzer { overlays -> onRealtimeDetected(overlays) }
            )

            val selector = if (provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            try {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture, imageAnalysis)
            } catch (e: Exception) {
                Log.e("EnhancedOCR", "bind camera failed", e)
            }
        }

        onDispose {
            runCatching { cameraProviderFuture.get().unbindAll() }
            runCatching { analysisExecutor.shutdown() }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun OverlayIngredientBoxes(items: List<OverlayIngredient>) {
    Box(modifier = Modifier.fillMaxSize()) {
        items.take(4).forEach { item ->
            val color = when (item.level) {
                IngredientLevel.HALAL -> Color(0xFF22C55E)
                IngredientLevel.HARAM -> Color(0xFFEF4444)
                IngredientLevel.WARNING -> Color(0xFFF59E0B)
            }
            val label = when (item.level) {
                IngredientLevel.HALAL -> "HALAL"
                IngredientLevel.HARAM -> "HARAM"
                IngredientLevel.WARNING -> "CHECK"
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = (item.left * 260f).dp,
                        top = (item.top * 320f).dp,
                        end = ((1f - item.right) * 260f).dp,
                        bottom = ((1f - item.bottom) * 320f).dp
                    )
                    .border(2.dp, color, RoundedCornerShape(8.dp))
            )

            Box(
                modifier = Modifier
                    .offset(x = (item.left * 280f).dp, y = (item.top * 320f - 22f).dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${item.text.take(16)} ($label)",
                    color = Color.White,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun IngredientResultRow(item: OverlayIngredient) {
    val (iconColor, borderColor, titleColor) = when (item.level) {
        IngredientLevel.HALAL -> Triple(Color(0xFF16A34A), Color(0xFFBBF7D0), Color(0xFF065F46))
        IngredientLevel.HARAM -> Triple(Color(0xFFDC2626), Color(0xFFFECACA), Color(0xFF7F1D1D))
        IngredientLevel.WARNING -> Triple(Color(0xFFD97706), Color(0xFFFDE68A), Color(0xFF78350F))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (item.level) {
                    IngredientLevel.HALAL -> Icons.Default.CheckCircle
                    IngredientLevel.HARAM -> Icons.Default.ErrorOutline
                    IngredientLevel.WARNING -> Icons.Default.ErrorOutline
                },
                contentDescription = null,
                tint = iconColor
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.text, fontWeight = FontWeight.Bold, color = titleColor)
                Text(item.reason, fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
private fun StatusPill(level: IngredientLevel) {
    val color = when (level) {
        IngredientLevel.HALAL -> Color(0xFF10B981)
        IngredientLevel.HARAM -> Color(0xFFEF4444)
        IngredientLevel.WARNING -> Color(0xFFF59E0B)
    }
    val text = when (level) {
        IngredientLevel.HALAL -> "SAFE"
        IngredientLevel.HARAM -> "CONTAINS HARAM"
        IngredientLevel.WARNING -> "VERIFICATION NEEDED"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

private fun classifyIngredientLine(line: String): Pair<IngredientLevel, String> {
    val text = line.lowercase(Locale.getDefault())
    val haramKeywords = listOf("gelatin", "lard", "pork", "babi", "carmine", "e120", "alcohol", "ethanol", "rum")
    val safeKeywords = listOf("pectin", "lecithin", "citric acid", "vitamin c", "salt", "water", "agar")

    return when {
        haramKeywords.any { text.contains(it) } -> IngredientLevel.HARAM to "Derived from non-halal source. Prohibited."
        safeKeywords.any { text.contains(it) } -> IngredientLevel.HALAL to "Plant-derived or commonly safe ingredient."
        else -> IngredientLevel.WARNING to "Sumber belum jelas, perlu verifikasi lanjutan."
    }
}

private fun parseTextToOverlay(text: String): List<OverlayIngredient> {
    val lines = text
        .split("\n")
        .map { it.trim() }
        .filter { it.length >= 3 }
        .take(8)

    if (lines.isEmpty()) return emptyList()

    return lines.mapIndexed { index, line ->
        val (level, reason) = classifyIngredientLine(line)
        val top = 0.18f + (index * 0.08f)
        OverlayIngredient(
            text = line,
            level = level,
            reason = reason,
            left = 0.18f,
            top = top.coerceAtMost(0.82f),
            right = 0.82f,
            bottom = (top + 0.06f).coerceAtMost(0.90f)
        )
    }
}

private fun guessProductName(text: String): String {
    return text
        .split("\n")
        .map { it.trim() }
        .firstOrNull { it.length > 4 && !it.contains(":") }
        ?.take(50)
        ?: "Produk OCR"
}

private fun analyzeCapturedImage(
    context: Context,
    imageUri: Uri,
    onDone: (List<OverlayIngredient>, String, String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        if (bitmap == null) {
            onError("Gagal membaca gambar")
            return
        }

        // Use suspend function from existing utility within coroutine scope
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            val rawText = MlKitTextScanner.readTextFromBitmap(bitmap)
            if (rawText.isBlank()) {
                onError("Tidak ada teks ingredients terdeteksi")
            } else {
                onDone(parseTextToOverlay(rawText), rawText, guessProductName(rawText))
            }
        }
    } catch (e: Exception) {
        onError(e.message ?: "Analisis OCR gagal")
    }
}

private fun captureCurrentFrame(
    capture: ImageCapture?,
    context: Context,
    onCaptured: (String) -> Unit,
    onError: (String) -> Unit
) {
    val imageCapture = capture ?: run {
        onError("Kamera belum siap")
        return
    }

    val file = File(context.cacheDir, "ocr_capture_${System.currentTimeMillis()}.jpg")
    val options = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        options,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onCaptured(file.absolutePath)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception.message ?: "Gagal capture")
            }
        }
    )
}

private class RealtimeIngredientAnalyzer(
    private val onDetected: (List<OverlayIngredient>) -> Unit
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var lastRunTs = 0L
    @Volatile private var processing = false

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val now = SystemClock.elapsedRealtime()
        if (processing || now - lastRunTs < 900) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        processing = true
        lastRunTs = now
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val imageW = imageProxy.width.toFloat().coerceAtLeast(1f)
                val imageH = imageProxy.height.toFloat().coerceAtLeast(1f)

                val overlays = visionText.textBlocks
                    .flatMap { it.lines }
                    .mapNotNull { line ->
                        val txt = line.text.trim()
                        if (txt.length < 3) return@mapNotNull null
                        val box = line.boundingBox ?: return@mapNotNull null
                        val (level, reason) = classifyIngredientLine(txt)
                        OverlayIngredient(
                            text = txt,
                            level = level,
                            reason = reason,
                            left = (box.left / imageW).coerceIn(0f, 1f),
                            top = (box.top / imageH).coerceIn(0f, 1f),
                            right = (box.right / imageW).coerceIn(0f, 1f),
                            bottom = (box.bottom / imageH).coerceIn(0f, 1f)
                        )
                    }
                    .take(6)

                onDetected(overlays)
            }
            .addOnFailureListener { e ->
                Log.e("RealtimeIngredientAnalyzer", "OCR frame failed", e)
            }
            .addOnCompleteListener {
                processing = false
                imageProxy.close()
            }
    }
}
