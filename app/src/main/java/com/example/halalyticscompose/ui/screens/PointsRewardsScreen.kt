package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsRewardsScreen(
    navController: NavController,
    totalPoints: Int = 0,
    pointsHistory: List<PointHistoryItem> = emptyList()
) {
    val level = getLevelInfo(totalPoints)
    val progress = if (level.nextThreshold != null) {
        ((totalPoints - level.minThreshold).toFloat() / (level.nextThreshold - level.minThreshold))
    } else 1f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Poin & Reward") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate("leaderboard") }) {
                        Text("🏆 Peringkat", fontSize = 13.sp)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Points Hero Card ──────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
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
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💎", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "%,d".format(totalPoints),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Total Poin",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Level badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(level.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Level ${level.name}",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Progress bar
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = Color(0xFFFFD54F),
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )

                            if (level.nextThreshold != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${level.nextThreshold - totalPoints} poin lagi ke ${level.nextLevelName}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // ── How to Earn ──────────────────────
            item {
                Text(
                    "Cara Dapat Poin",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            val earningWays = listOf(
                Triple("📷", "Scan barcode produk baru", "+5 poin"),
                Triple("📸", "OCR scan label", "+10 poin"),
                Triple("✍️", "Review produk", "+15 poin"),
                Triple("📦", "Kontribusi data produk", "+50 poin"),
                Triple("✅", "Kontribusi diverifikasi", "+100 poin"),
                Triple("🔥", "Login 7 hari berturut-turut", "+100 poin"),
                Triple("⭐", "Login 30 hari berturut-turut", "+500 poin"),
            )

            items(earningWays) { (emoji, desc, points) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = desc,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF26A69A).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = points,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00695C)
                            )
                        }
                    }
                }
            }

            // ── History ──────────────────────
            if (pointsHistory.isNotEmpty()) {
                item {
                    Text(
                        "Riwayat Poin",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(pointsHistory) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.description, fontSize = 14.sp)
                            Text(
                                item.date,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "+${item.points}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF26A69A)
                        )
                    }
                }
            }
        }
    }
}

data class PointHistoryItem(
    val points: Int,
    val description: String,
    val date: String,
    val source: String
)

data class LevelInfo(
    val name: String,
    val emoji: String,
    val minThreshold: Int,
    val nextThreshold: Int?,
    val nextLevelName: String?
)

fun getLevelInfo(points: Int): LevelInfo = when {
    points >= 10000 -> LevelInfo("Legenda", "💎", 10000, null, null)
    points >= 5000  -> LevelInfo("Master", "🥇", 5000, 10000, "Legenda")
    points >= 2000  -> LevelInfo("Ahli", "🟣", 2000, 5000, "Master")
    points >= 500   -> LevelInfo("Penjelajah", "🔵", 500, 2000, "Ahli")
    else            -> LevelInfo("Pemula", "🟢", 0, 500, "Penjelajah")
}
