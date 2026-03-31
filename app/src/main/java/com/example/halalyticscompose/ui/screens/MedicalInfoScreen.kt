package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val NavyDark = Color(0xFF0A1929)
private val Mint = Color(0xFF00BFA6)
private val MintLight = Color(0xFFE0F7EF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalInfoScreen(navController: NavController) {
    var weightKg by remember { mutableStateOf("") }
    var heightCm by remember { mutableStateOf("") }
    var chronicDiseases by remember { mutableStateOf("") }
    var hasGerd by remember { mutableStateOf<Boolean?>(null) }
    var bloodType by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }
    val selectedAllergies = remember { mutableStateListOf<String>() }
    var showAllergyModal by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informasi Medis", fontWeight = FontWeight.Bold) },
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
        ) {
            // Header Ilustrasi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(MintLight, Color.White)
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🩺", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Bagikan Informasi Medis untuk\nPerawatan Maksimal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = NavyDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Data ini digunakan untuk personalisasi AI Assistant\ndan tidak dibagikan ke pihak lain.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                // Berat Badan
                Text("Berat Badan (kg)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Opsional", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = weightKg,
                    onValueChange = { if (it.length <= 5) weightKg = it },
                    placeholder = { Text("Contoh: 55") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    supportingText = { Text("${weightKg.length}/5") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tinggi Badan
                Text("Tinggi Badan (cm)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Opsional", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = heightCm,
                    onValueChange = { if (it.length <= 3) heightCm = it },
                    placeholder = { Text("Contoh: 165") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    supportingText = { Text("${heightCm.length}/3") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Golongan Darah
                Text("Golongan Darah", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Opsional", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("A", "B", "AB", "O").forEach { type ->
                        FilterChip(
                            selected = bloodType == type,
                            onClick = { bloodType = if (bloodType == type) "" else type },
                            label = { Text(type, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Mint,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Alergi Obat
                Text("Ada Alergi Obat?", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Opsional · Data ini penting agar AI tidak merekomendasikan obat yang Anda alergi", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedCard(
                    onClick = { showAllergyModal = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        if (selectedAllergies.isEmpty()) {
                            Text("Contoh: Ibuprofen atau Bodrex", color = Color.Gray, fontSize = 14.sp)
                        } else {
                            Text(
                                selectedAllergies.joinToString(", "),
                                fontSize = 14.sp,
                                color = Mint,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Penyakit Kronis
                Text("Ada Penyakit Kronis?", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Opsional · Jika tidak ada, isi dengan tanda strip (-)", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = chronicDiseases,
                    onValueChange = { if (it.length <= 2000) chronicDiseases = it },
                    placeholder = { Text("Contoh: Diabetes, Hipertensi") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4,
                    supportingText = { Text("${chronicDiseases.length}/2000") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Riwayat GERD
                Text("Ada Riwayat GERD?", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Opsional · Mengarahkan AI merekomendasikan obat yang aman bagi lambung", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(true to "Ya", false to "Tidak").forEach { (value, label) ->
                        FilterChip(
                            selected = hasGerd == value,
                            onClick = { hasGerd = if (hasGerd == value) null else value },
                            label = { Text(label) },
                            leadingIcon = if (hasGerd == value) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Mint,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Catatan Tambahan
                Text("Catatan Tambahan", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Opsional", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = additionalNotes,
                    onValueChange = { if (it.length <= 2000) additionalNotes = it },
                    placeholder = { Text("Informasi lain yang perlu diketahui...") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Success Banner
                AnimatedVisibility(visible = showSuccess) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MintLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("✅", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Informasi medis berhasil disimpan!", fontWeight = FontWeight.Medium, color = Color(0xFF2E7D32))
                        }
                    }
                }

                // Tombol Kirim
                Button(
                    onClick = {
                        isSaving = true
                        // Simulate save
                        isSaving = false
                        showSuccess = true
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Mint),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kirim", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Allergy Modal
    if (showAllergyModal) {
        DrugAllergyModalSheet(
            selectedAllergies = selectedAllergies,
            onDismiss = { showAllergyModal = false },
            onSave = { allergies ->
                selectedAllergies.clear()
                selectedAllergies.addAll(allergies)
                showAllergyModal = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugAllergyModalSheet(
    selectedAllergies: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val tempSelected = remember { mutableStateListOf<String>().apply { addAll(selectedAllergies) } }

    val allDrugs = listOf(
        "Tidak ada alergi", "Ibuprofen", "Aspirin", "Paracetamol", "Amoxicillin",
        "Penisilin", "Sulfonamida", "Cephalosporin", "Codeine", "Naproxen",
        "Diclofenac", "Piroxicam", "Mefenamic Acid", "Tramadol", "Morphine",
        "Metformin", "Glibenclamide", "Captopril", "Amlodipine", "Simvastatin",
        "Omeprazole", "Ranitidine", "Ciprofloxacin", "Azithromycin", "Dexamethasone",
        "Prednisone", "Cetirizine", "Loratadine", "Diphenhydramine", "Salbutamol",
        "Tolak Angin", "Bodrex", "Mixagrip", "Decolgen", "Promag",
        "Antangin", "Komix", "OBH Combi", "Vicks Formula 44", "Neurobion"
    )

    val filteredDrugs = if (searchQuery.isBlank()) allDrugs
    else allDrugs.filter { it.contains(searchQuery, ignoreCase = true) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Ada Alergi Obat?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari nama obat...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .heightIn(max = 350.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                filteredDrugs.forEach { drug ->
                    val isSelected = tempSelected.contains(drug)
                    val isNone = drug == "Tidak ada alergi"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isNone) {
                                    tempSelected.clear()
                                    tempSelected.add(drug)
                                } else {
                                    tempSelected.remove("Tidak ada alergi")
                                    if (isSelected) tempSelected.remove(drug) else tempSelected.add(drug)
                                }
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(
                                checkedColor = Mint
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            drug,
                            fontSize = 14.sp,
                            fontWeight = if (isNone) FontWeight.Bold else FontWeight.Normal,
                            color = if (isNone) Color.Gray else Color.Unspecified
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Batal", color = Color(0xFFE5173F), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = {
                        val result = if (tempSelected.contains("Tidak ada alergi")) emptyList() else tempSelected.toList()
                        onSave(result)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Mint)
                ) {
                    Text("Simpan & Tutup", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
