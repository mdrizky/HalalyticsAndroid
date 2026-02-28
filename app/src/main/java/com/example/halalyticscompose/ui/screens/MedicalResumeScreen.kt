package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.viewmodel.MedicalRecordsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalResumeScreen(
    navController: NavController,
    viewModel: MedicalRecordsViewModel = hiltViewModel()
) {
    val color = MaterialTheme.colorScheme
    val isLoading by viewModel.isLoading.collectAsState()
    val records by viewModel.records.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRecords()
    }

    Scaffold(
        containerColor = color.background,
        topBar = {
            TopAppBar(
                title = { Text("Resume Medis", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = color.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = color.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Ringkasan Cepat", fontWeight = FontWeight.Bold)
                    Text("Total dokumen medis: ${records.size}", color = color.onSurfaceVariant)
                    Text("Gunakan layar ini saat konsultasi dokter untuk ringkasan riwayat.", color = color.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading && records.isEmpty() -> CircularProgressIndicator()
                !error.isNullOrBlank() -> Text(error ?: "Gagal memuat data", color = color.error)
                records.isEmpty() -> Text("Belum ada resume medis.", color = color.onSurfaceVariant)
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(records.take(20)) { record ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = color.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(record.title, fontWeight = FontWeight.Bold)
                                    Text("Jenis: ${record.recordType}", color = color.onSurfaceVariant)
                                    Text("Tanggal: ${record.recordDate}", color = color.onSurfaceVariant)
                                    if (!record.hospitalName.isNullOrBlank()) {
                                        Text("Faskes: ${record.hospitalName}", color = color.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
