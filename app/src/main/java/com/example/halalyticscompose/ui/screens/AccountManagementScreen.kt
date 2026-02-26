package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import com.example.halalyticscompose.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val textColor = MaterialTheme.colorScheme.onSurface
    val cardColor = MaterialTheme.colorScheme.surface
    val isEnglish = appLanguage == "en"

    fun t(id: String): String = when (id) {
        "title" -> if (isEnglish) "Account Management" else "Manajemen Akun"
        "security_status" -> if (isEnglish) "Security Status" else "Status Keamanan"
        "verified" -> if (isEnglish) "Verified" else "Terverifikasi"
        "appearance" -> if (isEnglish) "Appearance" else "Tampilan"
        "dark_mode" -> if (isEnglish) "Dark Theme" else "Tema Gelap"
        "language" -> if (isEnglish) "Language" else "Bahasa"
        "danger" -> if (isEnglish) "Danger Zone" else "Zona Berbahaya"
        "delete_data" -> if (isEnglish) "Delete My Data" else "Hapus Data Saya"
        "logout" -> if (isEnglish) "Logout" else "Keluar Sekarang"
        else -> id
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("title"), color = textColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Section
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(cardColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = HalalGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentUser ?: "Pengguna",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Info Cards
            AccountInfoItem(
                label = "Username",
                value = currentUser ?: "-",
                icon = Icons.Default.Person
            )
            
            AccountInfoItem(
                label = t("security_status"),
                value = t("verified"),
                icon = Icons.Default.VerifiedUser,
                valueColor = HalalGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(t("appearance"), color = textColor, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(t("dark_mode"), color = textColor)
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() }
                        )
                    }
                    Text(t("language"), color = textColor, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = appLanguage == "id",
                            onClick = { viewModel.setAppLanguage("id") },
                            label = { Text("Indonesia") }
                        )
                        FilterChip(
                            selected = appLanguage == "en",
                            onClick = { viewModel.setAppLanguage("en") },
                            label = { Text("English") }
                        )
                        FilterChip(
                            selected = appLanguage == "ms",
                            onClick = { viewModel.setAppLanguage("ms") },
                            label = { Text("Melayu") }
                        )
                        FilterChip(
                            selected = appLanguage == "ar",
                            onClick = { viewModel.setAppLanguage("ar") },
                            label = { Text("العربية") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Danger Zone
            Text(
                text = t("danger"),
                modifier = Modifier.fillMaxWidth(),
                color = ErrorColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* Implement delete account or reset */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorColor.copy(alpha = 0.1f),
                    contentColor = ErrorColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(t("delete_data"), fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.logout(navController) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorColor
                ),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(t("logout"), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun AccountInfoItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = HalalGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = valueColor)
            }
        }
    }
}
