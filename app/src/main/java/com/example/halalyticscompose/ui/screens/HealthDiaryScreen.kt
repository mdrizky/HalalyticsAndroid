package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.data.model.HealthMetricData
import com.example.halalyticscompose.ui.viewmodel.HealthDiaryViewModel

private val Emerald = Color(0xFF00A878)
private val OffWhite = Color(0xFFF6FBF8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDiaryScreen(
    navController: NavController,
    viewModel: HealthDiaryViewModel = hiltViewModel()
) {
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("🙂 Stabil") }
    val snackState = remember { SnackbarHostState() }

    val entries by viewModel.entries.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEntries()
    }

    LaunchedEffect(error) {
        error?.let {
            snackState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = OffWhite,
        snackbarHost = { SnackbarHost(hostState = snackState) },
        topBar = {
            TopAppBar(
                title = { Text("Health Diary", fontWeight = FontWeight.Bold, color = Emerald) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadEntries() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Catatan Hari Ini", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("🙂 Stabil", "😷 Kurang Fit", "🤕 Nyeri", "😴 Lelah").forEach { mood ->
                                Button(onClick = { selectedMood = mood }) {
                                    Text(mood)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Tulis gejala, obat, makanan, atau aktivitas") },
                            minLines = 4
                        )

                        Button(
                            onClick = {
                                if (note.isNotBlank()) {
                                    viewModel.saveEntry(selectedMood, note.trim())
                                    note = ""
                                }
                            },
                            enabled = !isSaving && note.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Simpan ke Server")
                            }
                        }
                    }
                }
            }

            item {
                Text("Riwayat Diary", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }

            if (isLoading && entries.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (!isLoading && entries.isEmpty()) {
                item {
                    Text("Belum ada catatan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            items(entries) { entry ->
                DiaryEntryCard(entry)
            }
        }
    }
}

@Composable
private fun DiaryEntryCard(entry: HealthMetricData) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Emerald)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(entry.value, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            }
            if (!entry.notes.isNullOrBlank()) {
                Text(entry.notes, color = Color(0xFF475569))
            }
            Text(entry.recordedAt, color = Emerald)
        }
    }
}
