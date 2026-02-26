package com.example.halalyticscompose.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.Data.Model.StreetFood
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.ui.viewmodel.FoodScanViewModel
import com.example.halalyticscompose.utils.SessionManager
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScanScreen(
    navController: NavController,
    viewModel: FoodScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val scope = rememberCoroutineScope()
    
    // Set auth token
    LaunchedEffect(Unit) {
        viewModel.setAuthToken(sessionManager.getAuthToken() ?: "")
        viewModel.loadPopularFoods()
    }
    
    var searchQuery by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val popularFoods by viewModel.popularFoods.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Camera Logic
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Helper to create temp file
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.cacheDir
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    // Helper to get File from URI
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = createImageFile(context)
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Handlers
    fun handleImage(uri: Uri?) {
        if (uri != null) {
            val file = getFileFromUri(context, uri)
            if (file != null) {
                // Clear previous search
                searchQuery = ""
                viewModel.clearSearch()
                
                // Call ViewModel
                viewModel.recognizeImage(file, sessionManager.getAuthToken() ?: "") { success ->
                    if (success) {
                        Toast.makeText(context, "Analysis Complete", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Analysis Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Error processing image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            handleImage(tempImageUri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        handleImage(uri)
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = createImageFile(context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            tempImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Scanner", fontWeight = FontWeight.Bold, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
             Column(horizontalAlignment = Alignment.End) {
                 // Gallery FAB
                 SmallFloatingActionButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    containerColor =  DarkCardLight,
                    contentColor = TextWhite,
                    modifier = Modifier.padding(bottom = 16.dp)
                 ) {
                     Icon(Icons.Default.PhotoLibrary, "Gallery")
                 }
                 
                 // Camera FAB
                 ExtendedFloatingActionButton(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            val file = createImageFile(context)
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            tempImageUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    containerColor = HalalGreen,
                    contentColor = DarkBackground,
                    icon = { Icon(Icons.Default.CameraAlt, "Scan") },
                    text = { Text("Scan Food") }
                )
             }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.searchFood(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari makanan (nasi goreng, dll)...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Outlined.Search, "Search", tint = TextGray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = ""; viewModel.clearSearch() }) {
                            Icon(Icons.Filled.Clear, "Clear", tint = TextGray)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HalalGreen,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    cursorColor = HalalGreen,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )
            
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    color = HalalGreen
                )
            }
            
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = error, color = Color(0xFFEF9A9A), modifier = Modifier.padding(16.dp))
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (searchResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "Results (${searchResults.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(searchResults) { food ->
                        FoodItemCard(food = food, viewModel = viewModel, onClick = { navController.navigate("food_analysis/${food.id}") })
                    }
                }
                
                if (searchQuery.isEmpty() && popularFoods.isNotEmpty()) {
                    item {
                        Text(
                            text = "🔥 Makanan Populer",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(popularFoods) { food ->
                        FoodItemCard(food = food, viewModel = viewModel, onClick = { navController.navigate("food_analysis/${food.id}") })
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    food: StreetFood,
    viewModel: FoodScanViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCardLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = getFoodEmoji(food.category), fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = food.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HalalGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)) {
                        Text(text = food.category, fontSize = 11.sp, color = HalalGreen)
                    }
                }
                 // Health bar
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { food.healthScore / 100f },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = Color(viewModel.getHealthScoreColor(food.healthScore)),
                    trackColor = DarkCardLight
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(viewModel.getHalalStatusColor(food.halalStatus)).copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = getHalalStatusShort(food.halalStatus), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(viewModel.getHalalStatusColor(food.halalStatus)))
            }
        }
    }
}

fun getFoodEmoji(category: String): String {
    return when (category.lowercase()) {
        "nasi" -> "🍚"
        "mie" -> "🍜"
        "berkuah" -> "🍲"
        "gorengan" -> "🍳"
        "sayuran" -> "🥗"
        "kue" -> "🍰"
        else -> "🍽️"
    }
}

fun getHalalStatusShort(status: String): String {
    return when (status) {
        "halal_umum" -> "HALAL"
        "syubhat" -> "SYUBHAT"
        "haram" -> "HARAM"
        "tergantung_bahan" -> "CEK!"
        else -> "?"
    }
}


