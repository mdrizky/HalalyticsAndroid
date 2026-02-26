package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.data.api.Ingredient
import com.example.halalyticscompose.ui.theme.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.EncyclopediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncyclopediaScreen(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: EncyclopediaViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    
    val ingredients by viewModel.ingredients.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchIngredients()
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ingredient Encyclopedia", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.padding(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurface.copy(0.05f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Section Premium
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.05f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it 
                        viewModel.searchIngredients(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search ingredients or E-numbers...", color = MaterialTheme.colorScheme.onSurface.copy(0.6f)) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchQuery = "" 
                                viewModel.searchIngredients("")
                            }) {
                                Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // Filters Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Halal", "Syubhat", "Haram").forEach { filter ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.05f), RoundedCornerShape(12.dp))
                            .clickable { 
                                selectedFilter = filter
                                viewModel.fetchIngredients(
                                    query = if (searchQuery.isEmpty()) null else searchQuery,
                                    status = if (filter == "All") null else filter.lowercase()
                                )
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            filter, 
                            color = if (selectedFilter == filter) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main List
            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
                } else if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                } else if (ingredients.isEmpty()) {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.1f), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No matching database entries", color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(ingredients) { ingredient ->
                            PremiumIngredientCard(
                                ingredient = ingredient,
                                onClick = { navController.navigate("ingredient_detail/${ingredient.id_ingredient}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumIngredientCard(ingredient: Ingredient, onClick: () -> Unit) {
    val statusColor = when (ingredient.halal_status.lowercase()) {
        "halal" -> MaterialTheme.colorScheme.primary
        "haram" -> MaterialTheme.colorScheme.error
        "syubhat" -> MushboohYellow
        else -> MaterialTheme.colorScheme.onSurface.copy(0.6f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.05f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(statusColor.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Science, null, tint = statusColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    ingredient.name, 
                    style = MaterialTheme.typography.titleMedium, 
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                if (ingredient.e_number != null) {
                    Text("E-Number: ${ingredient.e_number}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(0.1f))
                    .border(1.dp, statusColor.copy(0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    ingredient.halal_status.uppercase(), 
                    color = statusColor, 
                    fontWeight = FontWeight.Black, 
                    fontSize = 10.sp
                )
            }
        }
    }
}
