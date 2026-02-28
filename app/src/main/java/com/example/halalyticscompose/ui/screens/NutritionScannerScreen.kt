package com.example.halalyticscompose.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.HalalGreen
import com.example.halalyticscompose.ui.theme.SecondaryColor
import com.example.halalyticscompose.ui.viewmodel.NutritionScannerViewModel
import java.io.ByteArrayOutputStream
import android.util.Base64
import java.io.IOException
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScannerScreen(
    navController: NavController,
    viewModel: NutritionScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val result by viewModel.result.collectAsState()
    val error by viewModel.error.collectAsState()

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageBitmap = uri?.let { loadBitmapFromUri(context, it) }
        if (selectedImageBitmap != null) {
            viewModel.clearResult()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            selectedImageBitmap = it
            viewModel.clearResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nutrition & Halal Scan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text(
                "Foto komposisi (ingredients) pada kemasan produk untuk mengecek status halal dan skor nutrisinya.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Image Preview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                if (selectedImageBitmap != null) {
                    Image(
                        bitmap = selectedImageBitmap!!.asImageBitmap(),
                        contentDescription = "Selected Nutrition Label",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada foto komposisi", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { cameraLauncher.launch(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
                ) {
                Text("Kamera")
                }
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
                ) {
                Text("Galeri")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedImageBitmap != null && result == null && !isLoading) {
                Button(
                    onClick = { 
                        val base64 = bitmapToBase64(selectedImageBitmap!!)
                        viewModel.scanNutrition(base64)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HalalGreen)
                ) {
                    Text("Baca Komposisi (AI)", fontWeight = FontWeight.Bold)
                }
            }

            if (isLoading) {
                CircularProgressIndicator(color = HalalGreen, modifier = Modifier.padding(16.dp))
                Text("AI sedang memindai komposisi...", color = MaterialTheme.colorScheme.onSurface)
            }

            if (error != null) {
                Text(text = error!!, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
            }

            // Results UI
            if (result != null) {
                Spacer(modifier = Modifier.height(24.dp))
                
                val isHalal = result!!.halalStatus.equals("Halal", ignoreCase = true)
                val isHaram = result!!.halalStatus.equals("Haram", ignoreCase = true)
                
                val statusColor = when {
                    isHalal -> HalalGreen
                    isHaram -> Color.Red
                    else -> Color.Yellow
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "STATUS",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            result!!.halalStatus.uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        // Parse JSON Array specifically for warnings
                        val aiJson = try { JSONObject(result!!.aiAnalysis) } catch (e: Exception) { null }
                        val concernsArray = aiJson?.optJSONArray("ingredients_concern")
                        
                        if (concernsArray != null && concernsArray.length() > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Warning", tint = Color.Yellow)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Perhatian pada komposisi:", color = Color.Yellow, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            for (i in 0 until concernsArray.length()) {
                                Text("- ${concernsArray.getString(i)}", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        if (aiJson != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Health Score", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${result!!.healthScore}/100", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Kalori", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${aiJson.optInt("kalori", 0)} kcal", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Gula", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${aiJson.optInt("gula", 0)} g", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                                }
                            }
                        } else {
                            Text("Raw Data: ${result!!.aiAnalysis}", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                
                if (isHaram) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Peringatan Darurat: Produk ini terindikasi HARAM. Notifikasi peringatan telah dikirim ke Admin Pusat/BPJPH via Real-time socket!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun loadBitmapFromUri(context: android.content.Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
    } catch (_: IOException) {
        null
    }
}
