package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleProductDetailScreen(
    barcode: String,
    navController: NavController
) {
    // Mock product data
    val productData = when (barcode) {
        "8999999101111" -> ProductData(
            name = "Indomie Mi Goreng",
            brand = "Indofood",
            category = "Instant Noodles",
            halalStatus = "Halal",
            certificateNumber = "123456789",
            certificationBody = "MUI",
            validUntil = "2025-12-31",
            lastChecked = "2024-01-20",
            description = "Indomie Mi Goreng adalah produk mi instan asli dari Indonesia yang telah mendapatkan sertifikat halal dari MUI."
        )
        "8998877123456" -> ProductData(
            name = "Coca Cola",
            brand = "The Coca-Cola Company",
            category = "Beverages",
            halalStatus = "Halal",
            certificateNumber = "987654321",
            certificationBody = "MUI",
            validUntil = "2024-06-30",
            lastChecked = "2024-01-15",
            description = "Coca Cola adalah minuman berkarbonat yang diproduksi secara global dan telah mendapatkan sertifikat halal."
        )
        else -> ProductData(
            name = "Unknown Product",
            brand = "Unknown",
            category = "Unknown",
            halalStatus = "Unknown",
            certificateNumber = null,
            certificationBody = null,
            validUntil = null,
            lastChecked = "2024-01-20",
            description = "Produk ini sedang dalam proses verifikasi status halal."
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFE2E8F0)
                    )
                )
            )
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigateUp() }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1A202C),
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Text(
                text = "Product Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C)
            )
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Product Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📦",
                        fontSize = 48.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Product Info
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = productData.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A202C)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = productData.brand,
                        fontSize = 16.sp,
                        color = Color(0xFF64748B)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = productData.category,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Barcode: $barcode",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = productData.description,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Halal Status Card
            HalalStatusCard(
                halalStatus = productData.halalStatus,
                certificateNumber = productData.certificateNumber,
                certificationBody = productData.certificationBody,
                validUntil = productData.validUntil,
                lastChecked = productData.lastChecked
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // Add to scan history
                        navController.navigateUp()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22C55E)
                    )
                ) {
                    Text(
                        text = "Add to History",
                        color = Color.White
                    )
                }
                
                Button(
                    onClick = {
                        // Share product
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    )
                ) {
                    Text(
                        text = "Share",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.width(120.dp)
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun HalalStatusCard(
    halalStatus: String,
    certificateNumber: String?,
    certificationBody: String?,
    validUntil: String?,
    lastChecked: String
) {
    val statusColor = when (halalStatus) {
        "Halal" -> Color(0xFF22C55E)
        "Non Halal" -> Color(0xFFEF4444)
        else -> Color(0xFFF59E0B)
    }
    
    val statusIcon = when (halalStatus) {
        "Halal" -> Icons.Default.CheckCircle
        "Non Halal" -> Icons.Default.Cancel
        else -> Icons.AutoMirrored.Filled.Help
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Halal Status",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = halalStatus,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                
                Icon(
                    statusIcon,
                    contentDescription = halalStatus,
                    tint = statusColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Certificate Info (only if Halal)
            if (halalStatus == "Halal" && certificateNumber != null) {
                Column {
                    InfoRow("Certificate Number", certificateNumber ?: "Unknown")
                    InfoRow("Certification Body", certificationBody ?: "Unknown")
                    InfoRow("Valid Until", validUntil ?: "Unknown")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Last Checked
            InfoRow("Last Checked", lastChecked)
        }
    }
}

data class ProductData(
    val name: String,
    val brand: String,
    val category: String,
    val halalStatus: String,
    val certificateNumber: String?,
    val certificationBody: String?,
    val validUntil: String?,
    val lastChecked: String,
    val description: String
)
