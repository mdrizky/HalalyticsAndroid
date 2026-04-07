package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.presentation.viewmodel.RecipeViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    onNavigateBack: () -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.recipe?.title ?: "Recipe Detail", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1565C0))
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                item {
                    AsyncImage(
                        model = uiState.recipe?.imagePath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                uiState.recipe?.title ?: "",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { viewModel.applyHalalSwitch(recipeId) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Halal Switch", fontSize = 12.sp)
                            }
                        }

                        if (uiState.isSubstituting) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Ingredients", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                items(uiState.recipe?.ingredients ?: emptyList()) { ingredient ->
                    val substitution = uiState.substitution?.data?.ingredients?.find { it.original.contains(ingredient.name, true) }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (substitution != null && substitution.status == "haram") Color(0xFFFFEBEE) else Color.White
                        )
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (substitution != null && substitution.status == "haram") Icons.Default.Warning else Icons.Default.Eco,
                                contentDescription = null,
                                tint = if (substitution != null && substitution.status == "haram") Color.Red else Color(0xFF43A047)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(ingredient.name, fontWeight = FontWeight.Bold)
                                if (substitution != null && substitution.halalSubstitute != null) {
                                    Text("Substitute: ${substitution.halalSubstitute}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                                    Text(substitution.reason ?: "", fontSize = 11.sp, color = Color.Gray)
                                } else {
                                    Text("${ingredient.amount ?: ""} ${ingredient.unit ?: ""}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cooking Steps", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        uiState.recipe?.steps?.forEachIndexed { index, step ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text("${index + 1}.", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(step)
                            }
                        }
                    }
                }
            }
        }
    }
}
