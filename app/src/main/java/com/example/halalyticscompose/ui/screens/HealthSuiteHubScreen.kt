package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthSuiteHubScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Health Suite", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        "Halo, Pendamping Kesehatan Anda!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Gunakan kekuatan AI untuk menjaga kesehatan dan kehalalan konsumsi obat Anda.",
                        color = Color.White.copy(0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Pilih Layanan AI",
                modifier = Modifier.padding(horizontal = 24.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HealthFeatureCard(
                    title = "Drug Interaction Checker",
                    description = "Cek efek samping kombinasi obat.",
                    icon = Icons.Default.Compare,
                    color = Color(0xFF3B82F6),
                    onClick = { navController.navigate("drug_interaction") }
                )
                HealthFeatureCard(
                    title = "Pill Identification",
                    description = "Identifikasi obat hanya dengan foto.",
                    icon = Icons.Default.CameraAlt,
                    color = Color(0xFF10B981),
                    onClick = { navController.navigate("pill_scanner") }
                )
                HealthFeatureCard(
                    title = "Smart Medication Reminder",
                    description = "Jadwal pintar dengan pesan suara AI.",
                    icon = Icons.Default.Alarm,
                    color = Color(0xFFF59E0B),
                    onClick = { navController.navigate("medication_reminder_advanced") }
                )
                HealthFeatureCard(
                    title = "Lab Result Analysis",
                    description = "Pahami hasil cek lab dengan mudah.",
                    icon = Icons.Default.Analytics,
                    color = Color(0xFF8B5CF6),
                    onClick = { navController.navigate("lab_analysis") }
                )
                HealthFeatureCard(
                    title = "Health Journey",
                    description = "Pantau berat badan & tensi secara rutin.",
                    icon = Icons.Default.Timeline,
                    color = Color(0xFFEC4899),
                    onClick = { navController.navigate("health_journey") }
                )
                HealthFeatureCard(
                    title = "Halal Specialist",
                    description = "Temukan alternatif obat halal terbaik.",
                    icon = Icons.Default.Verified,
                    color = Color(0xFF14B8A6),
                    onClick = { navController.navigate("halal_specialist") }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun HealthFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                Text(description, fontSize = 13.sp, color = Color(0xFF64748B))
            }
            
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFCBD5E1))
        }
    }
}
