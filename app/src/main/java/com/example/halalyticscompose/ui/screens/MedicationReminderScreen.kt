package com.example.halalyticscompose.ui.screens

import android.app.TimePickerDialog
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
import com.example.halalyticscompose.ui.viewmodel.HealthAiViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationReminderScreen(
    navController: NavController,
    viewModel: HealthAiViewModel = viewModel()
) {
    val context = LocalContext.current
    var drugName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("3x sehari") }
    val selectedTimes = remember { mutableStateListOf<String>() }
    
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengingat Obat Pintar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("Jadwalkan Konsumsi Obat", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("AI akan mengingatkan Anda dengan pesan suara yang ramah.", color = MaterialTheme.colorScheme.onSurface.copy(0.6f), fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = drugName,
                onValueChange = { drugName = it },
                label = { Text("Nama Obat") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosis (e.g. 1 Tablet, 5ml)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Frekuensi", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            val frequencies = listOf("1x sehari", "2x sehari", "3x sehari", "4x sehari")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                frequencies.forEach { freq ->
                    FilterChip(
                        selected = frequency == freq,
                        onClick = { frequency = freq },
                        label = { Text(freq) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Waktu Pengingat", fontWeight = FontWeight.Bold)
            selectedTimes.forEachIndexed { index, time ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(time, modifier = Modifier.weight(1f))
                    IconButton(onClick = { selectedTimes.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            TextButton(
                onClick = {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(context, { _, hour, minute ->
                        selectedTimes.add(String.format("%02d:%02d", hour, minute))
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Jam")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    viewModel.createReminder(
                        drugId = 1, // Placeholder
                        dosage = dosage,
                        frequency = frequency,
                        timeSlots = selectedTimes.toList(),
                        startDate = "2026-02-10"
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = dosage.isNotBlank() && selectedTimes.isNotEmpty() && !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Alarm, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Simpan Jadwal Pintar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
