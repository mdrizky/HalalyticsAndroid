package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.viewmodel.HealthAiViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthJourneyScreen(
    navController: NavController,
    viewModel: HealthAiViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf("Weight") }
    var showAddDialog by remember { mutableStateOf(false) }
    var metricValue by remember { mutableStateOf("") }
    var metricNotes by remember { mutableStateOf("") }
    val metrics = listOf("Weight", "Blood Pressure", "Blood Sugar", "Cholesterol")
    
    val metricHistory by viewModel.metricHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(selectedTab) {
        viewModel.fetchMetricHistory(selectedTab.lowercase().replace(" ", "_"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Journey Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            ScrollableTabRow(
                selectedTabIndex = metrics.indexOf(selectedTab),
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {},
                indicator = {}
            ) {
                metrics.forEach { metric ->
                    Tab(
                        selected = selectedTab == metric,
                        onClick = { selectedTab = metric },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedTab == metric) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = metric,
                            color = if (selectedTab == metric) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFFE2E8F0))
                        Text("Visualisasi Grafik $selectedTab", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Riwayat Terakhir", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = { 
                    metricValue = ""
                    metricNotes = ""
                    showAddDialog = true
                }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = Color(0xFF3B82F6), modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    metricHistory.forEach { data ->
                        HealthMetricItem(data)
                    }
                    if (metricHistory.isEmpty()) {
                        Text(
                            "Belum ada data untuk $selectedTab", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant, 
                            modifier = Modifier.fillMaxWidth().padding(32.dp), 
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        val metricType = selectedTab.lowercase().replace(" ", "_")
        val dateNow = remember {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Data $selectedTab") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = metricValue,
                        onValueChange = { metricValue = it },
                        label = { Text("Nilai $selectedTab") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = metricNotes,
                        onValueChange = { metricNotes = it },
                        label = { Text("Catatan (opsional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = metricValue.isNotBlank(),
                    onClick = {
                        viewModel.recordMetric(
                            type = metricType,
                            value = metricValue.trim(),
                            date = dateNow,
                            notes = metricNotes.trim().ifBlank { null }
                        )
                        showAddDialog = false
                    }
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun HealthMetricItem(data: com.example.halalyticscompose.data.model.HealthMetricData) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = data.value.take(2),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${data.value}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(data.recordedAt, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!data.notes.isNullOrBlank()) {
                Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
