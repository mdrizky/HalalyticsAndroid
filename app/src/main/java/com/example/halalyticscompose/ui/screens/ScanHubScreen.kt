package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.DarkBackground

@Composable
fun ScanHubScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF021A34))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Scan Center",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pilih mode scan sesuai kebutuhan analisis.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))

        ScanActionCard(
            title = "Barcode / QR",
            subtitle = "Scan produk halal umum",
            icon = Icons.Outlined.QrCodeScanner,
            onClick = { navController.navigate("scan") }
        )
        ScanActionCard(
            title = "OCR Produk",
            subtitle = "Ambil teks komposisi dari kemasan",
            icon = Icons.Outlined.TextSnippet,
            onClick = { navController.navigate("enhanced_ocr") }
        )
        ScanActionCard(
            title = "AI Meal Scan",
            subtitle = "Analisis makanan dari foto",
            icon = Icons.Outlined.Restaurant,
            onClick = { navController.navigate("food_scan") }
        )
        ScanActionCard(
            title = "BPOM Verify",
            subtitle = "Cek registrasi dan validitas BPOM",
            icon = Icons.Outlined.LocalPharmacy,
            onClick = { navController.navigate("bpom_scanner") }
        )
    }
}

@Composable
private fun ScanActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF102840), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFF0ABAB5).copy(alpha = 0.18f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF33E1DB))
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, color = Color.White.copy(alpha = 0.75f))
            }
        }
        Text(text = "Buka", color = Color(0xFF25E4DA), fontWeight = FontWeight.SemiBold)
    }
}
