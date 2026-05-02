package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.net.Uri
import coil.compose.AsyncImage
import com.example.halalyticscompose.data.api.BeautyProduct
import com.example.halalyticscompose.data.api.bestId
import com.example.halalyticscompose.data.api.bestIngredientsText
import com.example.halalyticscompose.data.model.MedicineData
import com.example.halalyticscompose.data.model.ProductItem
import com.google.gson.Gson
import com.example.halalyticscompose.ui.viewmodel.MedicineViewModel
import com.example.halalyticscompose.ui.viewmodel.ProductExternalViewModel
import com.example.halalyticscompose.ui.viewmodel.SkincareViewModel

private enum class SearchTab(val title: String) {
    MEDICINE("Obat"),
    FOOD("Food"),
    COSMETIC("Kosmetik")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHubScreen(
    navController: NavController,
    foodViewModel: ProductExternalViewModel = hiltViewModel(),
    medicineViewModel: MedicineViewModel = hiltViewModel(),
    skincareViewModel: SkincareViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val color = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val selectedTab = SearchTab.entries[selectedTabIndex]

    val foodResults by foodViewModel.searchResults.collectAsState()
    val foodLoading by foodViewModel.isSearching.collectAsState()
    val foodError by foodViewModel.searchError.collectAsState()

    val medicineResults by medicineViewModel.medicines.collectAsState()
    val medicineLoading by medicineViewModel.isLoading.collectAsState()
    val medicineError by medicineViewModel.errorMessage.collectAsState()

    val cosmeticResults by skincareViewModel.searchResults.collectAsState()
    val cosmeticLoading by skincareViewModel.isLoading.collectAsState()
    val cosmeticError by skincareViewModel.errorMessage.collectAsState()

    fun submitSearch() {
        val q = query.trim()
        if (q.isEmpty()) return
        focusManager.clearFocus()
        when (selectedTab) {
            SearchTab.MEDICINE -> medicineViewModel.searchMedicine(q)
            SearchTab.FOOD -> foodViewModel.searchProducts(query = q, pageSize = 100, page = 1)
            SearchTab.COSMETIC -> skincareViewModel.searchSkincare(q)
        }
    }

    Scaffold(
        containerColor = color.background,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Search Hub",
                            fontWeight = FontWeight.ExtraBold,
                            color = color.onBackground,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = color.background,
                        scrolledContainerColor = color.background
                    )
                )
                
                // Enhanced Search Bar
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search medicine, food, or cosmetic...", color = color.onSurfaceVariant.copy(alpha = 0.6f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = color.primary) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = color.onSurfaceVariant)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = color.surface,
                            unfocusedContainerColor = color.surface,
                            focusedBorderColor = color.primary,
                            unfocusedBorderColor = Color.Transparent,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { submitSearch() })
                    )
                }

                // Custom Premium Tab Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SearchTab.entries.forEachIndexed { index, tab ->
                        val isSelected = selectedTabIndex == index
                        val tabColor = if (isSelected) color.primary else color.surfaceVariant.copy(alpha = 0.7f)
                        val contentColor = if (isSelected) color.onPrimary else color.onSurfaceVariant
                        
                        Card(
                            onClick = { selectedTabIndex = index },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = tabColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val icon = when (tab) {
                                    SearchTab.MEDICINE -> Icons.Default.LocalPharmacy
                                    SearchTab.FOOD -> Icons.Default.CheckCircle
                                    SearchTab.COSMETIC -> Icons.Default.Spa
                                }
                                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = contentColor)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(tab.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 11.sp, color = contentColor)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            when (selectedTab) {
                SearchTab.MEDICINE -> {
                    SearchHeader(
                        title = "Database Obat (BPOM/FDA)",
                        subtitle = "Hasil real-time untuk obat, generik, dan status halal"
                    )
                    when {
                        medicineLoading -> LoadingState("Mencari obat...")
                        !medicineError.isNullOrBlank() -> ErrorState(medicineError ?: "Gagal memuat obat")
                        medicineResults.isEmpty() && query.isNotBlank() -> EmptyState("Obat tidak ditemukan")
                        medicineResults.isEmpty() -> EmptyState("Masukkan kata kunci untuk cari obat")
                        else -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(medicineResults) { medicine ->
                                    MedicineResultCard(medicine = medicine) {
                                        val id = medicine.idMedicine ?: 0
                                        if (id > 0) navController.navigate("medicine_detail/$id")
                                        else navController.navigate("international_medicine")
                                    }
                                }
                                item { Spacer(modifier = Modifier.height(20.dp)) }
                            }
                        }
                    }
                }

                SearchTab.FOOD -> {
                    SearchHeader(
                        title = "Database Food (OpenFoodFacts)",
                        subtitle = "Produk makanan/minuman dengan filter halal"
                    )
                    when {
                        foodLoading -> LoadingState("Mencari produk food...")
                        foodError.isNotBlank() -> ErrorState(foodError)
                        foodResults.isEmpty() && query.isNotBlank() -> EmptyState("Produk food tidak ditemukan")
                        foodResults.isEmpty() -> EmptyState("Masukkan kata kunci untuk cari food")
                        else -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(foodResults) { product ->
                                    FoodResultCard(product = product) {
                                        val barcode = product.barcode ?: product.code ?: product.id
                                        if (!barcode.isNullOrBlank()) {
                                            navController.navigate("product_external_detail/$barcode")
                                        }
                                    }
                                }
                                item { Spacer(modifier = Modifier.height(20.dp)) }
                            }
                        }
                    }
                }

                SearchTab.COSMETIC -> {
                    SearchHeader(
                        title = "Database Kosmetik (OpenBeautyFacts)",
                        subtitle = "Cari komposisi dan analisis keamanan/kelayakan halal"
                    )
                    when {
                        cosmeticLoading -> LoadingState("Mencari kosmetik...")
                        !cosmeticError.isNullOrBlank() -> ErrorState(cosmeticError ?: "Gagal memuat kosmetik")
                        cosmeticResults.isEmpty() && query.isNotBlank() -> EmptyState("Kosmetik tidak ditemukan")
                        cosmeticResults.isEmpty() -> EmptyState("Masukkan kata kunci untuk cari kosmetik")
                        else -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(cosmeticResults) { cosmetic ->
                                    CosmeticResultCard(cosmetic = cosmetic) {
                                        skincareViewModel.selectProduct(cosmetic)
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            "selected_cosmetic_json",
                                            Gson().toJson(cosmetic)
                                        )
                                        val id = cosmetic.bestId
                                        if (!id.isNullOrBlank()) {
                                            navController.navigate("cosmetic_detail/${Uri.encode(id)}")
                                        } else {
                                            navController.navigate("cosmetic_detail")
                                        }
                                    }
                                }
                                item { Spacer(modifier = Modifier.height(20.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHeader(title: String, subtitle: String) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = color.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = color.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(subtitle, color = color.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}

@Composable
private fun LoadingState(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(10.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEAEA)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = message,
            color = Color(0xFFC62828),
            modifier = Modifier.padding(12.dp),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

@Composable
private fun MedicineResultCard(medicine: MedicineData, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme
    val halalText = medicine.halalStatus.ifBlank { "unknown" }.uppercase()
    val halalColor = when (medicine.halalStatus.lowercase()) {
        "halal" -> Color(0xFF2E7D32)
        "haram" -> Color(0xFFC62828)
        else -> Color(0xFFEF6C00)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.primary.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = color.primary)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(medicine.name, fontWeight = FontWeight.Bold, color = color.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(medicine.genericName ?: "-", fontSize = 11.sp, color = color.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(halalText, color = halalColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
    }
}

@Composable
private fun FoodResultCard(product: ProductItem, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.getBestImageUrl(),
                contentDescription = product.getDisplayName(),
                modifier = Modifier
                    .size(46.dp)
                    .background(color.surfaceVariant, RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.getDisplayName(), fontWeight = FontWeight.Bold, color = color.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.brands ?: "-", fontSize = 11.sp, color = color.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = product.getHalalStatusColor(), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(product.getHalalStatus(), color = product.getHalalStatusColor(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CosmeticResultCard(cosmetic: BeautyProduct, onClick: () -> Unit) {
    val color = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cosmetic.imageUrl,
                contentDescription = cosmetic.productName,
                modifier = Modifier
                    .size(46.dp)
                    .background(color.surfaceVariant, RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cosmetic.productName ?: "Unknown Cosmetic", fontWeight = FontWeight.Bold, color = color.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(cosmetic.brands ?: "-", fontSize = 11.sp, color = color.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(cosmetic.bestIngredientsText ?: "Komposisi belum tersedia", fontSize = 10.sp, color = color.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Analisis", color = color.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
