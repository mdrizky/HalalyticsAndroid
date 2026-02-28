package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.viewmodel.PharmacyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyScreen(
    navController: NavController,
    viewModel: PharmacyViewModel = hiltViewModel()
) {
    val myMedicines by viewModel.myMedicines.collectAsState()
    val checkResult by viewModel.checkResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var searchMedName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("1 Tablet") }
    var time by remember { mutableStateOf("08:00") }

    LaunchedEffect(Unit) {
        viewModel.fetchMyMedicines()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Pharmacy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medicine", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            if (isLoading && myMedicines.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (myMedicines.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Belum ada obat tersimpan", fontWeight = FontWeight.Bold)
                                    Text("Tekan + untuk menambah", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(myMedicines) { med ->
                            MedicineCard(med)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Medicine") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchMedName,
                        onValueChange = { searchMedName = it },
                        label = { Text("Medicine Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.checkMedicineHalal(searchMedName) },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading && searchMedName.isNotEmpty()
                        ) {
                            Text(if (isLoading) "Checking..." else "Check")
                        }
                        Button(
                            onClick = { viewModel.searchGlobalMedicine(searchMedName) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            enabled = !isLoading && searchMedName.isNotEmpty()
                        ) {
                            Text(if (isLoading) "Search..." else "Global")
                        }
                    }

                    if (checkResult != null) {
                        val halal = checkResult!!.halalStatus.equals("halal", true)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (halal) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "Status: ${checkResult!!.halalStatus.uppercase()}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (halal) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                                Text(checkResult!!.description ?: "", fontSize = 12.sp)
                            }
                        }
                    }

                    OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage") })
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (HH:MM)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addSchedule(checkResult?.id, searchMedName, dosage, time) {
                            showAddDialog = false
                        }
                    }
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun MedicineCard(med: com.example.halalyticscompose.Data.Model.MedicationReminderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = med.drug?.name ?: "Obat #${med.drugId}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
                Text(
                    text = "${med.dosage} • ${med.timeSlots?.firstOrNull() ?: "No Time"}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
            val halalStatus = med.drug?.halalStatus ?: "syubhat"
            if (halalStatus == "halal") {
                Icon(Icons.Default.CheckCircle, contentDescription = "Halal", tint = Color(0xFF2E7D32))
            } else if (halalStatus == "haram") {
                Icon(Icons.Default.Warning, contentDescription = "Warning", tint = Color(0xFFC62828))
            }
        }
    }
}
