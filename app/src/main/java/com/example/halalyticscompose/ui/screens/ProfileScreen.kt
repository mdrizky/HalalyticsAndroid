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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

    Scaffold(
        containerColor = color.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title), color = color.onSurface, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = color.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = color.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = color.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(color.primary, color.secondary)))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
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
                                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(30.dp))
                                }
                            }
                            Spacer(modifier = Modifier.size(12.dp))
                            Column {
                                Text(
                                    userData?.fullName ?: currentUser ?: "Pengguna",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    userData?.email ?: "Akun Halalytics",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MiniStat(stringResource(R.string.profile_total_scan), totalScans.toString())
                        MiniStat(stringResource(R.string.home_stats_halal), halalProducts.toString())
                        MiniStat(stringResource(R.string.streak), stringResource(R.string.profile_streak_day, currentStreak))
                    }
                }
            }

            item {
                RealtimeStatusCard(
                    unreadNotifications = unreadNotificationCount,
                    pendingRequests = pendingContributionCount,
                    approvedRequests = approvedContributionCount,
                    syncedAt = lastRealtimeSyncAt
                )
            }

            item {
                HealthSummaryCard(
                    allergy = userData?.allergy,
                    medicalHistory = userData?.medicalHistory,
                    height = userData?.height,
                    weight = userData?.weight
                )
            }

            item {
                Text(
                    text = stringResource(R.string.profile_settings),
                    color = color.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            item {
                SettingCard(
                    icon = Icons.Default.Settings,
                    title = stringResource(R.string.profile_manage_account),
                    subtitle = stringResource(R.string.profile_manage_account_desc)
                ) { navController.navigate("edit_profile") }
            }

            item {
                SettingCard(
                    icon = Icons.Default.Person,
                    title = "Edit Profile Lengkap",
                    subtitle = "Nama, foto, tinggi, berat, alergi, riwayat medis"
                ) { navController.navigate("edit_profile") }
            }

            item {
                SettingCard(
                    icon = Icons.Default.VerifiedUser,
                    title = "Status Profil",
                    subtitle = "Lihat kelengkapan profil dan status verifikasi data"
                ) { navController.navigate("profile_status") }
            }

            item {
                SettingCard(
                    icon = Icons.Default.PictureAsPdf,
                    title = if (isExporting) "Sedang Export..." else "Export Laporan Bulanan",
                    subtitle = "Unduh ringkasan halal dan analisa AI Anda"
                ) { 
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
            }

            item {
                SettingCard(
                    icon = Icons.Default.MedicalServices,
                    title = "Health & Allergy Summary",
                    subtitle = "Lihat ringkasan alergi dan medical history"
                ) { navController.navigate("account_management") }
            }

            item {
                SettingCard(
                    icon = Icons.Default.Search,
                    title = "Ingredient Watchlist",
                    subtitle = "Kelola bahan yang ingin dihindari"
                ) { navController.navigate("watchlist_editor") }
            }

            item {
                SettingCard(
                    icon = Icons.Default.Settings,
                    title = "Security & Privacy",
                    subtitle = "Biometrik, auto-logout, privacy mode"
                ) { navController.navigate("settings") }
            }

            item {
                ToggleCard(
                    icon = Icons.Default.DarkMode,
                    title = stringResource(R.string.profile_dark_mode),
                    subtitle = stringResource(R.string.profile_dark_mode_desc)
                    ,checked = isDarkMode,
                    onChecked = { viewModel.toggleDarkMode() }
                )
            }

            item {
                ToggleCard(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.notifications),
                    subtitle = stringResource(R.string.profile_notif_desc)
                    ,checked = isNotifEnabled,
                    onChecked = { viewModel.setNotifEnabled(it) }
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clickable { viewModel.logout(navController) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = color.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color.error.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = color.error)
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        Column {
                            Text(stringResource(R.string.profile_logout), color = color.error, fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.profile_logout_desc), color = color.onSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(14.dp)) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HealthSummaryCard(
    allergy: String?,
    medicalHistory: String?,
    height: Double?,
    weight: Double?
) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Health & Allergy Summary", color = color.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            Text("Alergi", fontSize = 12.sp, color = color.onSurfaceVariant)
            if (allergy.isNullOrBlank()) {
                Text("-", color = color.onSurfaceVariant, fontSize = 13.sp)
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    allergy.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { alg ->






                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = color.errorContainer,
                            modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = alg, 
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = color.onErrorContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text("Riwayat medis: ${medicalHistory?.ifBlank { "-" } ?: "-"}", color = color.onSurfaceVariant, fontSize = 13.sp)
            Text(
                "Tinggi/Berat: ${height?.let { "${it}cm" } ?: "-"} / ${weight?.let { "${it}kg" } ?: "-"}",
                color = color.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun RealtimeStatusCard(
    unreadNotifications: Int,
    pendingRequests: Int,
    approvedRequests: Int,
    syncedAt: Long?
) {
    val color = MaterialTheme.colorScheme
    val syncedText = syncedAt?.let {
        val formatter = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        "Last synced at ${formatter.format(java.util.Date(it))}"
    } ?: "Cloud Sync inactive"

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Cloud Sync",
                    tint = color.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cloud Sync Status", color = color.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Unread Notif: $unreadNotifications", color = color.onSurfaceVariant, fontSize = 12.sp)
                Text("Pending Req: $pendingRequests", color = color.onSurfaceVariant, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Approved Req: $approvedRequests", color = color.onSurfaceVariant, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (syncedAt != null) color.primary else color.error, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(syncedText, color = color.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    val color = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color.onSurface, fontWeight = FontWeight.ExtraBold)
        Text(label, color = color.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun SettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color.primary)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                Text(title, color = color.onSurface, fontWeight = FontWeight.Bold)
                Text(subtitle, color = color.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ToggleCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color.primary)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = color.onSurface, fontWeight = FontWeight.Bold)
                Text(subtitle, color = color.onSurfaceVariant, fontSize = 12.sp)
            }
            Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}
