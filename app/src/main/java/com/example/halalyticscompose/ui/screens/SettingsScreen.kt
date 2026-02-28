package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.R
import com.example.halalyticscompose.ui.theme.HalalGreen
import com.example.halalyticscompose.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isNotifEnabled by viewModel.isNotifEnabled.collectAsState()
    
    val layoutDirection = if (appLanguage == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Appearance Section
                SettingsSectionTitle(stringResource(R.string.dark_mode))
                SettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = HalalGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(stringResource(R.string.dark_mode))
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() }
                        )
                    }
                }

                // Language Section
                SettingsSectionTitle(stringResource(R.string.language))
                SettingsCard {
                    Column(modifier = Modifier.padding(8.dp)) {
                        LanguageOption("id", "Indonesia", "🇮🇩", appLanguage) { viewModel.setAppLanguage("id") }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                        LanguageOption("en", "English", "🇺🇸", appLanguage) { viewModel.setAppLanguage("en") }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                        LanguageOption("ms", "Melayu", "🇲🇾", appLanguage) { viewModel.setAppLanguage("ms") }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                        LanguageOption("ar", "العربية", "🇸🇦", appLanguage) { viewModel.setAppLanguage("ar") }
                    }
                }

                // Notifications Section
                SettingsSectionTitle(stringResource(R.string.notifications))
                SettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = HalalGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(stringResource(R.string.notifications))
                        }
                        Switch(
                            checked = isNotifEnabled,
                            onCheckedChange = { viewModel.setNotifEnabled(it) }
                        )
                    }
                }

                // Watchlist Section (Quick implement)
                SettingsSectionTitle(stringResource(R.string.watchlist))
                SettingsCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("watchlist_editor") }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RemoveRedEye, contentDescription = null, tint = HalalGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(stringResource(R.string.watchlist))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }

                // About Section
                SettingsSectionTitle("About Halalytics")
                SettingsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Halalytics v2.5.0 Premium", fontWeight = FontWeight.Bold, color = HalalGreen)
                        Text("Advanced AI-Powered Halal & Health Analyzer for a better lifestyle.", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("© 2026 DeepMind Agentics Team", fontSize = 12.sp, color = androidx.compose.ui.graphics.Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = HalalGreen,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

@Composable
fun LanguageOption(
    code: String,
    name: String,
    flag: String,
    currentCode: String,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelect() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(flag, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(name, fontWeight = if (code == currentCode) FontWeight.Bold else FontWeight.Normal)
        }
        RadioButton(
            selected = code == currentCode,
            onClick = { onSelect() },
            colors = RadioButtonDefaults.colors(selectedColor = HalalGreen)
        )
    }
}
