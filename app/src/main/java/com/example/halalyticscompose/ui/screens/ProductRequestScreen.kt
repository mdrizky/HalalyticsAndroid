package com.example.halalyticscompose.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.halalyticscompose.ui.viewmodel.ProductRequestViewModel
import com.example.halalyticscompose.utils.ImageUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRequestScreen(
    navController: NavController,
    barcode: String,
    viewModel: ProductRequestViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var productName by remember { mutableStateOf("") }
    var ocrText by remember { mutableStateOf("") }

    var frontImageUri by remember { mutableStateOf<Uri?>(null) }
    var backImageUri by remember { mutableStateOf<Uri?>(null) }

    var pendingFrontCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingBackCameraUri by remember { mutableStateOf<Uri?>(null) }

    val uploadStatus by viewModel.uploadStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Izin kamera dibutuhkan untuk ambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    val takeFrontPicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) frontImageUri = pendingFrontCameraUri
    }
    val takeBackPicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) backImageUri = pendingBackCameraUri
    }

    val galleryFrontLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        frontImageUri = uri
    }
    val galleryBackLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        backImageUri = uri
    }

    LaunchedEffect(uploadStatus) {
        uploadStatus?.let { result ->
            if (result.isSuccess) {
                Toast.makeText(context, result.getOrNull(), Toast.LENGTH_LONG).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, result.exceptionOrNull()?.message ?: "Gagal kirim", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kontribusi Produk", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00B894),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Produk tidak ditemukan. Kirim foto depan + belakang agar admin bisa verifikasi.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4B5563)
            )
            if (barcode.isNotBlank()) {
                Text("Barcode: $barcode", color = Color(0xFF0EA5E9), fontWeight = FontWeight.SemiBold)
            }

            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Nama Produk") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            ImagePickerSection(
                title = "Foto Depan Produk",
                imageUri = frontImageUri,
                onCamera = {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    val uri = createTempImageUri(context, "front")
                    pendingFrontCameraUri = uri
                    takeFrontPicture.launch(uri)
                },
                onGallery = { galleryFrontLauncher.launch("image/*") }
            )

            ImagePickerSection(
                title = "Foto Belakang (Komposisi/Label)",
                imageUri = backImageUri,
                onCamera = {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    val uri = createTempImageUri(context, "back")
                    pendingBackCameraUri = uri
                    takeBackPicture.launch(uri)
                },
                onGallery = { galleryBackLauncher.launch("image/*") }
            )

            OutlinedTextField(
                value = ocrText,
                onValueChange = { ocrText = it },
                label = { Text("Keluhan / Keterangan Produk") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Button(
                onClick = {
                    if (frontImageUri == null || backImageUri == null || productName.isBlank()) {
                        Toast.makeText(context, "Lengkapi nama + foto depan + foto belakang", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val frontFile = ImageUtils.reduceFileImage(ImageUtils.uriToFile(frontImageUri!!, context))
                    val backFile = ImageUtils.reduceFileImage(ImageUtils.uriToFile(backImageUri!!, context))

                    val reqFront = frontFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val reqBack = backFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                    val bodyFront = MultipartBody.Part.createFormData("image_front", frontFile.name, reqFront)
                    val bodyBack = MultipartBody.Part.createFormData("image_back", backFile.name, reqBack)

                    viewModel.uploadProductRequest(
                        imageFront = bodyFront,
                        imageBack = bodyBack,
                        barcode = barcode.toRequestBody("text/plain".toMediaType()),
                        productName = productName.toRequestBody("text/plain".toMediaType()),
                        ocrText = ocrText.toRequestBody("text/plain".toMediaType())
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B894))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Kirim ke Admin Verifikasi", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ImagePickerSection(
    title: String,
    imageUri: Uri?,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(14.dp))
                .background(Color(0xFFF9FAFB)),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(42.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Belum ada foto", color = Color(0xFF6B7280))
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onCamera, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.size(6.dp))
                Text("Kamera")
            }
            Button(onClick = onGallery, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937))) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.size(6.dp))
                Text("Galeri")
            }
        }
    }
}

private fun createTempImageUri(context: android.content.Context, prefix: String): Uri {
    val imageFile = File.createTempFile("${prefix}_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
}
