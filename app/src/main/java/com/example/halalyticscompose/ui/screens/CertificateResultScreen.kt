package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.Data.Model.CertificateInfo
import com.example.halalyticscompose.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateResultScreen(
    navController: NavController,
    info: CertificateInfo
) {
    val isValid = info.status.lowercase() == "valid"
    val accentColor = if (isValid) HalalGreen else Color(0xFFEF4444)
    val color = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil Verifikasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = color.background,
                    titleContentColor = color.onBackground,
                    navigationIconContentColor = color.onBackground
                )
            )
        },
        containerColor = color.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Status Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isValid) Icons.Default.Verified else Icons.Default.Error,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(60.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (isValid) "Sertifikat Valid" else "Sertifikat Tidak Valid",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color.onBackground
            )
            
            Text(
                text = "Diverifikasi oleh ${info.issuer}",
                fontSize = 14.sp,
                color = color.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = color.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, color.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    DetailItem(
                        icon = Icons.Default.Info,
                        label = "Nomor Sertifikat",
                        value = info.certificateNumber
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = color.outlineVariant)
                    
                    DetailItem(
                        icon = Icons.Default.Business,
                        label = "Produk / Pabrik",
                        value = info.productName ?: info.manufacturer ?: "N/A"
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = color.outlineVariant)
                    
                    DetailItem(
                        icon = Icons.Default.CalendarMonth,
                        label = "Berlaku Hingga",
                        value = info.expiryDate ?: "Tidak tersedia"
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text("Selesai", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    val color = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = HalalGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = color.onSurfaceVariant)
            Text(text = value, fontSize = 15.sp, color = color.onSurface, fontWeight = FontWeight.Medium)
        }
    }
}
