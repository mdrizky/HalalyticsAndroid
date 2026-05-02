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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.MedicineViewModel
import com.example.halalyticscompose.data.model.MedicineData
import kotlinx.coroutines.delay

private val MintTheme = Color(0xFF00BFA6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineSearchScreen(
    navController: NavController,
    viewModel: MedicineViewModel = hiltViewModel(),
    onMedicineSelected: (MedicineData) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val medicineList by viewModel.medicines.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            delay(500) // debounce
            viewModel.searchMedicine(searchQuery)
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

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MintTheme)
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(medicineList) { medicine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val id = medicine.id ?: medicine.idMedicine ?: 0
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_medicine_name", medicine.name)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_medicine_id", id)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_medicine_dose", "1.0")
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selected_medicine_dose_unit", "tablet")
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
                                    medicine.kategori ?: "Obat",
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

                if (medicineList.isEmpty() && searchQuery.length >= 2 && !isLoading) {
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
