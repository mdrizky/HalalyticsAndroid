package com.example.halalyticscompose.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.HealthAiViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabAnalysisScreen(
    navController: NavController,
    viewModel: HealthAiViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var testDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    val color = MaterialTheme.colorScheme
    
    val analysisResult by viewModel.labAnalysisResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Lab Result Analysis", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = color.surface,
                    titleContentColor = color.onSurface,
                    navigationIconContentColor = color.onSurface
                )
            )
        },
        containerColor = color.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Pahami Hasil Tes Lab Anda",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                "Unggah foto hasil lab (darah, urin, dll) untuk mendapatkan ringkasan yang mudah dipahami.",
                color = color.onSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { galleryLauncher.launch("image/*") },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = color.surface),
                border = BorderStroke(1.dp, color.outlineVariant.copy(alpha = 0.5f))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Lab Result",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.UploadFile, 
                                contentDescription = null, 
                                modifier = Modifier.size(48.dp), 
                                tint = color.primary
                            )
                            Text("Klik untuk Unggah Foto", color = color.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    selectedImageUri?.let { uri ->
                        val tempFile = uriToTempFile(context, uri)
                        if (tempFile != null) {
                            viewModel.analyzeLab(imageFile = tempFile, testDate = testDate)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedImageUri != null && !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = color.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = color.onPrimary)
                } else {
                    Icon(Icons.Default.Analytics, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Mulai Analisis AI", fontWeight = FontWeight.Bold)
                }
            }

            error?.let {
                Text(it, color = color.error, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = analysisResult != null) {
                analysisResult?.let { result ->
                    LabResultView(result)
                }
            }
        }
    }
}

private fun uriToTempFile(context: android.content.Context, uri: Uri): File? {
    return runCatching {
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalStateException("Unable to open URI stream")
        val tempFile = File.createTempFile("lab_", ".jpg", context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.use { input -> input.copyTo(output) }
        }
        tempFile
    }.getOrNull()
}

@Composable
fun LabResultView(data: com.example.halalyticscompose.Data.Model.LabAnalysisData) {
    val color = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Interpretasi Hasil:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color.onBackground)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = color.primaryContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ringkasan Umum", fontWeight = FontWeight.Bold, color = color.onSurface)
                Text(data.overallAssessment, fontSize = 14.sp, color = color.onSurface, modifier = Modifier.padding(top = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        data.detectedTests.forEach { test ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(8.dp).clip(CircleShape).background(
                        if (test.status.lowercase() == "normal") color.primary else color.error
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(test.testName, fontWeight = FontWeight.SemiBold, color = color.onSurface)
                    Text("${test.value} ${test.unit}", fontSize = 12.sp, color = color.onSurfaceVariant)
                }
                Text(
                    test.status.uppercase(),
                    color = if (test.status.lowercase() == "normal") color.primary else color.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Rekomendasi Gaya Hidup:", fontWeight = FontWeight.Bold, color = color.onBackground)
        data.lifestyleRecommendations.forEach { rec ->
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = color.primary, modifier = Modifier.size(16.dp))
                Text(rec, fontSize = 13.sp, color = color.onSurfaceVariant)
            }
        }
    }
}
