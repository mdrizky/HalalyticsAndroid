package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.MainViewModel

// ═══════════════════════════════════════════════════════════════════
// MANUAL INPUT SCREEN — PREMIUM SIMPLIFIED
// Form input manual dengan desain modern dan interaktif
// ═══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInputScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    var barcode by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    val primaryGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Input Manual", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground,
                    navigationIconContentColor = colorScheme.onBackground
                )
            )
        },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── Header Icon ────────────────────────────────
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E).copy(alpha = 0.15f))
                    .border(2.dp, Color(0xFF22C55E).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Keyboard,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Pencarian Produk",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Masukkan nomor barcode untuk mendapatkan detail status halal produk secara otomatis.",
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // ─── Input Form Card ────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Barcode Input
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { 
                            barcode = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nomor Barcode") },
                        placeholder = { Text("Contoh: 8999999...") },
                        leadingIcon = {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF22C55E))
                        },
                        trailingIcon = {
                            if (barcode.isNotEmpty()) {
                                IconButton(onClick = { 
                                    barcode = ""
                                    errorMessage = ""
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF22C55E),
                            unfocusedBorderColor = colorScheme.outlineVariant,
                            cursorColor = Color(0xFF22C55E)
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Product Name Input (optional)
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { 
                            productName = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nama Produk (Opsional)") },
                        placeholder = { Text("Ketik nama produk jika ada...") },
                        leadingIcon = {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFF0EA5E9))
                        },
                        trailingIcon = {
                            if (productName.isNotEmpty()) {
                                IconButton(onClick = { productName = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0EA5E9),
                            unfocusedBorderColor = colorScheme.outlineVariant,
                            cursorColor = Color(0xFF0EA5E9)
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Error Message
                    if (errorMessage.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Text(
                                text = errorMessage,
                                modifier = Modifier.padding(12.dp),
                                color = Color(0xFFDC2626),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // Search Button
                    Button(
                        onClick = {
                            if (barcode.isEmpty()) {
                                errorMessage = "Mohon masukkan nomor barcode terlebih dahulu"
                                return@Button
                            }
                            if (barcode.length < 8) {
                                errorMessage = "Barcode tidak valid (minimal 8 digit)"
                                return@Button
                            }
                            errorMessage = ""
                            navController.navigate("product_detail/$barcode")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = primaryGradient,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Cari Produk",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // ─── Quick Actions ──────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Aksi Cepat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                QuickActionRow(
                    title = "Riwayat Scan",
                    subtitle = "Akses kembali produk yang pernah dicari",
                    icon = Icons.Default.History,
                    iconTint = Color(0xFF22C55E),
                    onClick = { navController.navigate("history") }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                QuickActionRow(
                    title = "Database Eksternal",
                    subtitle = "Pencarian meluas ke database BPOM & LPPOM",
                    icon = Icons.Default.Language,
                    iconTint = Color(0xFF3B82F6),
                    onClick = { navController.navigate("search_external") }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun QuickActionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
