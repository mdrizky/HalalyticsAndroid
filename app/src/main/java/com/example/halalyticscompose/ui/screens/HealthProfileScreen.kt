package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthProfileScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Health data
    val bmi by viewModel.bmi.collectAsState()
    val activityLevel by viewModel.activityLevel.collectAsState()
    val totalScans by viewModel.totalScans.collectAsState()
    val halalProducts by viewModel.halalProducts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Profil Kesehatan", 
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkBackground,
                            DarkBackground,
                            HalalGreen.copy(alpha = 0.05f)
                        )
                    )
                )
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // BMI Card
            HealthCard(
                title = "Indeks Massa Tubuh (BMI)",
                icon = Icons.Default.MonitorWeight,
                color = Color(0xFF3B82F6)
            ) {
                Column {
                    Text(
                        text = bmi,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = getBMICategory(bmi),
                        fontSize = 14.sp,
                        color = TextGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            // TODO: Open BMI calculator
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        )
                    ) {
                        Text("Hitung Ulang BMI")
                    }
                }
            }

            // Activity Level Card
            HealthCard(
                title = "Tingkat Aktivitas",
                icon = Icons.AutoMirrored.Filled.DirectionsRun,
                color = Color(0xFF10B981)
            ) {
                Column {
                    Text(
                        text = activityLevel,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            // TODO: Open activity level selector
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        )
                    ) {
                        Text("Ubah Aktivitas")
                    }
                }
            }

            // Scan Statistics Card
            HealthCard(
                title = "Statistik Scan",
                icon = Icons.Default.QrCodeScanner,
                color = Color(0xFF8B5CF6)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("Total Scan", totalScans.toString())
                        StatItem("Produk Halal", halalProducts.toString())
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            navController.navigate("history")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B5CF6)
                        )
                    ) {
                        Text("Lihat Riwayat")
                    }
                }
            }

            // Health Tools Card
            HealthCard(
                title = "Alat Kesehatan",
                icon = Icons.Default.HealthAndSafety,
                color = Color(0xFF0EA5E9)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HealthToolItem(
                        title = "Scanner Makanan",
                        description = "Scan kesehatan makanan",
                        icon = Icons.Default.CameraAlt,
                        onClick = { navController.navigate("scan") }
                    )
                    
                    HealthToolItem(
                        title = "AI Meal Scanner",
                        description = "Analisis makanan dengan AI",
                        icon = Icons.Default.Restaurant,
                        onClick = { navController.navigate("meal_scan") }
                    )
                    
                    HealthToolItem(
                        title = "Asisten Kesehatan AI",
                        description = "Konsultasi dengan AI",
                        icon = Icons.Default.MedicalInformation,
                        onClick = { navController.navigate("health_assistant") }
                    )
                }
            }
        }
    }
}

@Composable
fun HealthCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCard
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
            content()
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextGray
        )
    }
}

@Composable
fun HealthToolItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF0EA5E9),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun getBMICategory(bmi: String): String {
    return try {
        val bmiValue = bmi.toFloatOrNull() ?: return "Tidak diketahui"
        when {
            bmiValue < 18.5 -> "Kurus"
            bmiValue < 25 -> "Normal"
            bmiValue < 30 -> "Berlebih"
            else -> "Obesitas"
        }
    } catch (e: Exception) {
        "Tidak diketahui"
    }
}
