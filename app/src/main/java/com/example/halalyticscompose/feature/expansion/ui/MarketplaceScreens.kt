package com.example.halalyticscompose.feature.expansion.ui

import android.Manifest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.halalyticscompose.feature.expansion.model.HealthFacility
import com.example.halalyticscompose.feature.expansion.model.MarketplaceMerchant
import com.example.halalyticscompose.feature.expansion.model.MarketplaceProduct
import com.example.halalyticscompose.feature.expansion.viewmodel.MarketplaceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    navController: NavController,
    viewModel: MarketplaceViewModel = hiltViewModel(),
) {
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val merchants by viewModel.merchants.collectAsState()
    val healthFacilities by viewModel.healthFacilities.collectAsState()
    val products by viewModel.products.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var lastKnownLocation by remember { mutableStateOf(Pair(-6.200000, 106.816666)) }

    LaunchedEffect(locationPermission.status) {
        if (locationPermission.status is PermissionStatus.Granted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val lat = location?.latitude ?: lastKnownLocation.first
                val lng = location?.longitude ?: lastKnownLocation.second
                lastKnownLocation = lat to lng
                viewModel.loadNearbyMerchants(lat, lng)
                viewModel.loadHealthFacilities(lat, lng)
            }
        } else {
            locationPermission.launchPermissionRequest()
        }
        viewModel.loadProducts()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Marketplace Halal") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                )
                TabRow(selectedTabIndex = activeTab) {
                    listOf("Toko Halal", "Faskes", "Produk").forEachIndexed { index, title ->
                        Tab(
                            selected = activeTab == index,
                            onClick = { viewModel.setTab(index) },
                            text = { Text(title) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (activeTab == 2) {
                        viewModel.loadProducts(search = it)
                    }
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Cari merchant atau produk...") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(24.dp),
            )

            if (activeTab == 0) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        listOf(
                            null to "Semua",
                            "toko_halal" to "Toko",
                            "apotek" to "Apotek",
                            "restoran_halal" to "Resto",
                        ),
                    ) { (type, label) ->
                        FilterChip(
                            selected = selectedFilter == type,
                            onClick = {
                                selectedFilter = type
                                viewModel.loadNearbyMerchants(lastKnownLocation.first, lastKnownLocation.second, type)
                            },
                            label = { Text(label) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            when {
                isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                activeTab == 0 -> MerchantList(merchants)
                activeTab == 1 -> FacilityList(healthFacilities)
                else -> ProductGrid(products)
            }
        }
    }
}

@Composable
private fun MerchantList(merchants: List<MarketplaceMerchant>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(merchants, key = { it.id }) { merchant ->
            InfoCard(
                title = merchant.name,
                subtitle = merchant.address,
                trailing = merchant.distance?.let { "${String.format("%.1f", it)} km" } ?: "",
                badgeText = if (merchant.isVerified) "Terverifikasi" else null,
                icon = when (merchant.type) {
                    "restoran_halal" -> Icons.Default.Restaurant
                    "apotek" -> Icons.Default.MedicalServices
                    else -> Icons.Default.Store
                },
            )
        }
    }
}

@Composable
private fun FacilityList(facilities: List<HealthFacility>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(facilities, key = { it.placeId ?: it.name }) { facility ->
            InfoCard(
                title = facility.name,
                subtitle = facility.address.orEmpty(),
                trailing = facility.rating?.toString().orEmpty(),
                badgeText = when (facility.isOpen) {
                    true -> "Buka"
                    false -> "Tutup"
                    null -> null
                },
                icon = Icons.Default.LocalHospital,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductGrid(products: List<MarketplaceProduct>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(products, key = { it.id }) { product ->
            Card(
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(124.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (product.imageUrl.isNullOrBlank()) {
                            Icon(Icons.Default.Store, contentDescription = null)
                        } else {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(product.name, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Rp ${NumberFormat.getInstance(Locale("id", "ID")).format(product.price)}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            product.merchantName.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (product.isHalalCertified) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Bersertifikat halal", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    subtitle: String,
    trailing: String,
    badgeText: String?,
    icon: ImageVector,
) {
    Card(shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!badgeText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(badgeText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (trailing.isNotBlank()) {
                Text(trailing, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
