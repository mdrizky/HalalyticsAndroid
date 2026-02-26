package com.example.halalyticscompose.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.data.database.ProductHistoryEntity
import com.example.halalyticscompose.ui.viewmodel.HistoryViewModel
import com.example.halalyticscompose.ui.components.getHalalColor
import com.example.halalyticscompose.ui.components.getHalalColorDark
import com.example.halalyticscompose.ui.components.HalalColorIndicator
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.Icons

@Composable
fun RecentProductsSection(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val recentProducts by viewModel.getRecentProducts(5).collectAsState(initial = emptyList())
    
    if (recentProducts.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scan Terbaru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(
                    onClick = { navController.navigate("history") }
                ) {
                    Text("Lihat Semua")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(300.dp) // Limit height for home screen
            ) {
                items(
                    items = recentProducts,
                    key = { it.barcode }
                ) { product ->
                    CompactProductCard(
                        product = product,
                        onCardClick = { 
                            navController.navigate("product_detail/${product.barcode}")
                        },
                        onFavoriteClick = { 
                            viewModel.toggleFavorite(product.barcode)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CompactProductCard(
    product: ProductHistoryEntity,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = getHalalColor(product.status)
    
    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Indicator
            HalalColorIndicator(
                status = product.status,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = getHalalColorDark(product.status),
                    maxLines = 1
                )
                
                product.brand?.let { brand ->
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = getHalalColorDark(product.status).copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }
            
            // Favorite Button
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (product.isFavorite) {
                        androidx.compose.material.icons.Icons.Filled.Favorite
                    } else {
                        androidx.compose.material.icons.Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = if (product.isFavorite) "Hapus dari Favorite" else "Tambah ke Favorite",
                    tint = if (product.isFavorite) androidx.compose.ui.graphics.Color.Red else getHalalColorDark(product.status),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
