package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.Data.Model.ScanHistoryItem
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import java.util.Calendar

private val Bg = Color(0xFFF7FAFC)
private val HeaderStart = Color(0xFF00C896)
private val HeaderEnd = Color(0xFF007D5A)
private val PosterBlue = Color(0xFF1B6CA8)
private val Primary = Color(0xFF00C896)
private val TextDark = Color(0xFF0A2540)
private val TextMuted = Color(0xFF64748B)
private val Warning = Color(0xFFF5A623)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val scanHistory by viewModel.scanHistory.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val totalScans by viewModel.totalScans.collectAsState()
    val halalProducts by viewModel.halalProducts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..10 -> "Assalamu'alaikum"
        in 11..14 -> "Selamat siang"
        in 15..17 -> "Selamat sore"
        else -> "Selamat malam"
    }

    Scaffold(containerColor = Bg) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HeaderSection(
                    greeting = greeting,
                    name = user ?: "Sahabat Halal",
                    onProfile = { navController.navigate("profile") },
                    onNotification = { navController.navigate("notifications") },
                    onSearch = { navController.navigate("search_hub") }
                )
            }

            item {
                PosterBanner(
                    title = "Produk Baru Terverifikasi",
                    subtitle = "Cek BPOM, halal, dan keamanan dalam 1 scan",
                    onClick = { navController.navigate("bpom_scanner") }
                )
            }

            item {
                StatsRow(
                    total = totalScans,
                    halal = halalProducts,
                    warning = (totalScans - halalProducts).coerceAtLeast(0)
                )
            }

            item {
                QuickActions(
                    onScan = { navController.navigate("scan_hub") },
                    onSearch = { navController.navigate("search_hub") },
                    onMedicine = { navController.navigate("international_medicine") },
                    onBpom = { navController.navigate("bpom_scanner") }
                )
            }

            item {
                PosterCarousel()
            }

            item {
                SectionTitle("Scan Terbaru", "Lihat semua") { navController.navigate("history") }
            }

            items(scanHistory.take(6)) { item ->
                RecentScanCard(item = item) {
                    navController.navigate("product_detail/${item.barcode ?: ""}")
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun HeaderSection(
    greeting: String,
    name: String,
    onProfile: () -> Unit,
    onNotification: () -> Unit,
    onSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Brush.linearGradient(listOf(HeaderStart, HeaderEnd)))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(greeting, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text(name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
                HeaderIcon(Icons.Default.Notifications, onNotification)
                Spacer(modifier = Modifier.width(8.dp))
                HeaderIcon(Icons.Default.Person, onProfile)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .clickable { onSearch() }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cari produk halal, obat, kosmetik...", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun HeaderIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun PosterBanner(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PosterBlue),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.VerifiedUser, null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
            }
            Text("Lihat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
private fun StatsRow(total: Int, halal: Int, warning: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard("Scan", total.toString(), "Minggu ini", Color(0xFFE8FBF5), Primary, Modifier.weight(1f))
        StatCard("Halal", halal.toString(), "Produk", Color(0xFFE8FBF5), Primary, Modifier.weight(1f))
        StatCard("Peringatan", warning.toString(), "Cek ulang", Color(0xFFFFF8E6), Warning, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(title: String, value: String, subtitle: String, bg: Color, fg: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(bg))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = fg, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(title, color = TextDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun QuickActions(
    onScan: () -> Unit,
    onSearch: () -> Unit,
    onMedicine: () -> Unit,
    onBpom: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionTitle("Aksi Cepat", null, null)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ActionButton("Scan", Icons.Default.CameraAlt, Color(0xFFE8FBF5), onScan)
            ActionButton("Cari", Icons.Default.Search, Color(0xFFE3F2FD), onSearch)
            ActionButton("Obat", Icons.Default.HealthAndSafety, Color(0xFFEDE7F6), onMedicine)
            ActionButton("BPOM", Icons.Default.VerifiedUser, Color(0xFFF0F5FF), onBpom)
        }
    }
}

@Composable
private fun ActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, bg: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = TextDark, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PosterCarousel() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionTitle("Poster & Edukasi", "Lihat semua") {}
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SmallPoster("PROMO", "Scan Gratis Tanpa Batas", Color(0xFFFF8E53))
            SmallPoster("EDUKASI", "Kenali E-Number", Color(0xFF9B59B6))
            SmallPoster("HALAL", "Database produk terverifikasi", Color(0xFF007D5A))
        }
    }
}

@Composable
private fun SmallPoster(tag: String, title: String, color: Color) {
    Card(
        modifier = Modifier
            .width(190.dp)
            .height(90.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(tag, color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun SectionTitle(title: String, action: String?, onAction: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        if (action != null && onAction != null) {
            Text(action, color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.clickable { onAction() })
        }
    }
}

@Composable
private fun RecentScanCard(item: ScanHistoryItem, onClick: () -> Unit) {
    val status = (item.halalStatus ?: "unknown").lowercase()
    val statusColor = when (status) {
        "halal" -> Primary
        "haram" -> Color(0xFFFF4757)
        else -> Warning
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Text("📦")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.productName ?: "Produk",
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(item.createdAt ?: "-", color = TextMuted, fontSize = 10.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(statusColor.copy(alpha = 0.14f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(status.uppercase(), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
