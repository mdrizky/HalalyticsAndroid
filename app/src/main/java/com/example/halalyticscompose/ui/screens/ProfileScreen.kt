package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.example.halalyticscompose.R
import androidx.compose.ui.layout.ContentScale
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.ui.viewmodel.*

// Color constants moved into theme-aware components

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    contributionViewModel: ContributionViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val userData by authViewModel.userData.collectAsState()
    val totalScans by historyViewModel.totalScans.collectAsState()
    val halalProducts by historyViewModel.halalProducts.collectAsState()
    val currentStreak by historyViewModel.currentStreak.collectAsState()
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()
    val unreadNotificationCount by notificationViewModel.unreadCount.collectAsState()
    val pendingContributionCount by contributionViewModel.pendingCount.collectAsState()
    val approvedContributionCount by contributionViewModel.approvedCount.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.loadUserProfile()
        historyViewModel.refreshAll()
        notificationViewModel.loadNotifications()
        contributionViewModel.loadContributionStats()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                ProfileHeader(
                    userData = userData,
                    totalScans = totalScans,
                    halalProducts = halalProducts,
                    currentStreak = currentStreak,
                    navController = navController
                )
            }

            item {
                MenuSection(
                    title = stringResource(R.string.section_activity_contribution),
                    items = listOf(
                        MenuItem(stringResource(R.string.bottom_nav_history), Icons.Default.History, { navController.navigate("history") }),
                        MenuItem(stringResource(R.string.feature_my_contribution), Icons.Default.CloudUpload, { navController.navigate("contribution") }, badge = pendingContributionCount.takeIf { it > 0 }?.toString()),
                        MenuItem(stringResource(R.string.feature_favorite_list), Icons.Default.Favorite, { navController.navigate("favorites") })
                    )
                )
            }

            item {
                MenuSection(
                    title = stringResource(R.string.profile_settings),
                    items = listOf(
                        MenuItem(stringResource(R.string.notifications), Icons.Default.Notifications, { navController.navigate("notifications") }, badge = unreadNotificationCount.takeIf { it > 0 }?.toString()),
                        MenuItem(stringResource(R.string.dark_mode), Icons.Default.DarkMode, { mainViewModel.toggleDarkMode() }, isSwitch = true, switchState = isDarkMode),
                        MenuItem(stringResource(R.string.settings_title), Icons.Default.Settings, { navController.navigate("settings") })
                    )
                )
            }

            item {
                MenuSection(
                    title = stringResource(R.string.section_account),
                    items = listOf(
                        MenuItem(stringResource(R.string.feature_edit_profile), Icons.Default.Edit, { navController.navigate("edit_profile") }),
                        MenuItem(stringResource(R.string.feature_logout_label), Icons.AutoMirrored.Filled.Logout, { 
                            authViewModel.logout {
                                navController.navigate("login") { popUpTo(0) { inclusive = true } }
                            }
                        }, color = MaterialTheme.colorScheme.error)
                    )
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ProfileHeader(
    userData: com.example.halalyticscompose.data.model.User?,
    totalScans: Int,
    halalProducts: Int,
    currentStreak: Int,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(
                MaterialTheme.colorScheme.primary, 
                MaterialTheme.colorScheme.primaryContainer, 
                MaterialTheme.colorScheme.secondaryContainer
            )))
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
            ) {
                if (!userData?.image.isNullOrEmpty()) {
                    AsyncImage(model = userData?.image, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.fillMaxSize().padding(20.dp), tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(userData?.fullName ?: stringResource(R.string.account_default_user), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text(userData?.email ?: "", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatHeaderItem(stringResource(R.string.home_stats_scan), totalScans.toString())
                StatHeaderItem(stringResource(R.string.home_stats_halal), halalProducts.toString())
                StatHeaderItem(stringResource(R.string.streak), "$currentStreak " + stringResource(R.string.profile_streak_day).replace("%1\$d ", ""))
            }
        }
    }
}

@Composable
fun StatHeaderItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun MenuSection(title: String, items: List<MenuItem>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { item.onClick() }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(item.icon, contentDescription = null, tint = item.color ?: MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(item.title, modifier = Modifier.weight(1f), color = item.color ?: MaterialTheme.colorScheme.onSurface)
                        
                        if (item.badge != null) {
                            Badge { Text(item.badge) }
                        }
                        
                        if (item.isSwitch) {
                            Switch(checked = item.switchState, onCheckedChange = { item.onClick() })
                        } else {
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (index < items.size - 1) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val isSwitch: Boolean = false,
    val switchState: Boolean = false,
    val badge: String? = null,
    val color: Color? = null
)
