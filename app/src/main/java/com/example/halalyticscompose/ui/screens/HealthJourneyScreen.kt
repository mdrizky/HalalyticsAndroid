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
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthJourneyScreen(
    navController: NavController,
    viewModel: HealthAiViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf("Weight") }
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
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
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
                            .background(if (selectedTab == metric) Color(0xFF3B82F6) else Color(0xFFF1F5F9))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = metric,
                            color = if (selectedTab == metric) Color.White else Color(0xFF64748B),
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
                IconButton(onClick = { /* Add data dialog */ }) {
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
                            color = Color.Gray, 
                            modifier = Modifier.fillMaxWidth().padding(32.dp), 
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HealthMetricItem(data: com.example.halalyticscompose.Data.Model.HealthMetricData) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF3B82F6).copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = data.value.take(2),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B82F6)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${data.value}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(data.recordedAt, fontSize = 12.sp, color = Color.Gray)
            }
            if (!data.notes.isNullOrBlank()) {
                Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF94A3B8))
            }
        }
    }
}
