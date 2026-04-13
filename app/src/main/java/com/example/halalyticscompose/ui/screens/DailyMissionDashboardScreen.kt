package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.Data.Model.DailyMissionData
import com.example.halalyticscompose.Data.Model.Mission
import com.example.halalyticscompose.presentation.viewmodel.DashboardViewModel

private val MissionTeal = Color(0xFF004D40)
private val MissionMint = Color(0xFFE0F2F1)
private val MissionBg = Color(0xFFF4F9F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMissionDashboardScreen(
    onNavigateBack: () -> Unit,
    onGoToScan: () -> Unit,
    onGoToNutrition: () -> Unit,
    onGoToAr: () -> Unit,
    onGoToMarketplace: () -> Unit,
    onGoToCommunity: () -> Unit,
    onGoToHalocode: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Scaffold(
        containerColor = MissionBg,
        topBar = {
            TopAppBar(
                title = { Text("Misi Harian AI", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { paddingValues ->
        if (uiState.isLoading && uiState.missionData == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MissionTeal)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Text(
                        text = "Selamat datang, ${uiState.userName ?: "Sobat Halal"}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Selesaikan misi kecil hari ini untuk dapat poin dan kebiasaan sehat yang konsisten.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item {
                DailyMissionSummaryCard(
                    missionData = uiState.missionData,
                    isCompletingMission = uiState.isCompletingMission,
                    onMissionClick = { mission ->
                        when (mission.id) {
                            "scan" -> onGoToScan()
                            "nutrition" -> onGoToNutrition()
                            "location" -> {
                                if (mission.isCompleted) {
                                    onGoToAr()
                                } else {
                                    viewModel.completeMission("location", onGoToAr)
                                }
                            }
                        }
                    },
                )
            }

            item {
                Text(
                    text = "Akses Cepat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                MissionQuickAccessGrid(
                    onGoToScan = onGoToScan,
                    onGoToNutrition = onGoToNutrition,
                    onGoToAr = onGoToAr,
                    onGoToMarketplace = onGoToMarketplace,
                    onGoToCommunity = onGoToCommunity,
                    onGoToHalocode = onGoToHalocode,
                )
            }

            uiState.error?.takeIf { it.isNotBlank() }?.let { message ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = message,
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun DailyMissionSummaryCard(
    missionData: DailyMissionData?,
    isCompletingMission: Boolean,
    onMissionClick: (Mission) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MissionMint),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Progress Hari Ini",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MissionTeal,
                    )
                    Text(
                        text = "${missionData?.completedCount ?: 0}/${missionData?.totalCount ?: 0} misi selesai",
                        style = MaterialTheme.typography.bodySmall,
                        color = MissionTeal.copy(alpha = 0.75f),
                    )
                }

                Surface(
                    color = MissionTeal,
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        text = "+${missionData?.pointsEarnedToday ?: 0} poin",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = {
                    if (missionData != null && missionData.totalCount > 0) {
                        missionData.completedCount.toFloat() / missionData.totalCount.toFloat()
                    } else {
                        0f
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp),
                color = MissionTeal,
                trackColor = Color.White.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(18.dp))

            (missionData?.missions ?: emptyList()).forEachIndexed { index, mission ->
                MissionItemRow(
                    mission = mission,
                    isCompletingMission = isCompletingMission && mission.id == "location",
                    onClick = { onMissionClick(mission) },
                )

                if (index < (missionData?.missions?.lastIndex ?: -1)) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MissionTeal.copy(alpha = 0.12f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionItemRow(
    mission: Mission,
    isCompletingMission: Boolean,
    onClick: () -> Unit,
) {
    val icon = when (mission.iconType) {
        "scan" -> Icons.Default.QrCodeScanner
        "nutrition" -> Icons.Default.Restaurant
        else -> Icons.Default.Map
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = if (mission.isCompleted) Color(0xFF2E7D32) else Color.White,
            shape = CircleShape,
            modifier = Modifier.size(42.dp),
        ) {
            if (mission.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(10.dp),
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MissionTeal,
                    modifier = Modifier.padding(10.dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mission.title,
                fontWeight = FontWeight.SemiBold,
                textDecoration = if (mission.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = MissionTeal,
            )
            Text(
                text = mission.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (isCompletingMission) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MissionTeal)
        } else {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "+${mission.pointsReward}",
                        color = MissionTeal,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (!mission.isCompleted) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MissionTeal,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionQuickAccessGrid(
    onGoToScan: () -> Unit,
    onGoToNutrition: () -> Unit,
    onGoToAr: () -> Unit,
    onGoToMarketplace: () -> Unit,
    onGoToCommunity: () -> Unit,
    onGoToHalocode: () -> Unit,
) {
    val features = listOf(
        Triple("OCR Scan", Icons.Default.QrCodeScanner, onGoToScan) to Color(0xFFE53935),
        Triple("Nutrisi", Icons.Default.MonitorHeart, onGoToNutrition) to Color(0xFF00897B),
        Triple("AR Finder", Icons.Default.Map, onGoToAr) to Color(0xFFF57C00),
        Triple("Marketplace", Icons.Default.Store, onGoToMarketplace) to Color(0xFF2E7D32),
        Triple("Komunitas", Icons.Default.Groups, onGoToCommunity) to Color(0xFF1976D2),
        Triple("Halocode", Icons.Default.LocalHospital, onGoToHalocode) to Color(0xFF6A1B9A),
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        userScrollEnabled = false,
        modifier = Modifier.height(220.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(features) { (feature, color) ->
            val (label, icon, action) = feature
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                onClick = action,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Surface(
                        color = color.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(44.dp),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
