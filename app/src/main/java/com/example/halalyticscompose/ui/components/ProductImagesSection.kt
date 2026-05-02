package com.example.halalyticscompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.halalyticscompose.Data.Model.ProductImage
import com.example.halalyticscompose.Data.Model.ProductImageResult
import com.example.halalyticscompose.R

@Composable
fun ProductImagesSection(
    imageResult: ProductImageResult,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        // Banner sumber foto
        when (imageResult.source) {
            "placeholder" -> SourceBanner(
                message = "Foto tidak ditemukan. Menampilkan placeholder.",
                color = MaterialTheme.colorScheme.errorContainer,
                textColor = MaterialTheme.colorScheme.onErrorContainer
            )
            "unsplash" -> SourceBanner(
                message = "Foto ilustrasi dari Unsplash (bukan foto produk asli).",
                color = MaterialTheme.colorScheme.secondaryContainer,
                textColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid foto
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 500.dp)
        ) {
            items(imageResult.images) { image ->
                ProductImageCard(image = image)
            }
        }
    }
}

@Composable
fun ProductImageCard(image: ProductImage) {
    Card(
        modifier = Modifier.aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Gunakan Coil untuk load gambar dengan fallback
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image.url)
                    .crossfade(true)
                    // .error(R.drawable.ic_placeholder)   // fallback lokal jika URL gagal
                    // .placeholder(R.drawable.ic_loading)
                    .build(),
                contentDescription = image.label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Label di bawah foto
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(4.dp)
            ) {
                Text(
                    text = image.label,
                    color = Color.White,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                image.credit?.let {
                    Text(
                        text = "📷 $it",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun SourceBanner(message: String, color: Color, textColor: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            color = textColor,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
