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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private val Emerald = Color(0xFF00A878)
private val Mint = Color(0xFFE6F7F0)
private val OffWhite = Color(0xFFF7FAF8)
private val Gold = Color(0xFFFFC857)

private data class HubItem(
    val title: String,
    val subtitle: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthSuiteHubScreen(navController: NavController) {
    val summaries = listOf(
        HubItem("Monthly Blood Panel", "Analisis gula darah stabil pada rentang normal.", "medical_resume"),
        HubItem("Cardiology Report", "Ringkasan kunjungan dokter jantung minggu ini.", "medical_records"),
        HubItem("Prescription Update", "Instruksi obat terbaru siap ditinjau.", "medicine_reminders")
    )

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            TopAppBar(
                title = { Text("Health Hub", color = Emerald, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Emerald)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Emergency Triage Status", fontWeight = FontWeight.Bold)
                            Text("Semua sistem aman. Tidak ada red-flag saat ini.", color = Color(0xFF64748B))
                        }
                        Text(
                            text = "Details",
                            color = Emerald,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { navController.navigate("ai_analysis") }
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(92.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Mint),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("POWERED BY GEMINI AI", color = Emerald, fontWeight = FontWeight.Bold)
                        }
                        Text("Smart Resume", fontWeight = FontWeight.Bold)
                        Text(
                            "Upload dokumen medis untuk ringkasan AI instan dan status kesehatan.",
                            color = Color(0xFF64748B)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("medical_resume") },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Emerald)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 11.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upload Documents", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = Emerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Halalytics AI Assistant", color = Emerald, fontWeight = FontWeight.Bold)
                        }
                        Card(colors = CardDefaults.cardColors(containerColor = OffWhite), shape = RoundedCornerShape(10.dp)) {
                            Text(
                                "Saya sudah analisis data kesehatan terakhir. Mau lanjut triage keluhan sekarang?",
                                modifier = Modifier.padding(10.dp),
                                color = Color(0xFF334155)
                            )
                        }
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            placeholder = { Text("Ask about your health...") }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            QuickHubChip("AI Triage", "health_assistant", navController)
                            QuickHubChip("Diary", "health_diary", navController)
                            QuickHubChip("Health Pass", "health_pass", navController)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Health Summaries", fontWeight = FontWeight.Bold)
                    Text(
                        text = "View All",
                        color = Emerald,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { navController.navigate("medical_records") }
                    )
                }
            }

            items(summaries) { item ->
                SummaryCard(item = item, navController = navController)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Quick Access", fontWeight = FontWeight.Bold)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickHubIcon("Monitoring", Icons.Default.MonitorHeart, "health_monitor", navController)
                    QuickHubIcon("Resume", Icons.Default.Description, "medical_resume", navController)
                    QuickHubIcon("Diary", Icons.Default.Edit, "health_diary", navController)
                    QuickHubIcon("Pass", Icons.Default.QrCode2, "health_pass", navController)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SummaryCard(item: HubItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(item.route) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Mint),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Emerald)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(item.subtitle, color = Color(0xFF64748B), style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.Add, contentDescription = null, tint = Gold)
        }
    }
}

@Composable
private fun QuickHubChip(label: String, route: String, navController: NavController) {
    Card(
        modifier = Modifier.clickable { navController.navigate(route) },
        shape = RoundedCornerShape(100.dp),
        colors = CardDefaults.cardColors(containerColor = Mint)
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = Emerald)
    }
}

@Composable
private fun RowScope.QuickHubIcon(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, route: String, navController: NavController) {
    Card(
        modifier = Modifier
            .weight(1f)
            .clickable { navController.navigate(route) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = label, tint = Emerald)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}
