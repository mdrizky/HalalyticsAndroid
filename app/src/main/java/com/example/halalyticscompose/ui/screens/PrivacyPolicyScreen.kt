package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kebijakan Privasi", color = textColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = HalalGreen
                )
            }

            Text(
                text = "Privasi Anda Adalah Prioritas Kami",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            PrivacySection(
                title = "1. Data yang Kami Kumpulkan",
                content = "Kami mengumpulkan data yang Anda berikan secara sukarela (Username, Email, No. HP) dan data aktivitas scan produk untuk memberikan rekomendasi halal yang personal."
            )

            PrivacySection(
                title = "2. Penggunaan Data",
                content = "Data Anda digunakan untuk memproses history scan, sinkronisasi favorit antarkomponen perangkat, dan peningkatan database produk melalui filter kecerdasan buatan."
            )

            PrivacySection(
                title = "3. Keamanan Data",
                content = "Semua transmisi data dilakukan melalui protokol aman (HTTPS/SSL). Kami tidak menjual data pribadi Anda kepada pihak ketiga mana pun."
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Terakhir Diperbarui: 17 Januari 2026",
                fontSize = 12.sp,
                color = androidx.compose.ui.graphics.Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun PrivacySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HalalGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}
