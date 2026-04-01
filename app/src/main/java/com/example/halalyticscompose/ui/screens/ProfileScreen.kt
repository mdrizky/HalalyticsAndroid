package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.R
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

// ═══════════════════════════════════════════════════════════════════
// COLOR CONSTANTS — Emerald Forest Premium
// ═══════════════════════════════════════════════════════════════════
private val EmeraldDark = Color(0xFF004D40)
private val EmeraldMedium = Color(0xFF00695C)
private val EmeraldLight = Color(0xFF26A69A)
private val SageBg = Color(0xFFF4F9F8)
private val SoftSage = Color(0xFFE0F2F1)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF212121)
private val TextMedium = Color(0xFF757575)
private val TextLight = Color(0xFF9E9E9E)
private val GoldAccent = Color(0xFFD4AF37)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userData by viewModel.userData.collectAsState()
    val totalScans by viewModel.totalScans.collectAsState()
    val halalProducts by viewModel.halalProducts.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isNotifEnabled by viewModel.isNotifEnabled.collectAsState()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsState()
    val pendingContributionCount by viewModel.pendingContributionCount.collectAsState()
    val approvedContributionCount by viewModel.approvedContributionCount.collectAsState()
    var lastRealtimeSyncAt by remember { mutableStateOf<Long?>(null) }
    var isExporting by remember { mutableStateOf(false) }
    val color = MaterialTheme.colorScheme
    val context = LocalContext.current

    Scaffold(containerColor = SageBg) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── EMERALD GRADIENT HEADER + AVATAR + STATS ──
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldDark, EmeraldMedium, EmeraldLight)
                            )
                        )
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        // Row: Back + Title + Settings
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { navController.navigateUp() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Text(
                                stringResource(R.string.profile_title),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { navController.navigate("settings") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val imageUrl = userData?.avatarUrl ?: userData?.image
                            if (!imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Initials
                                val name = userData?.fullName ?: currentUser ?: "User"
                                val initials = name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
                                Text(
                                    initials.ifEmpty { "U" },
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            userData?.fullName ?: currentUser ?: "Pengguna",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            userData?.email ?: "Akun Halalytics",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Stats Row
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp, horizontal = 10.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                MiniStat(totalScans.toString(), stringResource(R.string.profile_total_scan))
                                Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color.White.copy(alpha = 0.3f)))
                                MiniStat(halalProducts.toString(), stringResource(R.string.home_stats_halal))
                                Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color.White.copy(alpha = 0.3f)))
                                MiniStat(
                                    stringResource(R.string.profile_streak_day, currentStreak),
                                    stringResource(R.string.streak)
                                )
                            }
                        }
                    }
                }
            }

            // ── CLOUD SYNC STATUS ──
            item {
                RealtimeStatusCard(
                    unreadNotifications = unreadNotificationCount,
                    pendingRequests = pendingContributionCount,
                    approvedRequests = approvedContributionCount,
                    syncedAt = lastRealtimeSyncAt
                )
            }

            // ── HEALTH SUMMARY ──
            item {
                HealthSummaryCard(
                    allergy = userData?.allergy,
                    medicalHistory = userData?.medicalHistory,
                    height = userData?.height,
                    weight = userData?.weight
                )
            }

            // ── SECTION: KESEHATAN ──
            item {
                ProfileSectionHeader(
                    title = "Kesehatan",
                    icon = Icons.Default.Favorite,
                    iconBg = Color(0xFFE8F5E9),
                    iconTint = EmeraldDark
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.MonitorHeart,
                    title = "Health Profile",
                    subtitle = "Kelola data kesehatan dan alergi",
                    iconBg = SoftSage,
                    iconTint = EmeraldDark,
                    onClick = { navController.navigate("health_profile") }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.LocalFireDepartment,
                    title = "Health Diary",
                    subtitle = "Catatan kesehatan harian",
                    iconBg = Color(0xFFFFF3E0),
                    iconTint = Color(0xFFF57C00),
                    onClick = { navController.navigate("health_diary") }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.BarChart,
                    title = "Health Journey",
                    subtitle = "Track progress kesehatan Anda",
                    iconBg = Color(0xFFE0F2F1),
                    iconTint = Color(0xFF00695C),
                    onClick = { navController.navigate("health_journey") }
                )
            }

            // ── SECTION: AKUN ──
            item {
                ProfileSectionHeader(
                    title = "Akun & Privasi",
                    icon = Icons.Default.Shield,
                    iconBg = Color(0xFFE0F2F1),
                    iconTint = EmeraldDark
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = stringResource(R.string.profile_manage_account),
                    subtitle = stringResource(R.string.profile_manage_account_desc),
                    iconBg = SoftSage,
                    iconTint = EmeraldDark,
                    onClick = { navController.navigate("edit_profile") }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.VerifiedUser,
                    title = "Status Profil",
                    subtitle = "Kelengkapan profil dan verifikasi data",
                    iconBg = Color(0xFFFFF8E1),
                    iconTint = GoldAccent,
                    onClick = { navController.navigate("profile_status") }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.PictureAsPdf,
                    title = if (isExporting) "Sedang Export..." else "Export Laporan Bulanan",
                    subtitle = "Unduh ringkasan halal dan analisa AI Anda",
                    iconBg = Color(0xFFFFEBEE),
                    iconTint = Color(0xFFD32F2F),
                    onClick = {
                        if (!isExporting) {
                            isExporting = true
                            viewModel.exportReport { url ->
                                isExporting = false
                                if (url != null) {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                                    intent.data = android.net.Uri.parse(url)
                                    context.startActivity(intent)
                                } else {
                                    android.widget.Toast.makeText(context, "Gagal membuat laporan", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Search,
                    title = "Ingredient Watchlist",
                    subtitle = "Kelola bahan yang ingin dihindari",
                    iconBg = SoftSage,
                    iconTint = EmeraldMedium,
                    onClick = { navController.navigate("watchlist_editor") }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Security & Privacy",
                    subtitle = "Biometrik, auto-logout, privacy mode",
                    iconBg = Color(0xFFF3E5F5),
                    iconTint = Color(0xFF6A1B9A),
                    onClick = { navController.navigate("settings") }
                )
            }

            // ── SECTION: LAINNYA ──
            item {
                ProfileSectionHeader(
                    title = "Pengaturan",
                    icon = Icons.Default.Tune,
                    iconBg = Color(0xFFF0F4C3),
                    iconTint = Color(0xFF827717)
                )
            }

            item {
                ToggleCard(
                    icon = Icons.Default.DarkMode,
                    title = stringResource(R.string.profile_dark_mode),
                    subtitle = stringResource(R.string.profile_dark_mode_desc),
                    checked = isDarkMode,
                    onChecked = { viewModel.toggleDarkMode() }
                )
            }

            item {
                ToggleCard(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.notifications),
                    subtitle = stringResource(R.string.profile_notif_desc),
                    checked = isNotifEnabled,
                    onChecked = { viewModel.setNotifEnabled(it) }
                )
            }

            // ── LOGOUT ──
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clickable { viewModel.logout(navController) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(stringResource(R.string.profile_logout), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(stringResource(R.string.profile_logout_desc), color = TextMedium, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Version info
            item {
                Text(
                    "Halalytics v2.5.0 Premium",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    color = TextLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// PROFILE SECTION HEADER
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ProfileSectionHeader(
    title: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            title,
            color = TextDark,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// PROFILE MENU ITEM — Premium card with colored icon
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBg: Color = SoftSage,
    iconTint: Color = EmeraldDark,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = TextMedium, fontSize = 12.sp)
            }
            Icon(
                Icons.Default.ChevronRight,
                null,
                tint = TextLight,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// HEALTH SUMMARY CARD
// ═══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HealthSummaryCard(
    allergy: String?,
    medicalHistory: String?,
    height: Double?,
    weight: Double?
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Favorite, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Health & Allergy Summary", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))
            
            Text("Alergi", fontSize = 12.sp, color = TextMedium, fontWeight = FontWeight.Medium)
            if (allergy.isNullOrBlank()) {
                Text("Belum ada data alergi", color = TextLight, fontSize = 13.sp)
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    allergy.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { alg ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = alg,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                color = Color(0xFFD32F2F),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text("Riwayat medis: ${medicalHistory?.ifBlank { "-" } ?: "-"}", color = TextMedium, fontSize = 13.sp)
            Text(
                "Tinggi/Berat: ${height?.let { "${it}cm" } ?: "-"} / ${weight?.let { "${it}kg" } ?: "-"}",
                color = TextMedium,
                fontSize = 13.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// REALTIME STATUS CARD
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun RealtimeStatusCard(
    unreadNotifications: Int,
    pendingRequests: Int,
    approvedRequests: Int,
    syncedAt: Long?
) {
    val syncedText = syncedAt?.let {
        val formatter = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        "Last synced at ${formatter.format(java.util.Date(it))}"
    } ?: "Cloud Sync inactive"

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SoftSage.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldDark.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Cloud Sync",
                        tint = EmeraldDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cloud Sync Status", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Unread Notif: $unreadNotifications", color = TextMedium, fontSize = 12.sp)
                Text("Pending Req: $pendingRequests", color = TextMedium, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Approved Req: $approvedRequests", color = TextMedium, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (syncedAt != null) EmeraldDark else Color(0xFFD32F2F), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(syncedText, color = TextMedium, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// MINI STAT (for header stats row)
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun MiniStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Text(
            label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// SETTING / TOGGLE CARDS
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SettingCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldDark.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = EmeraldDark, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = TextMedium, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ToggleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldDark.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = EmeraldDark, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = TextMedium, fontSize = 12.sp)
            }
            Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}
