package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.halalyticscompose.ui.viewmodel.MedicineViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    navController: NavController,
    medicineId: Int,
    viewModel: MedicineViewModel = viewModel()
) {
    val medicine by viewModel.selectedMedicine.collectAsState()
    val safeSchedule by viewModel.safeSchedule.collectAsState()
    val personalRiskScore by viewModel.personalRiskScore.collectAsState()
    val drugFoodConflict by viewModel.drugFoodConflict.collectAsState()
    val isRiskLoading by viewModel.isRiskLoading.collectAsState()
    val isConflictLoading by viewModel.isConflictLoading.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var mealRelation by remember { mutableStateOf("after_meal") }

    LaunchedEffect(medicineId) {
        viewModel.getMedicineDetail(medicineId)
        viewModel.clearSafeSchedule()
        viewModel.fetchPersonalRiskScore()
    }

    LaunchedEffect(medicine?.name, medicine?.idMedicine) {
        medicine?.let { med ->
            viewModel.checkDrugFoodConflict(
                medicineName = med.genericName ?: med.name,
                medicineId = med.idMedicine ?: med.id,
                lookbackMinutes = 180
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Obat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Header Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF334155))
                        )
                    )
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF3B82F6)
                )
            } else if (errorMessage != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(errorMessage ?: "Gagal memuat data", textAlign = TextAlign.Center)
                    Button(onClick = { viewModel.getMedicineDetail(medicineId) }) {
                        Text("Coba Lagi")
                    }
                }
            } else {
                medicine?.let { med ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Medicine Card Header
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White)
                                .shadowCustom(8.dp, RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!med.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = med.imageUrl,
                                    contentDescription = med.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Medication,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = Color(0xFFCBD5E1)
                                    )
                                    Text("Tidak ada gambar", color = Color(0xFF94A3B8))
                                }
                            }
                            
                            // Halal Badge Overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (med.halalStatus.lowercase()) {
                                            "halal" -> Color(0xFFDCFCE7)
                                            "haram" -> Color(0xFFFEE2E2)
                                            else -> Color(0xFFFEF3C7)
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (med.halalStatus.lowercase()) {
                                            "halal" -> Icons.Default.CheckCircle
                                            "haram" -> Icons.Default.Cancel
                                            else -> Icons.AutoMirrored.Filled.Help
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = when (med.halalStatus.lowercase()) {
                                            "halal" -> Color(0xFF166534)
                                            "haram" -> Color(0xFF991B1B)
                                            else -> Color(0xFF92400E)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = med.halalStatus.uppercase(),
                                        color = when (med.halalStatus.lowercase()) {
                                            "halal" -> Color(0xFF166534)
                                            "haram" -> Color(0xFF991B1B)
                                            else -> Color(0xFF92400E)
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Info Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = med.name,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                            med.genericName?.let {
                                Text(
                                    text = it,
                                    fontSize = 16.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Badge(containerColor = Color(0xFFE2E8F0)) {
                                    Text(med.dosageForm ?: "Tablet", color = Color(0xFF475569))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Badge(containerColor = Color(0xFFE2E8F0)) {
                                    Text(med.kategori ?: "Umum", color = Color(0xFF475569))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            InfoCard(title = "Indikasi / Kegunaan", content = med.description ?: "Informasi tidak tersedia", icon = Icons.Default.Info)
                            InfoCard(title = "Dosis", content = med.dosageInfo ?: "Gunakan sesuai petunjuk dokter", icon = Icons.Default.Straighten)
                            
                            if (!med.ingredients.isNullOrEmpty()) {
                                InfoCard(title = "Komposisi", content = med.ingredients!!.joinToString(", "), icon = Icons.Default.Science)
                            }
                            
                            InfoCard(title = "Efek Samping", content = med.sideEffects ?: "Efek samping minimal jika sesuai dosis", icon = Icons.Default.Warning, tint = Color(0xFFF59E0B))
                            
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                "Generate Jadwal Aman",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ScheduleChip(
                                    text = "Sesudah makan",
                                    selected = mealRelation == "after_meal"
                                ) { mealRelation = "after_meal" }
                                ScheduleChip(
                                    text = "Sebelum makan",
                                    selected = mealRelation == "before_meal"
                                ) { mealRelation = "before_meal" }
                                ScheduleChip(
                                    text = "Saat makan",
                                    selected = mealRelation == "with_meal"
                                ) { mealRelation = "with_meal" }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.generateSafeSchedule(
                                        medicineId = med.idMedicine ?: med.id,
                                        medicineName = med.genericName ?: med.name,
                                        frequencyPerDay = med.frequencyPerDay ?: 3,
                                        mealRelation = mealRelation
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Jadwal Aman", fontWeight = FontWeight.Bold)
                            }

                            safeSchedule?.let { schedule ->
                                Spacer(modifier = Modifier.height(12.dp))
                                InfoCard(
                                    title = "Jadwal Aman (${schedule.scheduleTimes.size}x)",
                                    content = schedule.scheduleTimes.joinToString(", "),
                                    icon = Icons.Default.AccessTime,
                                    tint = Color(0xFF0EA5E9)
                                )
                                InfoCard(
                                    title = "Disclaimer Medis",
                                    content = schedule.disclaimer ?: "Data ini hanya referensi dan bukan pengganti konsultasi dokter.",
                                    icon = Icons.Default.Info,
                                    tint = Color(0xFFEF4444)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Personal Health Risk Score",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.fetchPersonalRiskScore() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Refresh Risk Score")
                            }
                            if (isRiskLoading) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                            personalRiskScore?.let { risk ->
                                val riskColor = when ((risk.riskLevel ?: "").lowercase()) {
                                    "high" -> Color(0xFFDC2626)
                                    "moderate" -> Color(0xFFD97706)
                                    else -> Color(0xFF16A34A)
                                }
                                InfoCard(
                                    title = "Risk ${risk.riskLevel?.uppercase() ?: "-"} (${risk.riskScore ?: 0})",
                                    content = buildString {
                                        append("Sugar: ${risk.totals?.sugarG ?: 0.0}g / ${risk.limits?.sugarG ?: 50.0}g\n")
                                        append("Sodium: ${risk.totals?.sodiumMg ?: 0.0}mg / ${risk.limits?.sodiumMg ?: 2300.0}mg\n")
                                        append("Fat: ${risk.totals?.fatG ?: 0.0}g / ${risk.limits?.fatG ?: 67.0}g\n")
                                        risk.alerts.firstOrNull()?.let { append("\nAlert: $it\n") }
                                        append("\n${risk.recommendation ?: ""}")
                                    },
                                    icon = Icons.Default.HealthAndSafety,
                                    tint = riskColor
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Drug-Food Conflict Lite",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.checkDrugFoodConflict(
                                        medicineName = med.genericName ?: med.name,
                                        medicineId = med.idMedicine ?: med.id,
                                        lookbackMinutes = 180
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                            ) {
                                Icon(Icons.Default.ReportProblem, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cek Konflik Obat-Makanan")
                            }
                            if (isConflictLoading) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                            drugFoodConflict?.let { conflict ->
                                val level = (conflict.severity ?: "none").lowercase()
                                val conflictColor = when (level) {
                                    "major", "contraindicated" -> Color(0xFFDC2626)
                                    "moderate" -> Color(0xFFD97706)
                                    "minor" -> Color(0xFF16A34A)
                                    else -> Color(0xFF0EA5E9)
                                }
                                val topMatch = conflict.matches.firstOrNull()
                                InfoCard(
                                    title = if (conflict.hasConflict) "Konflik Terdeteksi (${conflict.severity ?: "-"})" else "Tidak Ada Konflik Besar",
                                    content = buildString {
                                        if (conflict.hasConflict && topMatch != null) {
                                            append("Pemicu: ${topMatch.foodName ?: "-"}\n")
                                            append("Alasan: ${topMatch.reason ?: "-"}\n\n")
                                        }
                                        append(conflict.recommendation ?: "")
                                        append("\n\n")
                                        append(conflict.disclaimer ?: "")
                                    },
                                    icon = Icons.Default.LocalHospital,
                                    tint = conflictColor
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Button(
                                onClick = { /* Navigate to add reminder */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                            ) {
                                Icon(Icons.Default.AlarmAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pasang Pengingat Minum Obat", fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, content: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color = Color(0xFF3B82F6)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = tint)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF334155), fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = content,
                color = Color(0xFF475569),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun ScheduleChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (selected) Color(0xFFDBEAFE) else Color(0xFFF1F5F9))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (selected) Color(0xFF1D4ED8) else Color(0xFF475569),
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun Modifier.shadowCustom(elevation: androidx.compose.ui.unit.Dp, shape: androidx.compose.ui.graphics.Shape) = this.then(
    Modifier.shadow(elevation, shape)
)
