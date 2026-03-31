package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

private val MintTheme = Color(0xFF00BFA6)

data class SearchableMedicine(
    val id: Long,
    val name: String,
    val unit: String,
    val category: String,
    val defaultDose: String = "1.0",
    val defaultDoseUnit: String = "tablet"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineSearchScreen(
    navController: NavController,
    onMedicineSelected: (SearchableMedicine) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // Mock data — in production, query API
    val allMedicines = remember {
        listOf(
            SearchableMedicine(1, "Tolak Angin Cair Plus Madu 15 ml 12 Sachet", "Per Box", "Herbal", "1.0", "sachet"),
            SearchableMedicine(2, "Tolak Angin Anak Cair Plus Madu 10 ml 5 Sachet", "Per Box", "Herbal Anak", "1.0", "sachet"),
            SearchableMedicine(3, "Paracetamol 500mg Tablet", "Per Strip (10 Tablet)", "Analgesik", "1.0", "tablet"),
            SearchableMedicine(4, "Amoxicillin 500mg Kapsul", "Per Strip (10 Kapsul)", "Antibiotik", "1.0", "kapsul"),
            SearchableMedicine(5, "Omeprazole 20mg Kapsul", "Per Strip (10 Kapsul)", "Anti Asam Lambung", "1.0", "kapsul"),
            SearchableMedicine(6, "Cetirizine 10mg Tablet", "Per Strip (10 Tablet)", "Antihistamin", "1.0", "tablet"),
            SearchableMedicine(7, "Ibuprofen 400mg Tablet", "Per Strip (10 Tablet)", "Anti Inflamasi", "1.0", "tablet"),
            SearchableMedicine(8, "Vitamin C 1000mg Tablet Effervescent", "Per Tube (10 Tablet)", "Vitamin", "1.0", "tablet"),
            SearchableMedicine(9, "OBH Combi Batuk Plus Flu 100ml", "Per Botol", "Batuk & Flu", "5.0", "ml"),
            SearchableMedicine(10, "Promag Tablet Kunyah", "Per Strip (6 Tablet)", "Antasida", "1.0", "tablet"),
            SearchableMedicine(11, "Neurobion Forte Tablet", "Per Strip (10 Tablet)", "Multivitamin", "1.0", "tablet"),
            SearchableMedicine(12, "Diclofenac Sodium 50mg Tablet", "Per Strip (10 Tablet)", "Anti Inflamasi", "1.0", "tablet"),
            SearchableMedicine(13, "Metformin 500mg Tablet", "Per Strip (10 Tablet)", "Diabetes", "1.0", "tablet"),
            SearchableMedicine(14, "Amlodipine 5mg Tablet", "Per Strip (10 Tablet)", "Hipertensi", "1.0", "tablet"),
            SearchableMedicine(15, "Simvastatin 20mg Tablet", "Per Strip (10 Tablet)", "Kolesterol", "1.0", "tablet"),
            SearchableMedicine(16, "Salbutamol Inhaler 100mcg", "Per Unit", "Asma", "1.0", "puff"),
            SearchableMedicine(17, "Antangin JRG Cair 15ml", "Per Sachet", "Herbal", "1.0", "sachet"),
            SearchableMedicine(18, "Bodrex Extra Tablet", "Per Strip (4 Tablet)", "Analgesik", "1.0", "tablet"),
            SearchableMedicine(19, "Mixagrip Flu & Batuk Tablet", "Per Strip (4 Tablet)", "Flu & Batuk", "1.0", "tablet"),
            SearchableMedicine(20, "Dexamethasone 0.5mg Tablet", "Per Strip (10 Tablet)", "Kortikosteroid", "1.0", "tablet"),
        )
    }

    val filteredMedicines = if (searchQuery.length >= 2) {
        allMedicines.filter { it.name.contains(searchQuery, ignoreCase = true) }
    } else {
        allMedicines
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            isSearching = true
            delay(300) // debounce
            isSearching = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cari Obat", fontWeight = FontWeight.Bold) },
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
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Ketik nama obat...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            if (isSearching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MintTheme)
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredMedicines) { medicine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_medicine_name", medicine.name)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_medicine_id", medicine.id)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_medicine_dose", medicine.defaultDose)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_medicine_dose_unit", medicine.defaultDoseUnit)
                                navController.popBackStack()
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Medicine Icon
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = MintTheme.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("💊", fontSize = 22.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    medicine.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    medicine.unit,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            IconButton(onClick = { /* show info */ }) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF0F0F0))
                }

                if (filteredMedicines.isEmpty() && searchQuery.length >= 2) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Obat tidak ditemukan", fontWeight = FontWeight.Bold)
                            Text(
                                "Coba ketik dengan kata kunci lain",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
