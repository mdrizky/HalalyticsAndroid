package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.DarkBackground
import com.example.halalyticscompose.ui.theme.DarkCard
import com.example.halalyticscompose.ui.theme.HalalGreen
import com.example.halalyticscompose.ui.theme.TextGray
import com.example.halalyticscompose.ui.theme.TextWhite
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import com.example.halalyticscompose.ui.viewmodel.PharmacyViewModel

@Composable
fun PharmacyScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = HalalGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medicine", tint = Color.White)
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(
                "My Pharmacy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading && myMedicines.isEmpty()) {
                CircularProgressIndicator(color = HalalGreen)
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
                                        tint = TextGray,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Belum ada obat tersimpan",
                                        color = TextGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Tekan + untuk menambah",
                                        color = TextGray,
                                        fontSize = 12.sp
                                    )
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
                Column {
                    OutlinedTextField(
                        value = searchMedName,
                        onValueChange = { searchMedName = it },
                        label = { Text("Medicine Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.checkMedicineHalal(searchMedName)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading && searchMedName.isNotEmpty()
                        ) {
                            Text(if (isLoading) "Checking..." else "Check Local")
                        }
                        
                        Button(
                            onClick = {
                                viewModel.searchGlobalMedicine(searchMedName)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            enabled = !isLoading && searchMedName.isNotEmpty()
                        ) {
                            Text(if (isLoading) "Search..." else "Global Search")
                        }
                    }
                    
                    if (checkResult != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = (if(checkResult!!.halalStatus == "halal") HalalGreen else Color.Red).copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Status: ${checkResult!!.halalStatus.uppercase()}",
                                    color = if(checkResult!!.halalStatus == "halal") HalalGreen else Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(checkResult!!.description ?: "", fontSize = 12.sp, color = TextWhite)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage") })
                    Spacer(modifier = Modifier.height(8.dp))
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
                ) {
                    Text("Add to Schedule")
                }
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
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0F2F1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF009688))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = med.drug?.name ?: "Obat #${med.drugId}",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontSize = 16.sp
                )
                Text(
                    text = "${med.dosage} • ${med.timeSlots?.firstOrNull() ?: "No Time"}",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
            val halalStatus = med.drug?.halalStatus ?: "syubhat"
            if (halalStatus == "halal") {
                Icon(Icons.Default.CheckCircle, contentDescription = "Halal", tint = HalalGreen)
            } else if (halalStatus == "haram") {
                Icon(Icons.Default.Warning, contentDescription = "Warning", tint = Color.Red)
            }
        }
    }
}
