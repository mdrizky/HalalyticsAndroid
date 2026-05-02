package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.data.model.LeaderboardMember
import com.example.halalyticscompose.ui.viewmodel.LeaderboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavController,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val leaders by viewModel.leaderboard.collectAsState()
    val myRankData by viewModel.myRank.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedPeriod by remember { mutableStateOf("monthly") }

    LaunchedEffect(selectedPeriod) {
        viewModel.loadLeaderboard(selectedPeriod)
        viewModel.loadMyRank(selectedPeriod)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏆 Leaderboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading && leaders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ── Period Toggle ──────────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("monthly" to "Bulan Ini", "all_time" to "Sepanjang Waktu").forEach { (key, label) ->
                            FilterChip(
                                selected = selectedPeriod == key,
                                onClick = { selectedPeriod = key },
                                label = { Text(label, fontSize = 13.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // ── My Rank Card ──────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF004D40), Color(0xFF26A69A))
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Peringkatmu",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "#${myRankData?.rank ?: "-"}",
                                        color = Color.White,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "Total Poin",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "%,d".format(myRankData?.totalPoints ?: 0),
                                        color = Color(0xFFFFD54F),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Top 3 Podium ──────────────────────
                if (leaders.size >= 3) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                Text("👑 TOP CONTRIBUTORS", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF00695C))
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // 2nd place
                                    PodiumEntry(leaders[1], height = 100, medalColor = Color(0xFFB0BEC5), label = "2nd")
                                    // 1st place
                                    PodiumEntry(leaders[0], height = 140, medalColor = Color(0xFFFFD54F), label = "1st", isWinner = true)
                                    // 3rd place
                                    PodiumEntry(leaders[2], height = 80, medalColor = Color(0xFFBCAAA4), label = "3rd")
                                }
                            }
                        }
                    }
                }

                // ── Full List ──────────────────────
                item {
                    Text(
                        "Kontributor Teratas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                itemsIndexed(leaders) { index, entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = if (entry.isCurrentUser) {
                            CardDefaults.cardColors(containerColor = Color(0xFF004D40).copy(alpha = 0.08f))
                        } else CardDefaults.cardColors()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank
                            Text(
                                text = when (index) {
                                    0 -> "🥇"
                                    1 -> "🥈"
                                    2 -> "🥉"
                                    else -> "#${index + 1}"
                                },
                                fontSize = if (index < 3) 24.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(40.dp),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Avatar placeholder
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF26A69A).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    entry.fullName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00695C)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entry.fullName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "@${entry.username} • ${entry.level}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "%,d".format(entry.totalPoints),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
                
                if (error != null) {
                    item {
                        Text(
                            text = error ?: "",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumEntry(
    entry: LeaderboardMember,
    height: Int,
    medalColor: Color,
    label: String,
    isWinner: Boolean = false
) {
    val size = if (isWinner) 64.dp else 52.dp
    val podiumColor = if (isWinner) Color(0xFF00695C) else Color(0xFF26A69A)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(size + 4.dp),
                shape = CircleShape,
                color = medalColor,
                shadowElevation = if (isWinner) 6.dp else 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        entry.fullName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isWinner) 22.sp else 18.sp,
                        color = podiumColor
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(22.dp),
                shape = CircleShape,
                color = medalColor,
                shadowElevation = 4.dp
            ) {
                Text(
                    label.take(1),
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            entry.fullName.split(" ").first(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = Color(0xFF37474F)
        )
        Text(
            "%,d pts".format(entry.totalPoints),
            fontSize = 11.sp,
            color = podiumColor,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(podiumColor, podiumColor.copy(alpha = 0.6f))
                    )
                )
        ) {
            Text(
                label,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
