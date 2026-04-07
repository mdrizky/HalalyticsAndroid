package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.presentation.viewmodel.NutritionViewModel
import coil.compose.AsyncImage

@Composable
fun NutritionScreen(
    onNavigateBack: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Nutrition", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00BFA5))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Open Camera to Log Meal */ },
                containerColor = Color(0xFF00BFA5),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Meal")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Calories Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Progress", fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterCenter) {
                            Text("${uiState.data?.totalCalories ?: 0}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Eaten", color = Color.Gray, fontSize = 12.sp)
                        }
                        Divider(modifier = Modifier.width(1.dp).height(40.dp))
                        Column(horizontalAlignment = Alignment.CenterCenter) {
                            Text("${uiState.data?.goal?.dailyCalories ?: 2100}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Goal", color = Color.Gray, fontSize = 12.sp)
                        }
                        Divider(modifier = Modifier.width(1.dp).height(40.dp))
                        Column(horizontalAlignment = Alignment.CenterCenter) {
                            val remaining = (uiState.data?.goal?.dailyCalories ?: 2100) - (uiState.data?.totalCalories ?: 0)
                            Text("$remaining", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = if (remaining < 0) Color.Red else Color(0xFF00BFA5))
                            Text("Remaining", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Meal History", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.data?.logs?.isEmpty() == true) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No meals logged today", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.data?.logs ?: emptyList()) { log ->
                        MealLogItem(log = log)
                    }
                }
            }
        }
    }
}

@Composable
fun MealLogItem(log: com.example.halalyticscompose.Data.Model.DailyNutritionLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            ) {
                if (log.imagePath != null) {
                    AsyncImage(model = log.imagePath, contentDescription = null)
                } else {
                    Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.align(Alignment.Center))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val title = log.foodItems?.joinToString(", ") { it.name } ?: log.mealType
                Text(title, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(log.loggedAt, fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${log.totalCalories} kcal", fontWeight = FontWeight.Bold, color = Color(0xFF00BFA5))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Halal", fontSize = 11.sp, color = Color(0xFF4CAF50))
                }
            }
        }
    }
}
