package com.example.halalyticscompose.ui.screens

import android.app.TimePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar

private val MintColor = Color(0xFF00BFA6)
private val MintBg = Color(0xFFE6F7F0)

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Pengingat Baru", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Medicine Name Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MintBg)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💊", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    if (medicineName.isNotBlank()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(medicineName, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                        }
                        IconButton(onClick = { navController.navigate("medicine_search") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Ganti obat", tint = MintColor)
                        }
                    } else {
                        TextButton(
                            onClick = { navController.navigate("medicine_search") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cari dan Pilih Obat", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Frekuensi
            Text("Berapa kali sehari?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Wajib diisi", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                (1..4).forEach { freq ->
                    FilterChip(
                        selected = frequencyPerDay == freq,
                        onClick = { frequencyPerDay = freq },
                        label = { Text("${freq}x", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MintColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notification preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MintColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Kamu akan menerima notifikasi pengingat pada ${notificationTimes.joinToString(", ")}",
                        fontSize = 12.sp, color = Color.Gray
                    )
                }
            }

            // Editable times
            Spacer(modifier = Modifier.height(8.dp))
            notificationTimes.forEachIndexed { index, time ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val parts = time.split(":")
                            TimePickerDialog(context, { _, h, m ->
                                notificationTimes[index] = String.format("%02d:%02d", h, m)
                            }, parts[0].toInt(), parts[1].toInt(), true).show()
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⏰", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Jadwal ${index + 1}", fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Text(time, fontWeight = FontWeight.Bold, color = MintColor)
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Durasi
            Text("Berapa lama obat ini harus dikonsumsi?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Wajib diisi", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(3, 5, 7, 14, 30).forEach { days ->
                    FilterChip(
                        selected = durationDays == days,
                        onClick = { durationDays = days },
                        label = { Text("${days}d", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MintColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text(
                "Notifikasi pengingat akan muncul selama $durationDays hari",
                fontSize = 12.sp, color = Color.Gray,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Accordion: Petunjuk Penggunaan (Dosis)
            Card(
                modifier = Modifier.fillMaxWidth().clickable { showDosageAccordion = !showDosageAccordion },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📋 Petunjuk Penggunaan", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Icon(
                            if (showDosageAccordion) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                    AnimatedVisibility(visible = showDosageAccordion) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Text("Dosis", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = doseAmount,
                                    onValueChange = { doseAmount = it },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                                Text(doseUnit, fontWeight = FontWeight.Bold, color = MintColor)
                                IconButton(onClick = { /* edit unit picker */ }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit unit", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Accordion: Cara Penggunaan
            Card(
                modifier = Modifier.fillMaxWidth().clickable { showInstructionAccordion = !showInstructionAccordion },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("❓ Bagaimana cara penggunaannya?", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Icon(
                            if (showInstructionAccordion) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                    AnimatedVisibility(visible = showInstructionAccordion) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Text("Instruksi lainnya (opsional)", fontSize = 13.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = customInstruction,
                                onValueChange = { customInstruction = it },
                                placeholder = { Text("Contoh: Minum dengan air putih setelah makan") },
                                modifier = Modifier.fillMaxWidth().height(80.dp),
                                shape = RoundedCornerShape(10.dp),
                                maxLines = 3
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol SIMPAN
            Button(
                onClick = {
                    isSaving = true
                    // Save reminder to backend
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MintColor,
                    disabledContainerColor = Color.LightGray
                ),
                enabled = medicineName.isNotBlank() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIMPAN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
