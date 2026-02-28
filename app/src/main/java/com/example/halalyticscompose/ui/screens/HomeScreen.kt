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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.halalyticscompose.Data.Model.Banner
import com.example.halalyticscompose.R
import com.example.halalyticscompose.Data.Model.ScanHistoryItem
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.vector.ImageVector
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val scanHistory by viewModel.scanHistory.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val totalScans by viewModel.totalScans.collectAsState()
    val halalProducts by viewModel.halalProducts.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val bannersLastUpdated by viewModel.bannersLastUpdated.collectAsState()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsState()
    val color = MaterialTheme.colorScheme
    var showAllFeaturesSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30000)
            viewModel.fetchBanners()
        }
    }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..10 -> stringResource(R.string.home_greeting_morning)
        in 11..14 -> stringResource(R.string.home_greeting_noon)
        in 15..17 -> stringResource(R.string.home_greeting_afternoon)
        else -> stringResource(R.string.home_greeting_night)
    }

    Scaffold(containerColor = color.background) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HeaderSection(
                    greeting = greeting,
                    name = user ?: stringResource(R.string.home_default_user),
                    onNotification = { navController.navigate("notifications") },
                    onSearch = { navController.navigate("search_hub") }
                )
            }

            item {
                val updatedLabel = bannersLastUpdated?.let {
                    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    stringResource(R.string.home_updated_at, formatter.format(Date(it)))
                } ?: stringResource(R.string.home_updated_unknown)
                Text(
                    text = updatedLabel,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = color.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            item {
                PosterBanner(
                    banners = banners,
                    onClick = { banner ->
                        navigateByBannerAction(navController = navController, banner = banner)
                    }
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
                    onHealthSuite = { navController.navigate("health_suite_hub") },
                    onAssistant = { navController.navigate("health_assistant") },
                    onHalalSpecialist = { navController.navigate("halal_specialist") },
                    onDrugInteraction = { navController.navigate("drug_interaction") },
                    onLabAnalysis = { navController.navigate("lab_analysis") },
                    onAiReport = { navController.navigate("ai_report") },
                    onBpom = { navController.navigate("bpom_scanner") },
                    onMore = { showAllFeaturesSheet = true }
                )
            }

            item {
                HealthBulletinSection(
                    onSearchArticle = { navController.navigate("health_articles") },
                    onOpenArticle = { navController.navigate("health_articles") }
                )
            }

            item {
                PosterCarousel(
                    banners = banners,
                    onClick = { navController.navigate("health_articles") },
                    onBannerClick = { banner -> navigateByBannerAction(navController, banner) }
                )
            }

            item {
                SectionTitle(
                    stringResource(R.string.home_recent_scan),
                    stringResource(R.string.home_see_all)
                ) { navController.navigate("history") }
            }

            items(scanHistory.take(6)) { item ->
                RecentScanCard(item = item) {
                    if (item.id > 0) {
                        navController.navigate("scan_history_detail/${item.id}")
                    } else {
                        navController.navigate("history")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        if (showAllFeaturesSheet) {
            val features = remember {
                listOf(
                    FeatureAction("Scan Barcode", Icons.Default.QrCode2, "scan"),
                    FeatureAction("Scan Hub", Icons.Default.CameraAlt, "scan_hub"),
                    FeatureAction("Medical Records", Icons.Default.MedicalServices, "medical_records"),
                    FeatureAction("Medical Resume", Icons.Default.Description, "medical_resume"),
                    FeatureAction("Health Diary", Icons.Default.Edit, "health_diary"),
                    FeatureAction("Health Pass", Icons.Default.QrCode2, "health_pass"),
                    FeatureAction("Emergency P3K", Icons.Default.LocalHospital, "emergency_p3k"),
                    FeatureAction("Pantauan Tubuh", Icons.Default.MonitorHeart, "health_monitor"),
                    FeatureAction("Health Journey", Icons.Default.CalendarMonth, "health_journey"),
                    FeatureAction("Pharmacy", Icons.Default.Medication, "pharmacy"),
                    FeatureAction("Nutrition Scan", Icons.Default.CameraAlt, "nutrition_scanner"),
                    FeatureAction("Report Issue", Icons.Default.Description, "report_issue/0/General"),
                    FeatureAction("Intl Medicine", Icons.Default.MedicalServices, "international_medicine")
                )
            }

            ModalBottomSheet(
                onDismissRequest = { showAllFeaturesSheet = false },
                containerColor = color.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Semua Fitur",
                        color = color.onSurface,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    features.chunked(4).forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            rowItems.forEach { item ->
                                ActionButton(
                                    label = item.title,
                                    icon = item.icon,
                                    bg = color.primaryContainer.copy(alpha = 0.35f),
                                    iconTint = color.primary,
                                    onClick = {
                                        showAllFeaturesSheet = false
                                        navController.navigate(item.route)
                                    }
                                )
                            }
                            repeat(4 - rowItems.size) {
                                Spacer(modifier = Modifier.width(72.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun RealtimeSyncCard(
    unreadNotifications: Int,
    pendingRequests: Int,
    approvedRequests: Int,
    syncedAt: Long?
) {
    val color = MaterialTheme.colorScheme
    val syncedText = syncedAt?.let {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        "Realtime sync ${formatter.format(Date(it))}"
    } ?: "Realtime sync belum aktif"

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.surface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Status User-Admin", color = color.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Notif baru: $unreadNotifications", color = color.onSurfaceVariant, fontSize = 11.sp)
                Text("Request pending: $pendingRequests", color = color.onSurfaceVariant, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text("Request disetujui: $approvedRequests", color = color.onSurfaceVariant, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(syncedText, color = color.primary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HeaderSection(
    greeting: String,
    name: String,
    onNotification: () -> Unit,
    onSearch: () -> Unit
) {
    val color = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Brush.linearGradient(listOf(color.primary, color.secondary)))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(greeting, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text(name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
                HeaderIcon(Icons.Default.Notifications, onNotification)
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
                Text(stringResource(R.string.home_search_hint), color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
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
private fun PosterBanner(
    banners: List<Banner>,
    onClick: (Banner?) -> Unit
) {
    val color = MaterialTheme.colorScheme
    var index by remember { mutableStateOf(0) }
    val banner = banners.getOrNull(index)

    LaunchedEffect(banners) {
        if (banners.isEmpty()) return@LaunchedEffect
        while (true) {
            delay(4500)
            index = (index + 1) % banners.size
        }
    }

    val title = banner?.title ?: stringResource(R.string.home_banner_fallback_title)
    val subtitle = banner?.description ?: stringResource(R.string.home_banner_fallback_desc)
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(130.dp)
            .fillMaxWidth()
            .clickable { onClick(banner) },
        colors = CardDefaults.cardColors(containerColor = color.primaryContainer),
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
                    .background(color.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (!banner?.image.isNullOrBlank()) {
                    AsyncImage(
                        model = banner?.image,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.VerifiedUser, null, tint = color.primary)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = color.onPrimaryContainer, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, color = color.onPrimaryContainer.copy(alpha = 0.85f), fontSize = 11.sp)
            }
            Text(stringResource(R.string.home_see), color = color.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
private fun StatsRow(total: Int, halal: Int, warning: Int) {
    val color = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            stringResource(R.string.home_stats_scan),
            total.toString(),
            stringResource(R.string.home_stats_week),
            color.primaryContainer.copy(alpha = 0.35f),
            color.primary,
            Modifier.weight(1f)
        )
        StatCard(
            stringResource(R.string.home_stats_halal),
            halal.toString(),
            stringResource(R.string.home_stats_product),
            color.primaryContainer.copy(alpha = 0.35f),
            color.primary,
            Modifier.weight(1f)
        )
        StatCard(
            stringResource(R.string.home_stats_warning),
            warning.toString(),
            stringResource(R.string.home_stats_recheck),
            color.errorContainer.copy(alpha = 0.40f),
            color.error,
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(title: String, value: String, subtitle: String, bg: Color, fg: Color, modifier: Modifier) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(bg))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = fg, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(title, color = color.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = color.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}

@Composable
private fun QuickActions(
    onHealthSuite: () -> Unit,
    onAssistant: () -> Unit,
    onHalalSpecialist: () -> Unit,
    onDrugInteraction: () -> Unit,
    onLabAnalysis: () -> Unit,
    onAiReport: () -> Unit,
    onBpom: () -> Unit,
    onMore: () -> Unit
) {
    val color = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionTitle(stringResource(R.string.home_quick_action), null, null)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton(stringResource(R.string.home_action_suite), Icons.Default.HealthAndSafety, color.primaryContainer.copy(alpha = 0.35f), color.primary, onHealthSuite)
            ActionButton(stringResource(R.string.home_action_assistant), Icons.Default.SmartToy, color.secondaryContainer.copy(alpha = 0.35f), color.secondary, onAssistant)
            ActionButton(stringResource(R.string.home_action_halal), Icons.Default.VerifiedUser, color.tertiaryContainer.copy(alpha = 0.35f), Color(0xFF2E7D32), onHalalSpecialist)
            ActionButton("BPOM", Icons.Default.VerifiedUser, color.primaryContainer.copy(alpha = 0.35f), Color(0xFF1E40AF), onBpom)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton(stringResource(R.string.home_action_interaction), Icons.Default.MedicalServices, color.primaryContainer.copy(alpha = 0.35f), Color(0xFF7C3AED), onDrugInteraction)
            ActionButton(stringResource(R.string.home_action_lab), Icons.Default.Biotech, color.secondaryContainer.copy(alpha = 0.35f), Color(0xFF0F766E), onLabAnalysis)
            ActionButton(stringResource(R.string.home_action_report), Icons.Default.Summarize, color.tertiaryContainer.copy(alpha = 0.35f), Color(0xFFBE123C), onAiReport)
            ActionButton("Lainnya", Icons.Default.GridView, color.surfaceVariant, Color(0xFF374151), onMore)
        }
    }
}

private data class FeatureAction(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
private fun ActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    val color = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 10.sp,
            color = color.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PosterCarousel(
    banners: List<Banner>,
    onClick: () -> Unit,
    onBannerClick: (Banner) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionTitle(stringResource(R.string.home_poster_title), stringResource(R.string.home_see_all), onClick)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (banners.isEmpty()) {
                SmallPoster(tag = "PROMO", title = stringResource(R.string.home_banner_fallback_title), imageUrl = null, color = MaterialTheme.colorScheme.primary)
            } else {
                banners.sortedBy { it.position }.take(8).forEach { banner ->
                    SmallPoster(
                        tag = if (banner.position % 2 == 0) "EDUKASI" else "POSTER",
                        title = banner.title,
                        imageUrl = banner.image,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onBannerClick(banner) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthBulletinSection(
    onSearchArticle: () -> Unit,
    onOpenArticle: () -> Unit
) {
    val color = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Jelajahi Buletin Kesehatan",
            color = color.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "Kumpulan tips kesehatan dan informasi penyakit yang lengkap dan terpercaya.",
            color = color.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(color.surfaceVariant.copy(alpha = 0.6f))
                .clickable { onSearchArticle() }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = color.onSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cari artikel", color = color.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenArticle() },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = color.surface)
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1579684385127-1ef15d508118?auto=format&fit=crop&w=400&q=80",
                    contentDescription = "Artikel kesehatan",
                    modifier = Modifier
                        .size(width = 120.dp, height = 70.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Deteksi dini masalah kesehatan untuk mencegah kondisi berat.",
                    color = color.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SmallPoster(tag: String, title: String, imageUrl: String?, color: Color, onClick: () -> Unit = {}) {
    val scheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .width(190.dp)
            .height(96.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, tint = color)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tag, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    title,
                    color = scheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun navigateByBannerAction(
    navController: NavController,
    banner: Banner?
) {
    if (banner == null) {
        navController.navigate("bpom_scanner")
        return
    }

    val actionType = banner.action_type?.trim()?.lowercase()
    val actionValue = banner.action_value?.trim().orEmpty()

    val route = when (actionType) {
        "open_screen" -> actionValue.ifBlank { null }
        "open_news" -> if (actionValue.isNotBlank()) "health_article_detail/${Uri.encode(actionValue)}" else "health_articles"
        "open_poster" -> "health_articles"
        "open_search" -> "search_external"
        "open_scan" -> "scan_hub"
        "open_bpom" -> "bpom_scanner"
        "open_health_suite" -> "health_suite_hub"
        "open_product_external" -> actionValue.takeIf { it.isNotBlank() }?.let { "product_external_detail/$it" }
        "open_product_local" -> actionValue.takeIf { it.isNotBlank() }?.let { "product_detail/$it" }
        else -> when (banner.position) {
            1 -> "health_articles"
            2 -> "bpom_scanner"
            else -> "health_articles"
        }
    }

    if (!route.isNullOrBlank()) navController.navigate(route)
}

@Composable
private fun SectionTitle(title: String, action: String?, onAction: (() -> Unit)?) {
    val color = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = color.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        if (action != null && onAction != null) {
            Text(action, color = color.primary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.clickable { onAction() })
        }
    }
}

@Composable
private fun RecentScanCard(item: ScanHistoryItem, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme
    val status = (item.halalStatus ?: "unknown").lowercase()
    val statusColor = when (status) {
        "halal" -> color.primary
        "haram" -> color.error
        else -> color.tertiary
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color.surface),
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
                    .background(color.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("📦")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.productName ?: "Produk",
                    color = color.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(item.createdAt ?: "-", color = color.onSurfaceVariant, fontSize = 10.sp)
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
