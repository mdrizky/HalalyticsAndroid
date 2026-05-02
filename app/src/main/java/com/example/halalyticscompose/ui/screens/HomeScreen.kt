package com.example.halalyticscompose.ui.screens

import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.halalyticscompose.data.model.*
import com.example.halalyticscompose.R
import com.example.halalyticscompose.ui.viewmodel.*
import com.example.halalyticscompose.utils.toRelativeTime
import com.example.halalyticscompose.ui.components.*
import java.util.Calendar
import kotlinx.coroutines.delay

// Color constants moved into the theme-aware components

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel(),
    healthViewModel: HealthViewModel = hiltViewModel(),
    articleViewModel: HealthArticleViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val userData by authViewModel.userData.collectAsState()
    val name = userData?.fullName ?: userData?.username ?: "User"
    val banners by historyViewModel.banners.collectAsState()
    val totalScans by historyViewModel.totalScans.collectAsState()
    val halalProducts by historyViewModel.halalProducts.collectAsState()
    val healthScore by historyViewModel.dailyHealthScore.collectAsState() 
    val streak by historyViewModel.currentStreak.collectAsState()
    val scanHistory by historyViewModel.scanHistory.collectAsState()
    val articles by articleViewModel.articles.collectAsState()
    val aiDailyInsight by healthViewModel.aiDailyInsight.collectAsState()
    
    var showAllFeaturesSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.loadUserProfile()
        historyViewModel.refreshAll()
        healthViewModel.refreshHealthData()
        articleViewModel.loadArticles()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            PulsatingFAB(onClick = { navController.navigate("health_assistant") })
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                GroceryHeader(
                    name = name,
                    imageUrl = userData?.image,
                    location = userData?.bio?.take(20) ?: "Jakarta, ID",
                    onNotification = { navController.navigate("notifications") }
                )
            }

            item {
                GrocerySearchBar(
                    onClick = { navController.navigate("search_hub") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                AutoSlidingBanner(
                    banners = banners,
                    onClick = { banner -> navigateByBannerAction(navController, banner) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                FeatureGridSS(
                    onScan = { navController.navigate("scan") },
                    onHistory = { navController.navigate("history") },
                    onInsight = { navController.navigate("health_assistant") },
                    onPoints = { navController.navigate("health_journey") },
                    onMedicine = { navController.navigate("drug_interaction") },
                    onCosmetic = { navController.navigate("skincare_scanner") },
                    onBpom = { navController.navigate("bpom_scanner") },
                    onLainnya = { showAllFeaturesSheet = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { navController.navigate("donor_home") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3F3)),
                    border = BorderStroke(1.dp, Color(0xFFE74C3C).copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE74C3C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.WaterDrop, null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Blood Donor Central", fontWeight = FontWeight.Bold, color = Color(0xFFE74C3C))
                            Text("Cek stok darah & event terdekat", fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.Default.ArrowForwardIos, null, modifier = Modifier.size(16.dp), tint = Color(0xFFE74C3C))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                GroceryCategorySection(onViewAll = { navController.navigate("search_hub") })
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                GroceryTopPicksSection(
                    products = scanHistory,
                    onViewAll = { navController.navigate("history") },
                    onProductClick = { item ->
                        if (item.id > 0) navController.navigate("scan_history_detail/${item.id}")
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle(stringResource(R.string.home_health_articles), stringResource(R.string.home_see_all)) { 
                    navController.navigate("health_articles") 
                }
            }

            if (articles.isEmpty()) {
                items(3) { ShimmerArticleItem() }
            } else {
                items(articles.take(4)) { article ->
                    ArticleCard(
                        article = article,
                        onClick = { 
                            val slug = article.slug ?: article.id.toString()
                            navController.navigate("health_article_detail/${Uri.encode(slug)}") 
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        if (showAllFeaturesSheet) {
            AllFeaturesSheet(
                onDismiss = { showAllFeaturesSheet = false },
                onNavigate = { route ->
                    showAllFeaturesSheet = false
                    navController.navigate(route)
                }
            )
        }
    }
}

@Composable
private fun GroceryHeader(name: String, imageUrl: String?, location: String, onNotification: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.Center))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.home_greeting_generic) + ", $name", 
                color = MaterialTheme.colorScheme.onBackground, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(location, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .shadow(2.dp, RoundedCornerShape(12.dp))
                .clickable { onNotification() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun GrocerySearchBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(stringResource(R.string.home_search_hint), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(
            Icons.Default.Mic, 
            null, 
            tint = MaterialTheme.colorScheme.primary, 
            modifier = Modifier.size(20.dp).clickable { /* Voice search implementation */ }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AutoSlidingBanner(banners: List<Banner>, onClick: (Banner?) -> Unit) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })
    LaunchedEffect(banners) {
        while (true) {
            delay(3000)
            if (banners.size > 1) {
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            val banner = banners[page]
            Box(modifier = Modifier.fillMaxSize().clickable { onClick(banner) }) {
                AsyncImage(
                    model = banner.image,
                    contentDescription = banner.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))).padding(16.dp)) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Text(banner.title ?: "", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(banner.description ?: "", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureGridSS(onScan: () -> Unit, onHistory: () -> Unit, onInsight: () -> Unit, onPoints: () -> Unit, onMedicine: () -> Unit, onCosmetic: () -> Unit, onBpom: () -> Unit, onLainnya: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FeatureItemSS(Icons.Default.QrCodeScanner, stringResource(R.string.feature_scan_ai), MaterialTheme.colorScheme.primaryContainer, onScan)
                FeatureItemSS(Icons.Default.Medication, stringResource(R.string.feature_cek_obat), MaterialTheme.colorScheme.secondaryContainer, onMedicine)
                FeatureItemSS(Icons.Default.AutoAwesome, stringResource(R.string.feature_kosmetik), MaterialTheme.colorScheme.tertiaryContainer, onCosmetic)
                FeatureItemSS(Icons.Default.VerifiedUser, stringResource(R.string.feature_halal_points), MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), onPoints)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FeatureItemSS(Icons.Default.HealthAndSafety, stringResource(R.string.feature_bpom_id), MaterialTheme.colorScheme.surfaceVariant, onBpom)
                FeatureItemSS(Icons.Default.Chat, stringResource(R.string.feature_ai_chat_id), MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f), onInsight)
                FeatureItemSS(Icons.Default.History, stringResource(R.string.feature_riwayat_id), MaterialTheme.colorScheme.surfaceVariant, onHistory)
                FeatureItemSS(Icons.Default.GridView, stringResource(R.string.feature_lainnya_id), MaterialTheme.colorScheme.surfaceVariant, onLainnya)
            }
        }
    }
}

@Composable
private fun FeatureItemSS(icon: ImageVector, label: String, bgColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(bgColor), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun GroceryCategorySection(onViewAll: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle(stringResource(R.string.home_categories), stringResource(R.string.home_see_all), onViewAll)
        Spacer(modifier = Modifier.height(12.dp))
        val categories = listOf(
            stringResource(R.string.search_hub_food) to Icons.Default.Restaurant, 
            stringResource(R.string.search_hub_medicine) to Icons.Default.LocalDrink, 
            stringResource(R.string.search_hub_medicine) to Icons.Default.Medication, 
            stringResource(R.string.home_meat_category) to Icons.Default.SetMeal
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(categories) { (name, icon) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onViewAll() }
                ) {
                    Box(modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun GroceryTopPicksSection(products: List<ScanHistoryItem>, onViewAll: () -> Unit, onProductClick: (ScanHistoryItem) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle(stringResource(R.string.home_recommendations), stringResource(R.string.home_see_all), onViewAll)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (products.isEmpty()) {
                items(3) { ShimmerProductItem() }
            } else {
                items(products.take(6)) { item ->
                    Card(modifier = Modifier.width(160.dp).clickable { onProductClick(item) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            AsyncImage(model = item.productImage, contentDescription = null, modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(item.productName ?: stringResource(R.string.home_stats_product), fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                            Text(item.halalStatus ?: "Halal", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(article: HealthArticleItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp).fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(width = 90.dp, height = 68.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                AsyncImage(model = article.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(article.category?.uppercase() ?: "KESEHATAN", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(article.title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun PulsatingFAB(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(initialValue = 1f, targetValue = 1.1f, animationSpec = infiniteRepeatable(animation = tween(1200), repeatMode = RepeatMode.Reverse))
    FloatingActionButton(onClick = onClick, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = CircleShape, modifier = Modifier.scale(scale).size(58.dp)) {
        Icon(Icons.Default.AutoAwesome, "AI", modifier = Modifier.size(26.dp))
    }
}

@Composable
private fun SectionTitle(title: String, action: String?, onAction: (() -> Unit)?) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        if (action != null && onAction != null) {
            Text(action, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.clickable { onAction() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllFeaturesSheet(onDismiss: () -> Unit, onNavigate: (String) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(stringResource(R.string.home_all_features_sheet), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            val items = listOf(
                stringResource(R.string.home_scan_barcode_sheet) to "scan", 
                stringResource(R.string.feature_riwayat_id) to "history", 
                stringResource(R.string.home_medicine_reminders_sheet) to "medicine_reminders", 
                stringResource(R.string.home_mental_quiz_sheet) to "mental_health_hub"
            )
            items.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    row.forEach { (label, route) ->
                        TextButton(
                            onClick = { onNavigate(route) }, 
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        ) { 
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = when(route) {
                                        "scan" -> Icons.Default.QrCodeScanner
                                        "history" -> Icons.Default.History
                                        "medicine_reminders" -> Icons.Default.NotificationsActive
                                        "mental_health_hub" -> Icons.Default.Psychology
                                        else -> Icons.Default.Star
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) 
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

private fun navigateByBannerAction(navController: NavController, banner: Banner?) {
    val route = when (banner?.action_type) {
        "open_screen" -> banner.action_value
        "open_news" -> "health_articles"
        else -> "scan_hub"
    }
    if (!route.isNullOrBlank()) navController.navigate(route)
}
