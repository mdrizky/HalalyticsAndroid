package com.example.halalyticscompose.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.halalyticscompose.presentation.viewmodel.MobileSyncViewModel
import com.example.halalyticscompose.presentation.viewmodel.SyncState
import com.example.halalyticscompose.data.remote.AdminProduct
import com.example.halalyticscompose.data.remote.AdminCategory

/**
 * Screen for displaying synchronization status and managing sync operations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncStatusScreen(
    viewModel: MobileSyncViewModel,
    onNavigateBack: () -> Unit
) {
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
        viewModel.loadCategories()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sync Status") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sync Status Card
            SyncStatusCard(syncState = syncState)
            
            // Error Message
            errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Statistics
            StatisticsCards(
                productCount = products.size,
                categoryCount = categories.size,
                syncState = syncState
            )
            
            // Recent Products (if loaded)
            if (products.isNotEmpty()) {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Recent Products",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyColumn(
                            modifier = Modifier.height(200.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(products.take(5)) { product ->
                                ProductItem(product = product)
                            }
                        }
                    }
                }
            }
            
            // Categories Summary
            if (categories.isNotEmpty()) {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyColumn(
                            modifier = Modifier.height(150.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(categories.take(3)) { category ->
                                CategoryItem(category = category)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncStatusCard(syncState: SyncState) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (syncState) {
                is SyncState.Success -> MaterialTheme.colorScheme.primaryContainer
                is SyncState.Error -> MaterialTheme.colorScheme.errorContainer
                is SyncState.Syncing -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (syncState) {
                is SyncState.Idle -> {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ready to sync", style = MaterialTheme.typography.titleMedium)
                }
                
                is SyncState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading data...", style = MaterialTheme.typography.titleMedium)
                }
                
                is SyncState.Syncing -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Syncing...", style = MaterialTheme.typography.titleMedium)
                }
                
                is SyncState.Success -> {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sync Complete",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${syncState.result.syncedCount} items synced",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                is SyncState.Error -> {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sync Failed",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = syncState.message,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsCards(
    productCount: Int,
    categoryCount: Int,
    syncState: SyncState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = productCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Products",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Card(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = categoryCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ProductItem(product: AdminProduct) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.nama_product,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = product.barcode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Status badge
        Surface(
            color = when (product.status) {
                "halal" -> MaterialTheme.colorScheme.primary
                "tidak halal" -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.secondary
            },
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = product.status.replaceFirstChar { it.uppercase() },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun CategoryItem(category: AdminCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.nama_kategori,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = "${category.products_count} items",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
