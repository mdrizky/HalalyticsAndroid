package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.halalyticscompose.Data.Model.LoginModel
import com.example.halalyticscompose.ui.theme.MushboohYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileStatusScreen(
    user: LoginModel.LoginContent? = null,
    goal: String? = null,
    dietPreference: String? = null,
    activityLevel: String? = null,
    address: String? = null,
    language: String? = null,
    bmi: Float? = null,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Status Profil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // BMI Card
                bmi?.let { bmiValue ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Indeks Massa Tubuh (BMI)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = String.format("%.1f", bmiValue),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    bmiValue < 18.5 -> MaterialTheme.colorScheme.tertiary
                                    bmiValue < 25 -> MaterialTheme.colorScheme.primary
                                    bmiValue < 30 -> MushboohYellow
                                    else -> MaterialTheme.colorScheme.error
                                }
                            )
                            Text(
                                text = when {
                                    bmiValue < 18.5 -> "Kurus"
                                    bmiValue < 25 -> "Normal"
                                    bmiValue < 30 -> "Berlebih"
                                    else -> "Obesitas"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                // Profile Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Informasi Profil",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Goal
                        goal?.let {
                            ProfileStatusItem(
                                icon = Icons.Default.Flag,
                                title = "Tujuan",
                                value = it
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        // Diet Preference
                        dietPreference?.let {
                            ProfileStatusItem(
                                icon = Icons.Default.Restaurant,
                                title = "Preferensi Diet",
                                value = it
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        // Activity Level
                        activityLevel?.let {
                            ProfileStatusItem(
                                icon = Icons.AutoMirrored.Filled.DirectionsRun,
                                title = "Level Aktivitas",
                                value = it
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        // Address
                        address?.let {
                            ProfileStatusItem(
                                icon = Icons.Default.LocationOn,
                                title = "Alamat",
                                value = it
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        // Language
                        language?.let {
                            ProfileStatusItem(
                                icon = Icons.Default.Language,
                                title = "Bahasa",
                                value = it
                            )
                        }
                    }
                }
            }

            item {
                // Stats Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Statistik Scan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                title = "Total Scan",
                                value = (user?.total_scan ?: 0).toString(),
                                color = MaterialTheme.colorScheme.primary
                            )
                            StatItem(
                                title = "Halal",
                                value = (user?.halal_count ?: 0).toString(),
                                color = MaterialTheme.colorScheme.primary
                            )
                            StatItem(
                                title = "Syubhat",
                                value = (user?.syubhat_count ?: 0).toString(),
                                color = MushboohYellow
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        user?.streak?.let { streak ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                StatItem(
                                    title = "Hari Beruntun",
                                    value = streak.toString(),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileStatusItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
