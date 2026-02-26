package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.viewmodel.MainViewModel

private val ProfileBg = Color(0xFFF7FAFC)
private val ProfilePrimary = Color(0xFF00C896)
private val ProfilePrimaryDark = Color(0xFF00A878)
private val ProfileText = Color(0xFF0A2540)
private val ProfileMuted = Color(0xFF64748B)
private val Danger = Color(0xFFFF4757)

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

    Scaffold(
        containerColor = ProfileBg,
        topBar = {
            TopAppBar(
                title = { Text("Profil", color = Color.White, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ProfilePrimary)
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
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(ProfilePrimary, ProfilePrimaryDark)))
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
                                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(30.dp))
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
                        MiniStat("Total Scan", totalScans.toString())
                        MiniStat("Halal", halalProducts.toString())
                        MiniStat("Streak", "$currentStreak hari")
                    }
                }
            }

            item {
                Text(
                    text = "Pengaturan",
                    color = ProfileText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            item {
                SettingCard(
                    icon = Icons.Default.Settings,
                    title = "Kelola Akun",
                    subtitle = "Edit profil dan data pribadi"
                ) { navController.navigate("account_management") }
            }

            item {
                ToggleCard(
                    icon = Icons.Default.DarkMode,
                    title = "Mode Gelap",
                    subtitle = "Tampilan aplikasi"
                    ,checked = isDarkMode,
                    onChecked = { viewModel.toggleDarkMode() }
                )
            }

            item {
                ToggleCard(
                    icon = Icons.Default.Notifications,
                    title = "Notifikasi",
                    subtitle = "Pengingat dan update"
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
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Danger.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Logout, null, tint = Danger)
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        Column {
                            Text("Keluar", color = Danger, fontWeight = FontWeight.Bold)
                            Text("Logout dari aplikasi", color = ProfileMuted, fontSize = 12.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(14.dp)) }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = ProfileText, fontWeight = FontWeight.ExtraBold)
        Text(label, color = ProfileMuted, fontSize = 11.sp)
    }
}

@Composable
private fun SettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ProfilePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = ProfilePrimary)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                Text(title, color = ProfileText, fontWeight = FontWeight.Bold)
                Text(subtitle, color = ProfileMuted, fontSize = 12.sp)
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
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ProfilePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = ProfilePrimary)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = ProfileText, fontWeight = FontWeight.Bold)
                Text(subtitle, color = ProfileMuted, fontSize = 12.sp)
            }
            Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}
