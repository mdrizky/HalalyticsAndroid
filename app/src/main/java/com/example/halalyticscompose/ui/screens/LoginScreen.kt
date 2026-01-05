package com.example.halalyticscompose.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.halalyticscompose.Data.API.ApiConfig
import com.example.halalyticscompose.Data.Model.LoginModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val sharedPref = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF00BCD4), Color(0xFF5C6BC0))
                )
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Logo/Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF00BCD4), Color(0xFF5C6BC0))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Text(
                    text = "Selamat Datang",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Text(
                    text = "Login untuk melanjutkan",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00BCD4))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00BCD4),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00BCD4))
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00BCD4),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Login button
                Button(
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Username dan password harus diisi"
                            return@Button
                        }
                        
                        isLoading = true
                        errorMessage = ""
                        
                        scope.launch(Dispatchers.IO) {
                            try {
                                val response = ApiConfig.apiService.login(username, password)
                                withContext(Dispatchers.Main) {
                                    if (response.response_code == 200) {
                                        val editor = sharedPref.edit()
                                        editor.putString("access_token", response.access_token)
                                        val fullName = (response.content.full_name as? String)?.takeIf { it.isNotBlank() }
                                        editor.putString("user_name", fullName ?: response.content.username)
                                        editor.putString("email", response.content.email)
                                        editor.putInt("id_user", response.content.id_user)
                                        editor.apply()
                                        
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        errorMessage = response.message
                                    }
                                    isLoading = false
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    errorMessage = "Gagal login: ${e.message}"
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00BCD4)
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        text = "Lupa Password?",
                        color = Color(0xFF00BCD4)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Belum punya akun?",
                        color = Color.Gray
                    )
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            text = "Daftar",
                            color = Color(0xFF00BCD4),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Warna sesuai gambar
val CyanPrimary = Color(0xFF00BCD4)
val BluePrimary = Color(0xFF5C6BC0)
val GreenHealthy = Color(0xFF4CAF50)
val RedUnhealthy = Color(0xFFE91E63)
val YellowWarning = Color(0xFFFFB300)
val GrayLight = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    scans: List<Scan>,
    onScanClick: (Scan) -> Unit,
    onBackClick: () -> Unit,
    onFilterChange: (FilterType) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredScans = remember(scans, selectedFilter, searchQuery) {
        scans.filter { scan ->
            val matchesSearch = searchQuery.isEmpty() ||
                    scan.productName.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                FilterType.ALL -> true
                FilterType.HALAL -> scan.isHalal == true
                FilterType.NOT_HALAL -> scan.isHalal == false
                FilterType.SYUBHAT -> scan.isHalal == null
            }
            matchesSearch && matchesFilter
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(CyanPrimary, BluePrimary)
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { /* Statistics */ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Statistics",
                        tint = Color.White
                    )
                }
            }

            // Header Info
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Riwayat Scan",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${scans.size} produk terscan",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Cari produk atau brand...", color = Color.Gray)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = CyanPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == FilterType.ALL,
                    onClick = {
                        selectedFilter = FilterType.ALL
                        onFilterChange(FilterType.ALL)
                    },
                    label = "Semua",
                    count = scans.size,
                    color = BluePrimary
                )

                FilterChip(
                    selected = selectedFilter == FilterType.HALAL,
                    onClick = {
                        selectedFilter = FilterType.HALAL
                        onFilterChange(FilterType.HALAL)
                    },
                    label = "Halal",
                    count = scans.count { it.isHalal == true },
                    color = GreenHealthy
                )

                FilterChip(
                    selected = selectedFilter == FilterType.SYUBHAT,
                    onClick = {
                        selectedFilter = FilterType.SYUBHAT
                        onFilterChange(FilterType.SYUBHAT)
                    },
                    label = "Syubhat",
                    count = scans.count { it.isHalal == null },
                    color = YellowWarning
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scan List
            if (filteredScans.isEmpty()) {
                EmptyState(searchQuery.isNotEmpty())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredScans) { scan ->
                        ScanHistoryItem(
                            scan = scan,
                            onClick = { onScanClick(scan) }
                        )
                    }

                    // Bottom spacing for nav bar
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    count: Int,
    color: Color
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) color else Color.White,
        animationSpec = tween(300)
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFF424242),
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier.Companion
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 14.sp,
                color = textColor
            )

            Box(
                modifier = Modifier
                    .background(
                        color = if (selected) Color.White.copy(alpha = 0.3f)
                        else color.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun ScanHistoryItem(
    scan: Scan,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400)
    )

    val borderColor = when {
        scan.isHalal == true -> GreenHealthy
        scan.isHalal == false -> RedUnhealthy
        else -> YellowWarning
    }

    val iconColor = borderColor
    val icon = when {
        scan.isHalal == true -> Icons.Default.CheckCircle
        scan.isHalal == false -> Icons.Default.HighlightOff // Mengganti Cancel dengan HighlightOff
        else -> Icons.Default.Info
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .animatedScale()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(70.dp)
                    .background(borderColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = scan.productName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121),
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis
                )

                Text(
                    text = scan.barcode ?: "Unknown Brand",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scan.createdAt?.substringBefore(" ")?.let {
                            // Format: "8 Okt 2025"
                            it.split("-").let { parts ->
                                "${parts[2]} ${getMonthName(parts[1].toInt())} ${parts[0]}"
                            }
                        } ?: "",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = scan.createdAt?.substringAfter(" ")?.substring(0, 5) ?: "",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right Side Info
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )

                scan.healthScore?.let { score ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$score kal",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                scan.ingredients?.split(",")?.size?.let { count ->
                    Text(
                        text = "${count}g gula",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(isSearching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Default.Search else Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isSearching) "Produk tidak ditemukan" else "Belum ada riwayat scan",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )

        Text(
            text = if (isSearching)
                "Coba kata kunci lain"
            else
                "Mulai scan produk untuk melihat riwayat",
            fontSize = 14.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun Modifier.animatedScale(): Modifier {
    val scale = remember { Animatable(0.95f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    return this.scale(scale.value)
}

fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "Mei"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Agu"
        9 -> "Sep"
        10 -> "Okt"
        11 -> "Nov"
        12 -> "Des"
        else -> ""
    }
}

enum class FilterType {
    ALL, HALAL, NOT_HALAL, SYUBHAT
}

// Data class Scan (pastikan sudah ada di Models)
data class Scan(
    val idScan: Int,
    val userId: Int,
    val productName: String,
    val barcode: String?,
    val ingredients: String?,
    val isHalal: Boolean?,
    val isHealthy: Boolean?,
    val halalCertification: String?,
    val healthScore: Int?,
    val warnings: String?,
    val scanResult: String?,
    val imagePath: String?,
    val createdAt: String?,
    val updatedAt: String?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginClick: (String, String) -> Unit = { _, _ -> }, // Dikosongkan karena logika pindah ke doLogin
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Background & Animated Icons
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF10B981), Color(0xFF4A90E2))
                    )
                )
        ) {
            // Floating icons for decoration
            AnimatedFloatingIcon(icon = Icons.Default.CheckCircle, color = Color.White.copy(alpha = 0.2f), offsetY = 100f, initialDelay = 200, modifier = Modifier.align(Alignment.TopStart).padding(start = 50.dp))
            AnimatedFloatingIcon(icon = Icons.Default.Favorite, color = Color.White.copy(alpha = 0.2f), offsetY = 150f, initialDelay = 500, modifier = Modifier.align(Alignment.TopEnd).padding(end = 60.dp))
            AnimatedFloatingIcon(icon = Icons.Default.CameraAlt, color = Color.White.copy(alpha = 0.2f), offsetY = 250f, initialDelay = 800, modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp))
        }

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Halalytics",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Pindai, Verifikasi, dan Nikmati Hidup Sehat",
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Selamat Datang! 👋",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = "Masuk untuk melanjutkan hidup sehatmu",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email atau Username") },
                            placeholder = { Text("nama@email.com") },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF10B981)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF6F7FB),
                                unfocusedContainerColor = Color(0xFFF6F7FB),
                                disabledContainerColor = Color(0xFFF6F7FB),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF10B981)) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF6F7FB),
                                unfocusedContainerColor = Color(0xFFF6F7FB),
                                disabledContainerColor = Color(0xFFF6F7FB),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Lupa Password? →",
                            color = Color(0xFF3B82F6),
                            modifier = Modifier.align(Alignment.End).clickable(onClick = onForgotPasswordClick)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tombol Login memanggil fungsi doLogin
                        GradientButton(
                            enabled = !isLoading,
                            onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    isLoading = true
                                    // Memanggil fungsi login dengan parameter yang diperlukan
                                    doLogin(context, navController, email, password) { isLoading = false }
                                } else {
                                    Toast.makeText(context, "Isi semua data dulu", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.Login, null, tint = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Masuk", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Divider(modifier = Modifier.weight(1f))
                            Text("  Atau masuk dengan  ", color = Color(0xFF9CA3AF))
                            Divider(modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SocialButton(text = "Google", modifier = Modifier.weight(1f))
                            SocialButton(text = "Apple", modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row {
                            Text("Belum punya akun? ", color = Color(0xFF6B7280))
                            Text("Daftar Sekarang →", color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onRegisterClick))
                        }
                    }
                }
            }
        }
        Text(
            text = "© 2025 Halalytics. Hidup Sehat & Berkah",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        )
    }
}

/**
 * Fungsi ini menangani seluruh proses login.
 * 1. Menghubungi API Laravel.
 * 2. Jika berhasil, menyimpan data penting (terutama token) ke SharedPreferences.
 * 3. Pindah ke halaman utama (home).
 */
private fun doLogin(
    context: Context,
    navController: NavController,
    username: String,
    password: String,
    onComplete: (() -> Unit)?
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val loginResponse: LoginModel = ApiConfig.apiService.login(username, password)

            if (loginResponse.response_code == 200) {
                // ✅ LANGKAH 1: Ambil data penting dari response Laravel
                val user = loginResponse.content
                val accessToken = loginResponse.access_token

                // ✅ LANGKAH 2: Simpan data ke SharedPreferences
                // Ini adalah kunci utama agar aplikasi "mengingat" siapa yang login.
                val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val displayName = (user.full_name as? String)?.takeIf { it.isNotBlank() } ?: user.username
                val phoneStr = user.phone?.toString()
                with(sharedPref.edit()) {
                    putString("access_token", accessToken)
                    putInt("user_id", user.id_user)
                    putString("user_name", displayName)
                    putString("email", user.email)
                    putString("role", user.role)
                    putString("phone", phoneStr)
                    putString("created_at", user.created_at)
                    apply()
                }

                // ✅ LANGKAH 3: Pindah ke halaman utama setelah login berhasil
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // Hapus halaman login dari riwayat
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Login gagal: ${loginResponse.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (t: Throwable) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal koneksi ke server: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        } finally {
            // Memberi tahu UI bahwa proses loading sudah selesai
            withContext(Dispatchers.Main) { onComplete?.invoke() }
        }
    }
}

@Composable
fun AnimatedFloatingIcon(
    icon: ImageVector,
    color: Color,
    offsetY: Float,
    initialDelay: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(initialDelay.toLong())
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Surface(
        modifier = modifier.offset(y = offsetY.dp).scale(scale).size(48.dp),
        shape = CircleShape,
        color = color,
        shadowElevation = 8.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun GradientButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
        modifier = modifier.background(brush = Brush.horizontalGradient(listOf(Color(0xFF14C7A1), Color(0xFF4A67FF))), shape = shape),
        content = content
    )
}

@Composable
fun SocialButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
        Text(text, color = Color(0xFF111827), fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileStatusScreen(
    user: LoginModel.LoginContent,
    goal: String?,
    dietPreference: String?,
    activityLevel: String?,
    address: String?,
    language: String?,
    bmi: Float?,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status & Pencapaian") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Halaman ini masih dalam pengembangan.", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Data yang berhasil diterima:", fontSize = 14.sp)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Info Pengguna", style = MaterialTheme.typography.titleMedium)
                        Text("Nama: ${(user.full_name as? String)?.takeIf { it.isNotBlank() } ?: user.username}")
                        Text("Email: ${user.email}")
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Statistik", style = MaterialTheme.typography.titleMedium)
                        Text("Total Scan: ${user.total_scan ?: 0}")
                        Text("Produk Halal: ${user.halal_count ?: 0}")
                        Text("Produk Syubhat: ${user.syubhat_count ?: 0}")
                        Text("Streak: ${user.streak ?: 0} hari")
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Info Kesehatan", style = MaterialTheme.typography.titleMedium)
                        Text("BMI: ${bmi?.toString() ?: "Belum diatur"}")
                        Text("Tujuan: ${goal ?: "Belum diatur"}")
                        Text("Preferensi Diet: ${dietPreference ?: "Belum diatur"}")
                        Text("Level Aktivitas: ${activityLevel ?: "Belum diatur"}")
                    }
                }
            }
        }
    }
}