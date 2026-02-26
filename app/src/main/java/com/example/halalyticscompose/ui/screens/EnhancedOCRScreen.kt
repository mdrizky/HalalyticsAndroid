package com.example.halalyticscompose.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.widget.Toast
import coil.compose.AsyncImage
import com.example.halalyticscompose.ui.theme.*
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.util.Log
import androidx.compose.foundation.shape.CircleShape
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import com.example.halalyticscompose.ui.viewmodel.OCRViewModel
import com.example.halalyticscompose.utils.MlKitTextScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOCRScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    ocrViewModel: OCRViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )
    
    val accessToken by mainViewModel.accessToken.collectAsState(initial = null)
    val sessionManager = remember { com.example.halalyticscompose.utils.SessionManager.getInstance(context) }
    
    var currentStep by remember { mutableIntStateOf(1) }
    var frontImageUri by remember { mutableStateOf<String?>(null) }
    var backImageUri by remember { mutableStateOf<String?>(null) }
    
    // OCR Results state
    var detectedName by remember { mutableStateOf("") }
    var detectedBrand by remember { mutableStateOf("") }
    var detectedBarcode by remember { mutableStateOf("") }
    var detectedIngredients by remember { mutableStateOf("") }
    
    val isAnalyzing by remember { mutableStateOf(false) } // We'll use local state in processing
    val isSubmitting by ocrViewModel.isSubmitting.collectAsState(initial = false)
    
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (currentStep == 1) frontImageUri = it.toString()
            else backImageUri = it.toString()
        }
    }
    
    LaunchedEffect(cameraProviderFuture) {
        cameraProvider = cameraProviderFuture.get()
    }
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
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
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.background(DarkCard, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = "OCR Food Scanner",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            IconButton(
                onClick = { navController.navigate("scan") },
                modifier = Modifier.background(DarkCard, CircleShape)
            ) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = "Switch to Barcode",
                    tint = HalalGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepIndicator(currentStep = currentStep)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when (currentStep) {
                1 -> FrontImageStep(
                    frontImageUri = frontImageUri,
                    onCapture = {
                        if (!hasCameraPermission) {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            captureImage(imageCapture, cameraExecutor, context) { uri ->
                                frontImageUri = uri
                            }
                        }
                    },
                    onGallery = { galleryLauncher.launch("image/*") },
                    onNext = { if (frontImageUri != null) currentStep = 2 },
                    scanProgress = scanProgress,
                    cameraProvider = cameraProvider,
                    lifecycleOwner = lifecycleOwner,
                    onCameraReady = { capture -> imageCapture = capture },
                    hasCameraPermission = hasCameraPermission,
                    onRequestPermission = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
                )
                
                2 -> BackImageStep(
                    backImageUri = backImageUri,
                    onCapture = {
                        if (!hasCameraPermission) {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            captureImage(imageCapture, cameraExecutor, context) { uri ->
                                backImageUri = uri
                            }
                        }
                    },
                    onGallery = { galleryLauncher.launch("image/*") },
                    onNext = { if (backImageUri != null) currentStep = 3 },
                    onBack = { currentStep = 1 },
                    scanProgress = scanProgress,
                    cameraProvider = cameraProvider,
                    lifecycleOwner = lifecycleOwner,
                    onCameraReady = { capture -> imageCapture = capture },
                    hasCameraPermission = hasCameraPermission,
                    onRequestPermission = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
                )
                
                3 -> RealOCRProcessingStep(
                    navController = navController,
                    frontImageUri = frontImageUri,
                    backImageUri = backImageUri,
                    ocrViewModel = ocrViewModel,
                    mainViewModel = mainViewModel,
                    onRetry = { 
                        frontImageUri = null
                        backImageUri = null
                        currentStep = 1 
                    }
                )
            }
        }
    }
}

@Composable
fun RealOCRProcessingStep(
    navController: NavController,
    frontImageUri: String?,
    backImageUri: String?,
    ocrViewModel: OCRViewModel,
    mainViewModel: MainViewModel,
    onRetry: () -> Unit
) {
    val context = LocalContext.current
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisDone by remember { mutableStateOf(false) }
    var analysisError by remember { mutableStateOf<String?>(null) }
    
    // Result fields for review
    var prodName by remember { mutableStateOf("") }
    var prodBrand by remember { mutableStateOf("") }
    var prodBarcode by remember { mutableStateOf("") }
    var prodIngredients by remember { mutableStateOf("") }
    var mlKitRawText by remember { mutableStateOf("") }
    
    val geminiAnalyzer = remember { com.example.halalyticscompose.ai.GeminiAnalyzer() }
    val accessToken by mainViewModel.accessToken.collectAsState(initial = null)
    val isSubmitting by ocrViewModel.isSubmitting.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        if (!analysisDone && frontImageUri != null) {
            try {
                isAnalyzing = true
                val bitmap = uriToBitmap(context, Uri.parse(frontImageUri))
                if (bitmap != null) {
                    mlKitRawText = MlKitTextScanner.readTextFromBitmap(bitmap)
                    if (mlKitRawText.isNotBlank() && prodIngredients.isBlank()) {
                        prodIngredients = mlKitRawText
                    }

                    geminiAnalyzer.analyzeImage(bitmap).collect { result ->
                        when(result) {
                            is com.example.halalyticscompose.ai.AiAnalysisResult.Loading -> isAnalyzing = true
                            is com.example.halalyticscompose.ai.AiAnalysisResult.Success -> {
                                // Parse JSON from Gemini
                                try {
                                    val json = result.analysis.substringAfter("{").substringBeforeLast("}")
                                    val cleanedJson = "{$json}"
                                    val gson = com.google.gson.Gson()
                                    val map = gson.fromJson(cleanedJson, Map::class.java)
                                    
                                    prodName = map["name"]?.toString() ?: ""
                                    prodBrand = map["brand"]?.toString() ?: ""
                                    prodBarcode = map["barcode"]?.toString() ?: ""
                                    prodIngredients = map["ingredients"]?.toString()?.takeIf { it.isNotBlank() } ?: mlKitRawText
                                    
                                    analysisDone = true
                                    isAnalyzing = false
                                } catch (e: Exception) {
                                    // Fallback if JSON parse fails - show raw or error
                                    prodName = "Hasil Deteksi AI"
                                    prodIngredients = if (mlKitRawText.isNotBlank()) mlKitRawText else result.analysis
                                    analysisDone = true
                                    isAnalyzing = false
                                }
                            }
                            is com.example.halalyticscompose.ai.AiAnalysisResult.Error -> {
                                analysisError = result.message
                                isAnalyzing = false
                            }
                        }
                    }
                } else {
                    analysisError = "Gagal memuat gambar"
                    isAnalyzing = false
                }
            } catch (e: Exception) {
                analysisError = e.message
                isAnalyzing = false
            }
        }
    }

    if (isAnalyzing) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            CircularProgressIndicator(color = HalalGreen)
            Spacer(modifier = Modifier.height(24.dp))
            Text("AI sedang menganalisis foto produk...", color = Color.White, textAlign = TextAlign.Center)
            Text("Proses ini membutuhkan waktu beberapa detik", color = TextGray, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    } else if (analysisError != null) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Analisis Gagal", color = Color.White, fontWeight = FontWeight.Bold)
            Text(analysisError!!, color = Color.Red, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = HalalGreen)) {
                Text("Coba Lagi", color = DarkBackground)
            }
        }
    } else if (analysisDone) {
        // Review Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Review Hasil AI", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text("Silakan koreksi jika ada data yang kurang tepat", color = TextGray, fontSize = 13.sp)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                OutlinedTextField(
                    value = prodName,
                    onValueChange = { prodName = it },
                    label = { Text("Nama Produk") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = prodBrand,
                    onValueChange = { prodBrand = it },
                    label = { Text("Brand") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = prodBarcode,
                    onValueChange = { prodBarcode = it },
                    label = { Text("Barcode") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = prodIngredients,
                    onValueChange = { prodIngredients = it },
                    label = { Text("Komposisi") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                if (mlKitRawText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ML Kit OCR aktif: komposisi terdeteksi otomatis untuk bantu akurasi.",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        val session = com.example.halalyticscompose.utils.SessionManager.getInstance(context)
                        val userId = session.getUserId().toString()
                        
                        ocrViewModel.submitProduct(
                            token = accessToken ?: "",
                            barcode = prodBarcode,
                            name = prodName,
                            brand = prodBrand,
                            ingredients = prodIngredients,
                            userId = userId,
                            onSuccess = { result ->
                                Toast.makeText(context, "Produk berhasil disimpan ke database!", Toast.LENGTH_LONG).show()
                                navController.navigate("product_detail/${result.barcode}") {
                                    popUpTo("enhanced_ocr") { inclusive = true }
                                }
                            },
                            onError = { err -> 
                                Toast.makeText(context, "Gagal: $err", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HalalGreen),
                    enabled = !isSubmitting && prodName.isNotBlank()
                ) {
                    if (isSubmitting) {
                         CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DarkBackground)
                    } else {
                        Text("Simpan ke Database", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
                
                TextButton(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                    Text("Batal & Reset", color = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}

// Helper to convert URI to Bitmap
private fun uriToBitmap(context: android.content.Context, uri: Uri): android.graphics.Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        android.graphics.BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { step ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        if (step + 1 <= currentStep) HalalGreen else Color.Gray.copy(alpha = 0.3f),
                        RoundedCornerShape(6.dp)
                    )
            )
            
            if (step < 2) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(
                            if (step + 1 < currentStep) HalalGreen else Color.Gray.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
fun FrontImageStep(
    frontImageUri: String?,
    onCapture: () -> Unit,
    onGallery: () -> Unit,
    onNext: () -> Unit,
    scanProgress: Float,
    cameraProvider: ProcessCameraProvider?,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onCameraReady: (ImageCapture) -> Unit,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Text("Langkah 1: Foto Bagian Depan", color = Color.White, fontWeight = FontWeight.Bold)
    Text("Digunakan untuk identifikasi nama produk dan brand", color = TextGray, fontSize = 12.sp)
    
    Spacer(modifier = Modifier.height(16.dp))
    
    CameraCaptureView(
        imageUri = frontImageUri,
        label = "Front package",
        scanProgress = scanProgress,
        cameraProvider = cameraProvider,
        lifecycleOwner = lifecycleOwner,
        onCameraReady = onCameraReady,
        hasCameraPermission = hasCameraPermission,
        onRequestPermission = onRequestPermission
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    ActionButtons(
        imageUri = frontImageUri,
        onCapture = onCapture,
        onGallery = onGallery,
        onNext = onNext
    )
}

@Composable
fun BackImageStep(
    backImageUri: String?,
    onCapture: () -> Unit,
    onGallery: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    scanProgress: Float,
    cameraProvider: ProcessCameraProvider?,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onCameraReady: (ImageCapture) -> Unit,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Text("Langkah 2: Foto Komposisi", color = Color.White, fontWeight = FontWeight.Bold)
    Text("Digunakan untuk mengekstrak daftar bahan baku", color = TextGray, fontSize = 12.sp)
    
    Spacer(modifier = Modifier.height(16.dp))

    CameraCaptureView(
        imageUri = backImageUri,
        label = "Back package (Ingredients)",
        scanProgress = scanProgress,
        cameraProvider = cameraProvider,
        lifecycleOwner = lifecycleOwner,
        onCameraReady = onCameraReady,
        hasCameraPermission = hasCameraPermission,
        onRequestPermission = onRequestPermission
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = BorderStroke(1.dp, Color.White)
        ) {
            Text("Kembali")
        }
        
        Box(modifier = Modifier.weight(2f)) {
            ActionButtons(
                imageUri = backImageUri,
                onCapture = onCapture,
                onGallery = onGallery,
                onNext = onNext
            )
        }
    }
}

@Composable
fun CameraCaptureView(
    imageUri: String?,
    label: String,
    scanProgress: Float,
    cameraProvider: ProcessCameraProvider?,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onCameraReady: (ImageCapture) -> Unit,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(2.dp, DarkBorder)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Clear overlay
                IconButton(
                    onClick = { /* This would need to be handled by a passed lambda to clear URI */ },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
                }
            } else if (!hasCameraPermission) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Izin kamera dibutuhkan untuk ambil foto", color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = onRequestPermission) {
                        Text("Berikan Izin")
                    }
                }
            } else {
                AndroidView(
                    factory = { ctx ->
                        val previewView = androidx.camera.view.PreviewView(ctx)
                        cameraProvider?.let { provider ->
                            val preview = Preview.Builder().build()
                            preview.setSurfaceProvider(previewView.surfaceProvider)
                            
                            val imageCapture = ImageCapture.Builder().build()
                            onCameraReady(imageCapture)

                            // Robust Camera Selector for Emulator/Device
                            val cameraSelector = try {
                                if (provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                }
                            } catch (e: Exception) {
                                // Fallback for emulator if lens facing check fails
                                Log.e("EnhancedOCR", "Camera check failed, defaulting to BACK", e)
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }

                            try {
                                provider.unbindAll()
                                provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                            } catch (e: Exception) {
                                Log.e("EnhancedOCR", "Primary camera bind failed, trying fallback", e)
                                try {
                                    // Extreme fallback: Try ANY camera
                                    provider.unbindAll()
                                    provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageCapture)
                                } catch (e2: Exception) {
                                    Log.e("EnhancedOCR", "Generic camera bind failed", e2)
                                }
                            }
                        }
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Scan line animation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, HalalGreen, Color.Transparent)
                            )
                        )
                        .offset(y = (scanProgress * 350).dp)
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    imageUri: String?,
    onCapture: () -> Unit,
    onGallery: () -> Unit,
    onNext: () -> Unit
) {
    if (imageUri == null) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onCapture,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HalalGreen)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = DarkBackground)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ambil Foto", color = DarkBackground, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onGallery,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Galeri")
            }
        }
    } else {
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667EEA))
        ) {
            Text("Lanjut ke Langkah Berikutnya", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

private fun captureImage(capture: ImageCapture?, executor: ExecutorService, context: android.content.Context, onCaptured: (String) -> Unit) {
    capture?.let {
        val file = File(context.cacheDir, "OCR_${System.currentTimeMillis()}.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        it.takePicture(options, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) { onCaptured(file.absolutePath) }
            override fun onError(e: ImageCaptureException) {
                Log.e("EnhancedOCR", "Capture failed", e)
            }
        })
    }
}
