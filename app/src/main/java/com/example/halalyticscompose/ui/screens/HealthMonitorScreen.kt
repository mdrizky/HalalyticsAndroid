package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import com.example.halalyticscompose.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMonitorScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val color = MaterialTheme.colorScheme
    val bmi by viewModel.bmi.collectAsState()
    val dailyIntake by viewModel.dailyIntake.collectAsState()

    val bmiValue = remember(bmi) { bmi.toDoubleOrNull() ?: 0.0 }
    val bmiStatus = remember(bmiValue) { getBmiStatus(bmiValue) }
    val sugarPct = remember(dailyIntake) {
        val sugar = dailyIntake?.dailyIntake?.totalSugarG ?: 0
        val limit = (dailyIntake?.targets?.sugarLimitG ?: 50).coerceAtLeast(1)
        ((sugar.toFloat() / limit.toFloat()) * 100f).coerceIn(0f, 300f)
    }
    val sugarStatus = remember(sugarPct) { getSugarStatus(sugarPct) }
    val hydrationPct = remember(dailyIntake) { dailyIntake?.progress?.waterPercentage ?: 0f }
    val hydrationStatus = remember(hydrationPct) { getHydrationStatus(hydrationPct) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pantauan Tubuh", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = color.background
                )
            )
        },
        containerColor = color.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricChip("Hidrasi", hydrationStatus, Icons.Default.WaterDrop)
                MetricChip("IMT", bmiStatus, Icons.Default.MonitorWeight)
                MetricChip("Gula", sugarStatus, Icons.Default.Favorite)
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = color.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Status Indeks Massa Tubuh", color = color.onSurfaceVariant, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "${if (bmiValue > 0) "%.1f".format(bmiValue) else "0.0"} (${bmiStatus})",
                        fontWeight = FontWeight.ExtraBold,
                        color = getBmiColor(bmiValue),
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        buildMonitorSummary(
                            bmiStatus = bmiStatus,
                            sugarPct = sugarPct,
                            hydrationPct = hydrationPct
                        ),
                        color = color.onSurface,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

private fun getBmiStatus(bmi: Double): String {
    if (bmi <= 0.0) return "Belum diisi"
    return when {
        bmi < 18.5 -> "Kurus"
        bmi < 25.0 -> "Ideal"
        bmi < 30.0 -> "Berlebih"
        else -> "Obesitas"
    }
}

private fun getBmiColor(bmi: Double): Color {
    if (bmi <= 0.0) return Color(0xFF6B7280)
    return when {
        bmi < 18.5 -> Color(0xFFF59E0B)
        bmi < 25.0 -> Color(0xFF17A34A)
        bmi < 30.0 -> Color(0xFFF97316)
        else -> Color(0xFFDC2626)
    }
}

private fun getSugarStatus(percentage: Float): String {
    return when {
        percentage < 50f -> "Aman"
        percentage <= 100f -> "Waspada"
        else -> "Tinggi"
    }
}

private fun getHydrationStatus(percentage: Float): String {
    return when {
        percentage < 50f -> "Kurang"
        percentage < 100f -> "Cukup"
        else -> "Terpenuhi"
    }
}

private fun buildMonitorSummary(
    bmiStatus: String,
    sugarPct: Float,
    hydrationPct: Float
): String {
    val sugarText = when {
        sugarPct < 50f -> "asupan gula masih rendah"
        sugarPct <= 100f -> "asupan gula mendekati batas harian"
        else -> "asupan gula melewati batas harian"
    }
    val hydrationText = when {
        hydrationPct < 50f -> "hidrasi masih kurang"
        hydrationPct < 100f -> "hidrasi cukup baik"
        else -> "hidrasi harian sudah tercapai"
    }
    return "BMI Anda saat ini $bmiStatus, $sugarText, dan $hydrationText. " +
        "Jaga pola makan seimbang, aktivitas rutin, serta minum air yang cukup."
}

@Composable
private fun MetricChip(title: String, value: String, icon: ImageVector) {
    val color = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(99.dp),
        colors = CardDefaults.cardColors(containerColor = color.primaryContainer.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color.primary, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.size(6.dp))
            Column {
                Text(title, fontSize = 10.sp, color = color.onSurfaceVariant)
                Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color.onSurface)
            }
        }
    }
}
