package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.IngredientDetailViewModel
import com.example.halalyticscompose.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun IngredientDetailScreen(
    ingredientId: Int,
    navController: NavController,
    viewModel: IngredientDetailViewModel = hiltViewModel()
) {
    val ingredient by viewModel.ingredient.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    LaunchedEffect(ingredientId) {
        viewModel.fetchIngredientDetail(ingredientId)
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scientific Data", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(8.dp).clip(CircleShape).background(Color.White.copy(0.05f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgDarkBase)
            )
        },
        containerColor = BgDarkBase
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Emerald500)
            } else if (error != null) {
                Column(modifier = Modifier.align(Alignment.Center).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Error, null, tint = HaramRed, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(error!!, color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { viewModel.fetchIngredientDetail(ingredientId) }, colors = ButtonDefaults.buttonColors(containerColor = Emerald500)) {
                        Text("Retry Connection", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (ingredient != null) {
                val data = ingredient!!
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    // Hero Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(Brush.linearGradient(listOf(Emerald600, BgDarkSurface)))
                            .padding(32.dp)
                    ) {
                        Column {
                            Text(
                                data.name, 
                                style = MaterialTheme.typography.headlineMedium, 
                                color = Color.White, 
                                fontWeight = FontWeight.Black
                            )
                            if (data.e_number != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(0.1f))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("E-NUMBER: ${data.e_number}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Status Analysis
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(BgDarkSurface)
                            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(28.dp))
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val statusColor = when(data.halal_status.lowercase()) {
                                "halal" -> Emerald500
                                "haram" -> HaramRed
                                else -> MushboohYellow
                            }
                            
                            Box(
                                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(statusColor.copy(0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if(data.halal_status.lowercase() == "halal") Icons.Default.Verified else Icons.Default.GppMaybe,
                                    null,
                                    tint = statusColor,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(20.dp))
                            
                            Column {
                                Text("REGULATORY STATUS", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text(data.halal_status.uppercase(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = statusColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Detail Sections
                    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        if (!data.description.isNullOrEmpty()) {
                            PremiumDetailSection(title = "Scientific Description", content = data.description!!, icon = Icons.Default.Science)
                        }
                        if (!data.sources.isNullOrEmpty()) {
                            PremiumDetailSection(title = "Origin & Sources", content = data.sources!!, icon = Icons.Default.Source)
                        }
                        if (!data.notes.isNullOrEmpty()) {
                            PremiumDetailSection(title = "Analysis Notes", content = data.notes!!, icon = Icons.Default.EventNote)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun PremiumDetailSection(title: String, content: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Emerald500, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(0.03f))
                .padding(20.dp)
        ) {
            Text(
                content, 
                style = MaterialTheme.typography.bodyMedium, 
                color = Color.White.copy(0.7f), 
                lineHeight = 24.sp
            )
        }
    }
}
