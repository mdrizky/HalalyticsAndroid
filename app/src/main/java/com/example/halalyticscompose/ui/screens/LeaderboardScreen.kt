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
import androidx.navigation.NavController

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val fullName: String,
    val totalPoints: Int,
    val level: String,
    val badge: String,
    val isCurrentUser: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavController,
    leaders: List<LeaderboardEntry> = emptyList(),
    myRank: Int = 0,
    myPoints: Int = 0,
    period: String = "monthly"
) {
    var selectedPeriod by remember { mutableStateOf(period) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏆 Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
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
                                    "#$myRank",
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
                                    "%,d".format(myPoints),
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 2nd place
                        PodiumEntry(leaders[1], height = 90, medal = "🥈")
                        // 1st place
                        PodiumEntry(leaders[0], height = 120, medal = "🥇")
                        // 3rd place
                        PodiumEntry(leaders[2], height = 70, medal = "🥉")
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
        }
    }
}

@Composable
private fun PodiumEntry(entry: LeaderboardEntry, height: Int, medal: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Text(medal, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF26A69A).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                entry.fullName.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF00695C)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            entry.fullName.split(" ").first(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
        Text(
            "%,d".format(entry.totalPoints),
            fontSize = 11.sp,
            color = Color(0xFF004D40),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF004D40).copy(alpha = 0.7f), Color(0xFF004D40).copy(alpha = 0.3f))
                    )
                )
        )
    }
}
