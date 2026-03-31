package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.Data.Model.*
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.ui.viewmodel.FoodScanViewModel
import com.example.halalyticscompose.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodAnalysisScreen(
    navController: NavController,
    foodId: Int,
    viewModel: FoodScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val color = MaterialTheme.colorScheme
    
    // Set auth token and load analysis
    LaunchedEffect(foodId) {
        viewModel.setAuthToken(sessionManager.getAuthToken() ?: "")
        viewModel.analyzeFood(foodId, null)
    }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val foodAnalysis by viewModel.foodAnalysis.collectAsState()
    val selectedPortion by viewModel.selectedPortion.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        foodAnalysis?.foodName ?: "Analisis Nutrisi",
                        fontWeight = FontWeight.Bold,
                        color = color.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = color.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = color.background
                )
            )
        },
        containerColor = color.background
    ) { paddingValues ->
        if (isLoading && foodAnalysis == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = HalalGreen)
            }
        } else if (foodAnalysis != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                foodAnalysis?.let { analysis ->
                    // Health Score Card
                    HealthScoreCard(
                        healthInfo = analysis.healthInfo,
                        viewModel = viewModel
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Portion Selector
                    PortionSelectorCard(
                        selectedPortion = selectedPortion,
                        servingSize = analysis.servingSize,
                        onPortionChange = { viewModel.updatePortion(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Nutrition Details
                    NutritionCard(nutrition = analysis.nutrition)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Halal Status Card
                    HalalInfoCard(
                        halalInfo = analysis.halalInfo,
                        viewModel = viewModel
                    )
                    
                    // Health Recommendations
                    analysis.healthInfo?.recommendations?.let { recommendations ->
                        if (recommendations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            RecommendationsCard(recommendations = recommendations)
                        }
                    }
                    
                    // Disclaimer
                    analysis.disclaimer?.let { disclaimer ->
                        Spacer(modifier = Modifier.height(16.dp))
                        DisclaimerCard(text = disclaimer)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } else {
            // Error state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Error,
                        contentDescription = null,
                        tint = HaramColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: "Gagal memuat data",
                        fontSize = 14.sp,
                        color = color.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun HealthScoreCard(
    healthInfo: FoodHealthInfo?,
    viewModel: FoodScanViewModel
) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Health Score",
                fontSize = 14.sp,
                color = color.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Circular score indicator
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(color.surfaceVariant)
                    .border(
                        width = 6.dp,
                        color = Color(viewModel.getHealthScoreColor(healthInfo?.score ?: 50)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${healthInfo?.score ?: 0}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(viewModel.getHealthScoreColor(healthInfo?.score ?: 50))
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = healthInfo?.category ?: "N/A",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(viewModel.getHealthScoreColor(healthInfo?.score ?: 50))
            )
            
            healthInfo?.notes?.let { notes ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notes,
                    fontSize = 12.sp,
                    color = color.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            
            // Health tags
            healthInfo?.tags?.let { tags ->
                if (tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MushboohColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tag.replace("_", " ").replaceFirstChar { it.uppercase() },
                                    fontSize = 11.sp,
                                    color = MushboohColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PortionSelectorCard(
    selectedPortion: Double,
    servingSize: ServingSize?,
    onPortionChange: (Double) -> Unit
) {
    val portions = listOf(0.5, 1.0, 1.5, 2.0)
    val color = MaterialTheme.colorScheme
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ukuran Porsi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color.onSurface
                )
                
                servingSize?.let {
                    Text(
                        text = it.description,
                        fontSize = 13.sp,
                        color = color.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                portions.forEach { portion ->
                    val isSelected = selectedPortion == portion
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) HalalGreen else color.surfaceVariant
                            )
                            .clickable { onPortionChange(portion) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${portion}x",
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) color.onPrimary else color.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionCard(nutrition: NutritionInfo?) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Informasi Nutrisi",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = color.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Calories (highlighted)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HalalGreen.copy(alpha = 0.1f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.LocalFireDepartment,
                        contentDescription = null,
                        tint = HalalGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Kalori",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = color.onSurface
                    )
                }
                Text(
                    text = "${nutrition?.calories ?: 0} kcal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HalalGreen
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Macro nutrients grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutrientItem(
                    modifier = Modifier.weight(1f),
                    label = "Protein",
                    value = "${nutrition?.protein ?: 0}g",
                    color = Color(0xFF42A5F5)
                )
                NutrientItem(
                    modifier = Modifier.weight(1f),
                    label = "Karbohidrat",
                    value = "${nutrition?.carbs ?: 0}g",
                    color = Color(0xFFFFCA28)
                )
                NutrientItem(
                    modifier = Modifier.weight(1f),
                    label = "Lemak",
                    value = "${nutrition?.fat ?: 0}g",
                    color = Color(0xFFEF5350)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Other nutrients
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutrientItem(
                    modifier = Modifier.weight(1f),
                    label = "Serat",
                    value = "${nutrition?.fiber ?: 0}g",
                    color = color.onSurfaceVariant
                )
                NutrientItem(
                    modifier = Modifier.weight(1f),
                    label = "Gula",
                    value = "${nutrition?.sugar ?: 0}g",
                    color = color.onSurfaceVariant
                )
                NutrientItem(
                    modifier = Modifier.weight(1f),
                    label = "Natrium",
                    value = "${(nutrition?.sodium ?: 0).toInt()}mg",
                    color = color.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NutrientItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    val themeColor = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(themeColor.surfaceVariant)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = themeColor.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HalalInfoCard(
    halalInfo: FoodHalalInfo?,
    viewModel: FoodScanViewModel
) {
    val statusColor = Color(viewModel.getHalalStatusColor(halalInfo?.status ?: "halal_umum"))
    val color = MaterialTheme.colorScheme
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status Halal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color.onSurface
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = halalInfo?.statusLabel ?: "N/A",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
            
            halalInfo?.notes?.let { notes ->
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (halalInfo.status == "tergantung_bahan" || halalInfo.status == "syubhat")
                                MushboohColor.copy(alpha = 0.1f)
                            else
                                color.surfaceVariant
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        if (halalInfo.status == "halal_umum") 
                            Icons.Outlined.CheckCircle 
                        else 
                            Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = notes,
                        fontSize = 13.sp,
                        color = color.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationsCard(recommendations: List<String>) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = MushboohColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rekomendasi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            recommendations.forEach { recommendation ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        fontSize = 14.sp,
                        color = HalalGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = recommendation,
                        fontSize = 13.sp,
                        color = color.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DisclaimerCard(text: String) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = color.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                color = color.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
