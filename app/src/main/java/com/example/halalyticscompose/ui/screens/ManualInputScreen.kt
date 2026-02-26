package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import com.example.halalyticscompose.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInputScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var barcode by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top bar with clear button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            
            Text(
                text = "Input Manual",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            
            // Clear button
            IconButton(
                onClick = { 
                    barcode = ""
                    productName = ""
                    errorMessage = ""
                }
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Clear",
                    tint = TextWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 80.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon and title
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(HalalGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Keyboard,
                    contentDescription = "Manual Input",
                    tint = HalalGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Informasi Produk",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Masukkan barcode atau nama produk\nuntuk mendapatkan detail status halal",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Input card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(DarkCard),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Barcode Input
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { 
                            barcode = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nomor Barcode", color = TextGray) },
                        placeholder = { Text("Contoh: 8999999...", color = TextMuted) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = "Barcode",
                                tint = HalalGreen
                            )
                        },
                        trailingIcon = {
                            if (barcode.isNotEmpty()) {
                                IconButton(
                                    onClick = { 
                                        barcode = ""
                                        errorMessage = ""
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = TextGray
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HalalGreen,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground,
                            cursorColor = HalalGreen
                        ),
                        singleLine = true,
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Product Name Input (optional)
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { 
                            productName = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nama Produk (Opsional)", color = TextGray) },
                        placeholder = { Text("Nama produk jika diketahui...", color = TextMuted) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Product",
                                tint = HalalGreen
                            )
                        },
                        trailingIcon = {
                            if (productName.isNotEmpty()) {
                                IconButton(
                                    onClick = { 
                                        productName = ""
                                        errorMessage = ""
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = TextGray
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HalalGreen,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground,
                            cursorColor = HalalGreen
                        ),
                        singleLine = true,
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Error message
                    if (errorMessage.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFEF4444).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444))
                        ) {
                            Text(
                                text = errorMessage,
                                modifier = Modifier.padding(12.dp),
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    
                    // Search button - navigate directly to product_detail
                    Button(
                        onClick = {
                            if (barcode.isEmpty()) {
                                errorMessage = "Masukkan nomor barcode"
                                return@Button
                            }
                            
                            if (barcode.length < 8) {
                                errorMessage = "Barcode minimal 8 digit"
                                return@Button
                            }
                            
                            errorMessage = ""
                            // Navigate directly - ProductDetailScreen will handle loading
                            navController.navigate("product_detail/$barcode")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HalalGreen,
                            disabledContainerColor = HalalGreen.copy(alpha = 0.5f)
                        ),
                        enabled = barcode.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DarkBackground,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        Text(
                            text = "Cari Produk",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Quick Actions
            Text(
                text = "Aksi Cepat",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons grid
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recent Scans
                QuickActionCard(
                    title = "Riwayat Scan",
                    subtitle = "Lihat produk yang pernah di-scan",
                    icon = Icons.Default.History,
                    iconColor = HalalGreen,
                    onClick = { navController.navigate("history") }
                )
                
                // Search External
                QuickActionCard(
                    title = "Database Eksternal",
                    subtitle = "Cari di database halal publik",
                    icon = Icons.Default.Language,
                    iconColor = Color(0xFF3B82F6),
                    onClick = { navController.navigate("search_external") }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = DarkCard,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
            
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
