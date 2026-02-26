package com.example.halalyticscompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.halalyticscompose.data.database.ProductHistoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableProductCard(
    product: ProductHistoryEntity,
    onDelete: (ProductHistoryEntity) -> Unit,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(product)
                true
            } else {
                false
            }
        }
    )

    // Handle dismiss animation
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete(product)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // Background content yang muncul saat di-swipe
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Color.Red
                SwipeToDismissBoxValue.StartToEnd -> Color.Green
                else -> Color.Transparent
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    else -> Alignment.Center
                }
            ) {
                when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.EndToStart -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = Color.White
                            )
                            Text(
                                text = "Hapus",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    SwipeToDismissBoxValue.StartToEnd -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Favorite",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Icon(
                                imageVector = if (product.isFavorite) Icons.Default.Delete else Icons.Default.Delete,
                                contentDescription = if (product.isFavorite) "Hapus dari Favorite" else "Tambah ke Favorite",
                                tint = Color.White
                            )
                        }
                    }
                    else -> {}
                }
            }
        },
        content = {
            ProductHistoryCard(
                product = product,
                onFavoriteClick = onFavoriteClick,
                onCardClick = onCardClick,
                modifier = modifier
            )
        }
    )
}

@Composable
fun ProductHistoryCard(
    product: ProductHistoryEntity,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = getHalalColor(product.status)
    
    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image or Placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder icon atau gambar produk
                HalalColorIndicator(
                    status = product.status,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))
            
            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = getHalalColorDark(product.status)
                )
                
                product.brand?.let { brand ->
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = getHalalColorDark(product.status).copy(alpha = 0.7f)
                    )
                }
                
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                
                HalalStatusBadge(
                    status = product.status,
                    modifier = Modifier
                )
            }

            // Favorite Button
            androidx.compose.material3.IconButton(
                onClick = onFavoriteClick
            ) {
                Icon(
                    imageVector = if (product.isFavorite) {
                        androidx.compose.material.icons.Icons.Filled.Favorite
                    } else {
                        androidx.compose.material.icons.Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = if (product.isFavorite) "Hapus dari Favorite" else "Tambah ke Favorite",
                    tint = if (product.isFavorite) Color.Red else getHalalColorDark(product.status)
                )
            }
        }
    }
}
