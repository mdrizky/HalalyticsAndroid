package com.example.halalyticscompose.ui.screens

import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.halalyticscompose.Data.Model.Banner
import com.example.halalyticscompose.Data.Model.HealthArticleItem
import com.example.halalyticscompose.Data.Model.ScanHistoryItem
import com.example.halalyticscompose.R
import com.example.halalyticscompose.ui.viewmodel.HealthArticleViewModel
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.halalyticscompose.utils.toRelativeTime
import com.example.halalyticscompose.ui.components.ShimmerProductItem
import com.example.halalyticscompose.ui.components.ShimmerArticleItem
import com.example.halalyticscompose.ui.components.ShimmerBanner
import com.example.halalyticscompose.ui.components.ShimmerBentoGrid

// ═══════════════════════════════════════════════════════════════════
// COLOR CONSTANTS — Emerald Forest Premium Palette
// ═══════════════════════════════════════════════════════════════════
private val Navy = Color(0xFF004D40)        // Deep Emerald
private val NavyLight = Color(0xFF00695C)   // Slightly lighter emerald
private val Mint = Color(0xFF26A69A)        // Modern Mint
private val MintPale = Color(0xFFE0F2F1)   // Soft Sage
private val BgLight = Color(0xFFF4F9F8)    // Off-White Green
private val CardWhite = Color(0xFFFFFFFF)
private val BorderGray = Color(0xFFE0E0E0)
private val TextDark = Color(0xFF212121)
private val TextMedium = Color(0xFF757575)
private val TextLight = Color(0xFF9E9E9E)
private val GoldAccent = Color(0xFFD4AF37)  // Premium Gold

// ═══════════════════════════════════════════════════════════════════
// HOME SCREEN — Premium Emerald Forest (Halodoc-Level)
// ═══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
    articleViewModel: HealthArticleViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val user by viewModel.currentUser.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val totalScans by viewModel.totalScans.collectAsState()
    val halalProducts by viewModel.halalProducts.collectAsState()
    val streak by viewModel.currentStreak.collectAsState()
    val scanHistory by viewModel.scanHistory.collectAsState()
    val articles by articleViewModel.articles.collectAsState()
    val aiDailyInsight by viewModel.aiDailyInsight.collectAsState()
    val healthScoreData by viewModel.healthScoreData.collectAsState()
    val homeLoading by viewModel.isLoading.collectAsState()
    val homeError by viewModel.errorMessage.collectAsState()
    val articleLoading by articleViewModel.isLoading.collectAsState()
    val articleError by articleViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
        articleViewModel.loadArticles()
    }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..10 -> stringResource(R.string.home_greeting_morning)
        in 11..14 -> stringResource(R.string.home_greeting_noon)
        in 15..17 -> stringResource(R.string.home_greeting_afternoon)
        else -> stringResource(R.string.home_greeting_night)
    }

    Scaffold(
        containerColor = BgLight,
        floatingActionButton = {
            PulsatingFAB(onClick = { navController.navigate("health_assistant") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ─── NAVY HEADER + FLOATING QUICK ACTION CARD ────────
            item {
                Box {
                    // Navy header background
                    NavyHeader(
                        greeting = greeting,
                        name = user ?: stringResource(R.string.home_default_user),
                        onNotification = { navController.navigate("notifications") },
                        onSearch = { navController.navigate("search_hub") }
                    )

                    // Floating Quick Action Card (overlaps header and body)
                    FloatingQuickActionCard(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 92.dp)
                            .padding(horizontal = 20.dp),
                        onScan = { navController.navigate("scan") },
                        onHistory = { navController.navigate("history") },
                        onInsight = { navController.navigate("health_assistant") },
                        onPoints = { navController.navigate("health_journey") },
                        onMedicine = { navController.navigate("drug_interaction") },
                        onCosmetic = { navController.navigate("skincare_scanner") },
                        onBpom = { navController.navigate("bpom_scanner") },
                        onAllFeatures = { navController.navigate("all_features") }
                    )
                }
                // Spacer to account for the floating card overlap
                Spacer(modifier = Modifier.height(136.dp))
            }

            // ─── AUTO-SLIDING BANNER ─────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                when {
                    banners.isNotEmpty() -> {
                        AutoSlidingBanner(
                            banners = banners,
                            onClick = { banner -> navigateByBannerAction(navController, banner) }
                        )
                    }
                    homeLoading -> {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            ShimmerBanner()
                        }
                    }
                    else -> {
                        HomeStatusCard(
                            message = "Banner belum tersedia. Kamu tetap bisa lanjut pakai fitur utama Halalytics.",
                            tone = StatusTone.Info
                        )
                    }
                }
            }

            if (!homeError.isNullOrBlank() || (!articleError.isNullOrBlank() && articles.isEmpty())) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    HomeStatusCard(
                        message = homeError ?: articleError.orEmpty(),
                        tone = StatusTone.Warning
                    )
                }
            }

            // ─── BENTO GRID — Clean White Cards ──────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                BentoGrid(
                    totalScans = totalScans,
                    halalProducts = halalProducts,
                    aiDailyInsight = aiDailyInsight,
                    onAiInsight = { navController.navigate("health_assistant") }
                )
            }



            // ─── HEALTH ARTICLES ─────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(
                    "Artikel Kesehatan",
                    "Lihat Semua"
                ) { navController.navigate("health_articles") }
            }

            if (articleLoading && articles.isEmpty()) {
                items(3) { ShimmerArticleItem() }
            } else if (articles.isEmpty()) {
                item {
                    HomeStatusCard(
                        message = articleError ?: "Artikel belum tersedia sekarang. Coba lagi sebentar lagi.",
                        tone = StatusTone.Info
                    )
                }
            } else {
                items(articles.take(4)) { article ->
                    ArticleCard(
                        article = article,
                        onClick = {
                            val slug = article.slug ?: article.id
                            navController.navigate("health_article_detail/${Uri.encode(slug)}")
                        }
                    )
                }
            }


            item { Spacer(modifier = Modifier.height(120.dp)) }
        }

        // bottom sheet removed in favor of all_features full screen
    }
}

// ═══════════════════════════════════════════════════════════════════
// NAVY HEADER — GoPay / Dana Style (Flat solid Navy)
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun NavyHeader(
    greeting: String,
    name: String,
    onNotification: () -> Unit,
    onSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(
                Brush.linearGradient(
                    listOf(Navy, NavyLight)
                )
            )
            .padding(bottom = 84.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Row 1: Greeting + Notification
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        greeting,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { onNotification() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Row 2: Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { onSearch() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    stringResource(R.string.home_search_hint),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.CameraAlt,
                    null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// FLOATING QUICK ACTION CARD — Melayang di antara Header & Body
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun FloatingQuickActionCard(
    modifier: Modifier = Modifier,
    onScan: () -> Unit,
    onHistory: () -> Unit,
    onInsight: () -> Unit,
    onPoints: () -> Unit,
    onMedicine: () -> Unit = {},
    onCosmetic: () -> Unit = {},
    onBpom: () -> Unit = {},
    onAllFeatures: () -> Unit = {}
) {
    Card(
        modifier = modifier
                .fillMaxWidth()
                .zIndex(1f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Row 1: Scan AI, Cek Obat, Kosmetik, Halal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionIcon(Icons.Default.QrCode2, "Scan AI", Color(0xFF00695C), Color(0xFFE0F2F1), onScan)
                QuickActionIcon(Icons.Default.Medication, "Cek Obat", Color(0xFFD32F2F), Color(0xFFFFEBEE), onMedicine)
                QuickActionIcon(Icons.Default.AutoAwesome, "Kosmetik", Color(0xFF7B1FA2), Color(0xFFF3E5F5), onCosmetic)
                QuickActionIcon(Icons.Default.VerifiedUser, "Halal", Color(0xFFF57F17), Color(0xFFFFF8E1), onPoints)
            }
            // Row 2: BPOM, AI Assistant, Lainnya (All Features)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionIcon(Icons.Default.HealthAndSafety, "BPOM", Color(0xFF0277BD), Color(0xFFE1F5FE), onBpom)
                QuickActionIcon(Icons.Default.SmartToy, "AI Chat", Color(0xFF00695C), Color(0xFFE0F2F1), onInsight)
                QuickActionIcon(Icons.Default.GridView, "Lainnya", Color(0xFF546E7A), Color(0xFFECEFF1), onAllFeatures)
                // Placeholder to keep spacing consistent
                Spacer(modifier = Modifier.width(72.dp))
            }
        }
    }
}

@Composable
private fun QuickActionIcon(icon: ImageVector, label: String, tint: Color, bg: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextDark,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

private enum class StatusTone {
    Info,
    Warning,
}

@Composable
private fun HomeStatusCard(
    message: String,
    tone: StatusTone
) {
    val background = when (tone) {
        StatusTone.Info -> MintPale
        StatusTone.Warning -> Color(0xFFFFF3E0)
    }
    val foreground = when (tone) {
        StatusTone.Info -> Navy
        StatusTone.Warning -> Color(0xFFE65100)
    }
    val icon = when (tone) {
        StatusTone.Info -> Icons.Default.Description
        StatusTone.Warning -> Icons.Default.Warning
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = foreground, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                color = foreground,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// AUTO-SLIDING BANNER with HorizontalPager
// ═══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AutoSlidingBanner(
    banners: List<Banner>,
    onClick: (Banner?) -> Unit
) {
    if (banners.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState, banners) {
        if (banners.size > 1) {
            while (true) {
                delay(3000)
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            val banner = banners[page]
            BannerPage(banner = banner, onClick = { onClick(banner) })
        }

        // Page indicator dots
        if (banners.size > 1) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                banners.forEachIndexed { index, _ ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Navy
                                else Navy.copy(alpha = 0.2f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun BannerPage(banner: Banner, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() }
    ) {
        if (!banner.image.isNullOrBlank()) {
            AsyncImage(
                model = banner.image,
                contentDescription = banner.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Navy, Mint)
                        )
                    )
            )
        }

        // Subtle gradient overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                        startY = 60f
                    )
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    banner.title ?: "Halalytics",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    banner.description ?: "",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// BENTO GRID — Clean White Cards + Thin Borders
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun BentoGrid(
    totalScans: Int,
    halalProducts: Int,
    aiDailyInsight: String? = null,
    onAiInsight: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Health Dashboard",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Icon(
                Icons.Default.AutoAwesome,
                null,
                tint = Mint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Row 1: AI Daily Insight (full width)
        AiDailyInsightCard(
            totalScans = totalScans,
            halalProducts = halalProducts,
            insight = aiDailyInsight,
            onClick = onAiInsight
        )
    }
}

@Composable
private fun AiDailyInsightCard(
    totalScans: Int,
    halalProducts: Int,
    insight: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Navy, Color(0xFF00796B))
                    ),
                    RoundedCornerShape(18.dp)
                )
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Mint,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "AI Health Insight",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Gemini AI",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                val insightText = insight ?: when {
                    totalScans == 0 -> "Mulai scan produk hari ini untuk mendapatkan insight kesehatan yang dipersonalisasi untukmu."
                    halalProducts == totalScans -> "Semua $totalScans produk yang kamu scan hari ini berstatus halal. Terus pertahankan gaya hidup sehat!"
                    else -> "Dari $totalScans scan hari ini, $halalProducts produk halal. Pastikan selalu cek label sebelum konsumsi."
                }

                Text(
                    insightText,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Selengkapnya →",
                    color = Mint,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HealthScoreCard(
    score: Int,
    label: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Single color: Navy for progress arc
    val animatedProgress = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(score) {
        animatedProgress.animateTo(
            score / 100f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier
            .aspectRatio(0.85f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Health Score",
                color = TextMedium,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Circular progress — Navy only
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(90.dp)
                    .drawBehind {
                        val strokeWidth = 10.dp.toPx()
                        val arcSize = size.minDimension - strokeWidth
                        val topLeft = Offset(
                            (size.width - arcSize) / 2f,
                            (size.height - arcSize) / 2f
                        )
                        // Background arc (light gray)
                        drawArc(
                            color = BorderGray,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        // Progress arc — Navy
                        drawArc(
                            color = Navy,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress.value,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
            ) {
                Text(
                    "$score",
                    color = Navy,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            if (label != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    label,
                    color = TextMedium,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StreakCard(streak: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalFireDepartment,
                null,
                tint = Color(0xFFFF6D00),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    "$streak",
                    color = Color(0xFFFF6D00),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text(
                    "Day Streak",
                    color = TextMedium,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


// ═══════════════════════════════════════════════════════════════════
// QUICK ACTIONS — Unified Navy Icons on Light Background
// ═══════════════════════════════════════════════════════════════════

    onAiReport: () -> Unit,
    onBpom: () -> Unit,
    onMore: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        SectionTitle(stringResource(R.string.home_quick_action), null, null)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton(stringResource(R.string.home_action_suite), Icons.Default.HealthAndSafety, Color(0xFFE91E63), Color(0xFFFCE4EC), onClick = onHealthSuite)
            ActionButton(stringResource(R.string.home_action_assistant), Icons.Default.SmartToy, Color(0xFF7B1FA2), Color(0xFFF3E5F5), onClick = onAssistant)
            ActionButton(stringResource(R.string.home_action_halal), Icons.Default.VerifiedUser, Color(0xFF2E7D32), Color(0xFFE8F5E9), onClick = onHalalSpecialist)
            ActionButton("BPOM", Icons.Default.VerifiedUser, Color(0xFF004D40), Color(0xFFE0F2F1), onClick = onBpom)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton(stringResource(R.string.home_action_interaction), Icons.Default.MedicalServices, Color(0xFFD32F2F), Color(0xFFFFEBEE), onClick = onDrugInteraction)
            ActionButton(stringResource(R.string.home_action_report), Icons.Default.Summarize, Color(0xFFF57C00), Color(0xFFFFF3E0), onClick = onAiReport)
            ActionButton("Lainnya", Icons.Default.GridView, Color(0xFF455A64), Color(0xFFECEFF1), onClick = onMore)
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: ImageVector,
    tint: Color = Navy,
    bg: Color = Color(0xFFE0F2F1),
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 10.sp,
            color = TextDark,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// ARTICLE CARDS — Clean with Navy Category Badge
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ArticleCard(article: HealthArticleItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 90.dp, height = 68.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0F2F5))
            ) {
                if (!article.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = article.imageUrl,
                        contentDescription = article.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp),
                        tint = TextLight
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (!article.category.isNullOrBlank()) {
                    Text(
                        article.category.uppercase(),
                        color = Navy,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    article.title,
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
                if (!article.excerpt.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        article.excerpt,
                        color = TextMedium,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// PULSATING FAB — Navy Container
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun PulsatingFAB(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fab_scale"
    )

    FloatingActionButton(
        onClick = onClick,
        containerColor = Navy,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier
            .scale(scale)
            .size(58.dp),
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            pressedElevation = 16.dp
        )
    ) {
        Icon(
            Icons.Default.SmartToy,
            contentDescription = "AI Assistant",
            modifier = Modifier.size(26.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// RECENT SCAN CARD — Clean White
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun RecentScanCard(item: ScanHistoryItem, onClick: () -> Unit) {
    val status = (item.halalStatus ?: "unknown").lowercase()
    val statusColor = when (status) {
        "halal" -> Color(0xFF2E7D32)    // Professional green
        "haram" -> Color(0xFFD32F2F)    // Medical red
        else -> Color(0xFFF57C00)       // Deep amber
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 3.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0F2F5)),
                contentAlignment = Alignment.Center
            ) {
                if (!item.productImage.isNullOrBlank()) {
                    AsyncImage(
                        model = item.productImage,
                        contentDescription = item.productName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("📦", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.productName ?: "Produk",
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(item.createdAt.toRelativeTime(), color = TextLight, fontSize = 10.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(statusColor.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    status.uppercase(),
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// SECTION TITLE
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SectionTitle(title: String, action: String?, onAction: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        if (action != null && onAction != null) {
            Text(
                action,
                color = Navy,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onAction() }
            )
        }
    }
}


// ═══════════════════════════════════════════════════════════════════
// NAVIGATION HELPER
// ═══════════════════════════════════════════════════════════════════

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
