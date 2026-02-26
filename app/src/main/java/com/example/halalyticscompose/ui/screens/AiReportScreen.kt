package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.ui.viewmodel.AiReportViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiReportScreen(
    navController: NavController,
    viewModel: AiReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWeeklyReport()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI Medical Report", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(8.dp).clip(CircleShape).background(Color.White.copy(0.05f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgDarkBase)
            )
        },
        containerColor = BgDarkBase
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Emerald500)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Synthesizing Health Data...", color = TextMuted)
                }
            } else if (uiState.errorMessage != null) {
                ErrorDisplayPremium(message = uiState.errorMessage!!) {
                    viewModel.fetchWeeklyReport()
                }
            } else if (uiState.stats != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Weekly Insight Header
                    val insight = uiState.insight
                    if (insight != null && insight.summary != null && insight.error == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Brush.linearGradient(listOf(Emerald600, BgDarkSurface)))
                                .padding(24.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("AI HEALTH INSIGHT", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = insight.summary ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    lineHeight = 24.sp
                                )
                                if (insight.highlight != null) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Box(
                                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.1f)).padding(12.dp)
                                    ) {
                                        Text(text = insight.highlight, color = MushboohYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Activity Summary
                    Text(
                        "Clinical Activity", 
                        style = MaterialTheme.typography.titleMedium, 
                        color = Color.White,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp)
                    )
                    
                    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ReportStatCard(
                                label = "Total Scans",
                                value = uiState.stats!!.totalScans.toString(),
                                icon = Icons.Default.QrCodeScanner,
                                color = Color(0xFF3B82F6),
                                modifier = Modifier.weight(1f)
                            )
                            ReportStatCard(
                                label = "Halal Valid",
                                value = uiState.stats!!.halalCount.toString(),
                                icon = Icons.Default.CheckCircle,
                                color = Emerald500,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ReportStatCard(
                                label = "Risk Flags",
                                value = uiState.stats!!.syubhatCount.toString(),
                                icon = Icons.Default.Warning,
                                color = MushboohYellow,
                                modifier = Modifier.weight(1f)
                            )
                            ReportStatCard(
                                label = "Wellness",
                                value = uiState.stats!!.healthyCount.toString(),
                                icon = Icons.Default.Favorite,
                                color = HaramRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // AI Health Tips
                    if (uiState.insight?.tips?.isNotEmpty() == true) {
                        Text(
                            "Growth & Wellness Tips", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = Color.White,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp)
                        )
                        Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.insight!!.tips.forEach { tip ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(BgDarkSurface)
                                        .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(20.dp))
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TipsAndUpdates, null, tint = MushboohYellow, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(text = tip, color = Color.White.copy(0.7f), fontSize = 13.sp, lineHeight = 20.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Category Trends
                    if (uiState.stats?.topCategories?.isNotEmpty() == true) {
                        Text(
                            "Nutritional Trends", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = Color.White,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(BgDarkSurface)
                                .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(28.dp))
                                .padding(24.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                uiState.stats!!.topCategories.forEach { (cat, count) ->
                                    val progress = count.toFloat() / uiState.stats!!.totalScans.toFloat()
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(cat, color = TextMuted, fontSize = 12.sp)
                                            Text("$count records", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                            color = Emerald500,
                                            trackColor = Color.White.copy(0.05f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ReportStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(BgDarkSurface)
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Black)
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

@Composable
fun ErrorDisplayPremium(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.ErrorOutline, null, tint = HaramRed, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = message, color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Emerald500)) {
            Text("Retry Report", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
