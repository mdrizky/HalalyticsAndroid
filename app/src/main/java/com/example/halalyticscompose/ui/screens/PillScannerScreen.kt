package com.example.halalyticscompose.ui.screens

import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.HealthAiViewModel
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillScannerScreen(
    navController: NavController,
    viewModel: HealthAiViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var capturedImageFile by remember { mutableStateOf<File?>(null) }
    var selectedShape by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("") }
    
    val identifyResult by viewModel.pillIdentifyResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pill Identifier", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (capturedImageFile != null) capturedImageFile = null
                        else navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (capturedImageFile == null) {
                // Camera Preview
                AndroidView(
                    factory = { ctx ->
                        val previewView = androidx.camera.view.PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            imageCapture = ImageCapture.Builder().build()
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner, cameraSelector, preview, imageCapture
                                )
                            } catch (exc: Exception) {
                                // Handle error
                            }
                        }, androidx.core.content.ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Camera Overlay
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        "Posisikan pil di tengah kotak",
                        color = Color.White,
                        modifier = Modifier.background(Color.Black.copy(0.5f), RoundedCornerShape(8.dp)).padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    IconButton(
                        onClick = {
                            val file = File(context.cacheDir, "pill_${System.currentTimeMillis()}.jpg")
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                            imageCapture?.takePicture(
                                outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        capturedImageFile = file
                                    }
                                    override fun onError(exc: ImageCaptureException) {}
                                }
                            )
                        },
                        modifier = Modifier.size(80.dp).background(Color.White, CircleShape).padding(4.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Capture", modifier = Modifier.size(48.dp), tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else {
                // Analysis View
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.size(200.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        AsyncImage(
                            model = capturedImageFile,
                            contentDescription = "Pill Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Informasi Tambahan (Opsional)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = selectedShape,
                            onValueChange = { selectedShape = it },
                            label = { Text("Bentuk") },
                            placeholder = { Text("Bulat, Oval, dll") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = selectedColor,
                            onValueChange = { selectedColor = it },
                            label = { Text("Warna") },
                            placeholder = { Text("Putih, Merah, dll") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.identifyPill(capturedImageFile!!, selectedShape, selectedColor) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Identifikasi dengan AI", fontWeight = FontWeight.Bold)
                        }
                    }

                    error?.let {
                        Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                    }

                    Box(modifier = Modifier.padding(top = 24.dp)) {
                        identifyResult?.let { result ->
                            PillResultCard(result)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PillResultCard(data: com.example.halalyticscompose.data.model.PillIdentifyData) {
    Column {
        Text("Hasil Identifikasi:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))
        
        data.possibleDrugs.forEach { drug ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFF3B82F6).copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${(drug.confidence * 100).toInt()}%", fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(drug.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        drug.genericName?.let { Text(it, fontSize = 12.sp, color = Color.Gray) }
                    }
                }
            }
        }
    }
}
