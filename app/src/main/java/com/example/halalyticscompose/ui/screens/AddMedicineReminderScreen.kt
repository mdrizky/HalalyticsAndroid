package com.example.halalyticscompose.ui.screens

import android.app.TimePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar

// ═══════════════════════════════════════════════════════════════════
// COLOR CONSTANTS — Emerald Forest Premium
// ═══════════════════════════════════════════════════════════════════
private val EmeraldDark = Color(0xFF004D40)
private val EmeraldMedium = Color(0xFF00695C)
private val EmeraldLight = Color(0xFF26A69A)
private val SageBg = Color(0xFFF4F9F8)
private val SoftSage = Color(0xFFE0F2F1)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF212121)
private val TextMedium = Color(0xFF757575)
private val TextLight = Color(0xFF9E9E9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineReminderScreen(navController: NavController) {
    val context = LocalContext.current

    // Get selected medicine from search
    val selectedName = navController.currentBackStackEntry
        ?.savedStateHandle?.get<String>("selected_medicine_name") ?: ""
    val selectedDose = navController.currentBackStackEntry
        ?.savedStateHandle?.get<String>("selected_medicine_dose") ?: "1.0"
    val selectedDoseUnit = navController.currentBackStackEntry
        ?.savedStateHandle?.get<String>("selected_medicine_dose_unit") ?: "tablet"

    var medicineName by remember { mutableStateOf(selectedName) }
    var frequencyPerDay by remember { mutableIntStateOf(3) }
    var durationDays by remember { mutableIntStateOf(7) }
    var doseAmount by remember { mutableStateOf(selectedDose) }
    var doseUnit by remember { mutableStateOf(selectedDoseUnit) }
    var customInstruction by remember { mutableStateOf("") }
    var showDosageAccordion by remember { mutableStateOf(false) }
    var showInstructionAccordion by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Auto-calculate notification times
    val notificationTimes = remember(frequencyPerDay) {
        mutableStateListOf<String>().apply {
            clear()
            when (frequencyPerDay) {
                1 -> addAll(listOf("08:00"))
                2 -> addAll(listOf("08:00", "20:00"))
                3 -> addAll(listOf("07:00", "13:00", "19:00"))
                4 -> addAll(listOf("07:00", "11:00", "15:00", "19:00"))
            }
        }
    }

    // Refresh name when returning from search
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("selected_medicine_name")
            ?.observeForever { name ->
                if (name.isNotBlank()) medicineName = name
            }
    }

    Scaffold(containerColor = SageBg) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── EMERALD GRADIENT HEADER ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(EmeraldDark, EmeraldMedium, EmeraldLight)
                        )
                    )
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = Color.White, modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        "Tambah Pengingat Baru",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.size(36.dp))
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // ── Medicine Name Card ──
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftSage)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldDark.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💊", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        if (medicineName.isNotBlank()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    medicineName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 2,
                                    color = TextDark
                                )
                            }
                            IconButton(onClick = { navController.navigate("medicine_search") }) {
                                Icon(
                                    Icons.Default.Edit, null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            TextButton(
                                onClick = { navController.navigate("medicine_search") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Search, null,
                                    tint = EmeraldDark
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Cari dan Pilih Obat",
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Frekuensi ──
                Text(
                    "Berapa kali sehari?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark
                )
                Text("Wajib diisi", fontSize = 11.sp, color = TextLight)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    (1..4).forEach { freq ->
                        FilterChip(
                            selected = frequencyPerDay == freq,
                            onClick = { frequencyPerDay = freq },
                            label = {
                                Text(
                                    "${freq}x",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notification preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Notifications, null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Notifikasi pada ${notificationTimes.joinToString(", ")}",
                            fontSize = 12.sp,
                            color = TextMedium
                        )
                    }
                }

                // Editable times
                Spacer(modifier = Modifier.height(8.dp))
                notificationTimes.forEachIndexed { index, time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                val parts = time.split(":")
                                TimePickerDialog(context, { _, h, m ->
                                    notificationTimes[index] = String.format("%02d:%02d", h, m)
                                }, parts[0].toInt(), parts[1].toInt(), true).show()
                            }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⏰", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Jadwal ${index + 1}",
                            fontSize = 13.sp,
                            color = TextDark,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            time,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Edit, null,
                            tint = TextLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Durasi ──
                Text(
                    "Berapa lama dikonsumsi?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark
                )
                Text("Wajib diisi", fontSize = 11.sp, color = TextLight)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(3, 5, 7, 14, 30).forEach { days ->
                        FilterChip(
                            selected = durationDays == days,
                            onClick = { durationDays = days },
                            label = {
                                Text(
                                    "${days}d",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Text(
                    "Pengingat selama $durationDays hari ke depan",
                    fontSize = 12.sp,
                    color = TextMedium,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Accordion: Petunjuk Penggunaan ──
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDosageAccordion = !showDosageAccordion },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoftSage),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📋", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Petunjuk Penggunaan",
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                if (showDosageAccordion) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                null, tint = TextLight
                            )
                        }
                        AnimatedVisibility(visible = showDosageAccordion) {
                            Column(modifier = Modifier.padding(top = 14.dp)) {
                                Text(
                                    "Dosis",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = doseAmount,
                                        onValueChange = { doseAmount = it },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SoftSage
                                    ) {
                                        Text(
                                            doseUnit,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldDark,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Accordion: Cara Penggunaan ──
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showInstructionAccordion = !showInstructionAccordion },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("❓", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Cara Penggunaan",
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                if (showInstructionAccordion) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                null, tint = TextLight
                            )
                        }
                        AnimatedVisibility(visible = showInstructionAccordion) {
                            Column(modifier = Modifier.padding(top = 14.dp)) {
                                Text(
                                    "Instruksi lainnya (opsional)",
                                    fontSize = 12.sp,
                                    color = TextMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = customInstruction,
                                    onValueChange = { customInstruction = it },
                                    placeholder = {
                                        Text(
                                            "Contoh: Minum dengan air putih setelah makan",
                                            fontSize = 13.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── SAVE BUTTON ──
                Button(
                    onClick = {
                        isSaving = true
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldDark,
                        disabledContainerColor = Color.LightGray
                    ),
                    enabled = medicineName.isNotBlank() && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(
                            Icons.Default.Save, null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "SIMPAN PENGINGAT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
