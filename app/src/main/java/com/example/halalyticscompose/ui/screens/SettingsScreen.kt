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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.halalyticscompose.R
import com.example.halalyticscompose.ui.theme.HalalGreen
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import com.example.halalyticscompose.utils.BiometricAuthHelper
import com.example.halalyticscompose.utils.CrashReporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isNotifEnabled by viewModel.isNotifEnabled.collectAsState()
    val privacyModeEnabled by viewModel.privacyModeEnabled.collectAsState()
    val biometricLockEnabled by viewModel.biometricLockEnabled.collectAsState()
    val autoLogoutEnabled by viewModel.autoLogoutEnabled.collectAsState()
    val autoLogoutMinutes by viewModel.autoLogoutMinutes.collectAsState()
    var crashInfo by remember { mutableStateOf(CrashReporter.getLastCrash(context)) }
    var biometricNotice by remember { mutableStateOf<String?>(null) }
    
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

                // Security Section
                SettingsSectionTitle("Security")
                SettingsCard {
                    Column(modifier = Modifier.padding(8.dp)) {
                        SettingsSwitchRow(
                            title = "Privacy Mode (anti screenshot)",
                            icon = Icons.Default.Shield,
                            checked = privacyModeEnabled,
                            onCheckedChange = viewModel::setPrivacyModeEnabled
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                        SettingsSwitchRow(
                            title = "Biometric Lock (medical screens)",
                            icon = Icons.Default.Fingerprint,
                            checked = biometricLockEnabled,
                            onCheckedChange = { enabled ->
                                if (!enabled) {
                                    biometricNotice = null
                                    viewModel.setBiometricLockEnabled(false)
                                } else {
                                    val activity = context as? FragmentActivity
                                    if (activity == null) {
                                        biometricNotice = "Biometrik tidak tersedia di perangkat ini."
                                        viewModel.setBiometricLockEnabled(false)
                                    } else if (!BiometricAuthHelper.canAuthenticate(activity)) {
                                        biometricNotice = "Biometrik belum aktif. Aktifkan sidik jari/face unlock di pengaturan perangkat."
                                        viewModel.setBiometricLockEnabled(false)
                                    } else {
                                        BiometricAuthHelper.authenticate(
                                            activity = activity,
                                            executor = ContextCompat.getMainExecutor(activity),
                                            title = "Aktifkan Biometric Lock",
                                            subtitle = "Lindungi halaman medis sensitif",
                                            description = "Konfirmasi identitas untuk mengaktifkan kunci biometrik",
                                            onSuccess = {
                                                biometricNotice = "Biometric lock aktif."
                                                viewModel.setBiometricLockEnabled(true)
                                            },
                                            onError = { err ->
                                                biometricNotice = err
                                                viewModel.setBiometricLockEnabled(false)
                                            }
                                        )
                                    }
                                }
                            }
                        )
                        biometricNotice?.let {
                            Text(
                                text = it,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
                        SettingsSwitchRow(
                            title = "Auto Logout",
                            icon = Icons.Default.Timer,
                            checked = autoLogoutEnabled,
                            onCheckedChange = viewModel::setAutoLogoutEnabled
                        )

                        if (autoLogoutEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Timeout (minutes)",
                                modifier = Modifier.padding(horizontal = 12.dp),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5, 15, 30).forEach { minutes ->
                                    FilterChip(
                                        selected = autoLogoutMinutes == minutes,
                                        onClick = { viewModel.setAutoLogoutMinutes(minutes) },
                                        label = { Text("$minutes mnt") }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
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

                // Diagnostics Section (helps troubleshoot force-close without adb)
                SettingsSectionTitle("Crash Diagnostics")
                SettingsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (crashInfo.isNullOrBlank()) {
                            Text(
                                text = "No recorded crash.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Last crash captured on device:",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = crashInfo!!.take(650),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 12
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    CrashReporter.clearLastCrash(context)
                                    crashInfo = null
                                }
                            ) {
                                Text("Clear Crash Log")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = HalalGreen)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
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
