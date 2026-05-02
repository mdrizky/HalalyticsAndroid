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
import com.example.halalyticscompose.ui.viewmodel.HistoryViewModel
import com.example.halalyticscompose.ui.components.getHalalColor
import com.example.halalyticscompose.ui.components.getHalalColorDark
import com.example.halalyticscompose.ui.components.HalalColorIndicator
import com.example.halalyticscompose.data.model.ScanHistoryItem

@Composable
fun RecentProductsSection(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val scanHistory by viewModel.scanHistory.collectAsState()
    val recentProducts = scanHistory.take(5)

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
                    key = { it.barcode ?: it.id.toString() }
                ) { product ->
                    CompactHistoryCard(
                        product = product,
                        onCardClick = {
                            product.barcode?.let { barcode ->
                                navController.navigate("product_detail/$barcode")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CompactHistoryCard(
    product: ScanHistoryItem,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = product.halalStatus ?: "Unknown"
    val backgroundColor = getHalalColor(status)

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
                status = status,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.productName ?: "Produk Tidak Diketahui",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = getHalalColorDark(status),
                    maxLines = 1
                )

                product.barcode?.let { barcode ->
                    Text(
                        text = "Barcode: $barcode",
                        style = MaterialTheme.typography.bodySmall,
                        color = getHalalColorDark(status).copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
