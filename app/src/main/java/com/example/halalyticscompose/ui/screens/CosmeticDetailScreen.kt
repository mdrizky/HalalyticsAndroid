package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.halalyticscompose.Data.API.bestIngredientsText
import com.example.halalyticscompose.ui.viewmodel.SkincareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosmeticDetailScreen(
    navController: NavController,
    viewModel: SkincareViewModel = hiltViewModel()
) {
    val selected by viewModel.selectedProduct.collectAsState()
    val color = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Kosmetik", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = color.background)
            )
        },
        containerColor = color.background
    ) { padding ->
        if (selected == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Data kosmetik belum dipilih")
            }
            return@Scaffold
        }

        val product = selected!!
        val ingredients = product.bestIngredientsText

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = color.surface)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.productName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(product.productName ?: "Unknown Cosmetic", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            if (!product.brands.isNullOrBlank()) {
                Text(product.brands ?: "", color = color.onSurfaceVariant)
            }

            CosmeticInfoCard("Kategori", product.categories, Icons.Default.Category)
            CosmeticInfoCard("Negara Dijual", product.countries, Icons.Default.Public)
            CosmeticInfoCard("Quantity", product.quantity, Icons.Default.ShoppingBag)
            CosmeticInfoCard("Packaging", product.packaging, Icons.Default.Description)

            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = color.surface)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Ingredients", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        ingredients ?: "Ingredients tidak tersedia dari OpenBeautyFacts.",
                        color = color.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = {
                    if (!ingredients.isNullOrBlank()) {
                        viewModel.analyzeIngredients(
                            ingredientsText = ingredients,
                            productName = product.productName ?: "Cosmetic"
                        )
                        navController.navigate("skincare_scanner")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Analisis Halal & Safety")
            }
        }
    }
}

@Composable
private fun CosmeticInfoCard(title: String, value: String?, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    if (value.isNullOrBlank()) return
    val color = MaterialTheme.colorScheme
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color.primary)
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = color.onSurfaceVariant)
                Text(value, color = color.onSurface, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
