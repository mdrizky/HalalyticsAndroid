package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.halalyticscompose.utils.SessionManager
import com.example.halalyticscompose.ui.viewmodel.ScanHistoryDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryDetailScreen(
    navController: NavController,
    historyId: Int,
    viewModel: ScanHistoryDetailViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = SessionManager.getInstance(context)
    val token = sessionManager.getAuthToken() ?: ""
    val userAllergy = sessionManager.getAllergy().orEmpty()
    val userMedical = sessionManager.getMedicalHistory().orEmpty()

    val detail by viewModel.detail.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(token, historyId) {
        if (token.isNotBlank() && historyId > 0) {
            viewModel.loadDetail(token, historyId)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detail Riwayat", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { paddingValues ->
        when {
            loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(start = 24.dp))
                }
            }

            !error.isNullOrBlank() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Text(error ?: "Terjadi kesalahan.", color = MaterialTheme.colorScheme.error)
                }
            }

            detail != null -> {
                val data = detail!!
                val riskNote = when ((data.halalStatus ?: "unknown").lowercase()) {
                    "haram" -> "Produk ini tidak direkomendasikan untuk konsumsi rutin."
                    "syubhat", "unknown" -> "Status belum jelas. Disarankan cek ulang komposisi/sertifikat."
                    else -> "Status aman berdasarkan data scan saat itu."
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(data.productName ?: "Produk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Waktu scan: ${data.createdAt ?: "-"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Status halal: ${data.halalStatus ?: "unknown"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Metode: ${data.scanMethod ?: "-"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Sumber: ${data.source ?: "-"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (!data.barcode.isNullOrBlank()) {
                                    Text("Barcode: ${data.barcode}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Data Kesehatan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Confidence: ${data.confidenceScore?.toString() ?: "-"}")
                                Text("Lokasi: ${data.latitude ?: "-"}, ${data.longitude ?: "-"}")
                                Text(
                                    text = "Snapshot gizi: ${data.nutritionSnapshot?.entries?.joinToString { "${it.key}=${it.value}" } ?: "-"}",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Interpretasi Cepat", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(riskNote, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (userAllergy.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Profil alergi Anda: $userAllergy", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (userMedical.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Riwayat medis Anda: $userMedical", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
