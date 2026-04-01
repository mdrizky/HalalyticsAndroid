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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.Data.Model.BpomProduct
import com.example.halalyticscompose.ui.viewmodel.BpomViewModel

private val NavyA = Color(0xFF1A237E)
private val NavyB = Color(0xFF00695C)
private val TextDark = Color(0xFF0A2540)
private val TextMuted = Color(0xFF64748B)
private val Safe = Color(0xFF00C896)
private val Warn = Color(0xFFF5A623)
private val Danger = Color(0xFFFF4757)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BpomScannerScreen(
    navController: NavController,
    viewModel: BpomViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }

    val searchResults by viewModel.searchResults.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val sessionInfo by viewModel.sessionInfo.collectAsState()
    val searchSource by viewModel.searchSource.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Verification Hub", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.ExtraBold)
                        Text("BPOM Official Registry", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("Nama produk / nomor registrasi BPOM") },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                if (query.isNotBlank()) {
                                    viewModel.searchBpom(query)
                                }
                            })
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = query.isNotBlank() && !isLoading) { viewModel.searchBpom(query) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 11.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                                } else {
                                    Text("Cari & Verifikasi", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                sessionInfo?.disclaimer?.takeIf { it.isNotBlank() }?.let {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), shape = RoundedCornerShape(12.dp)) {
                        Text(it, color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 11.sp, modifier = Modifier.padding(12.dp))
                    }
                }
            }
            item {
                val sourceLabel = if (searchSource == "database_lokal") {
                    "Sumber: Database BPOM lokal terverifikasi"
                } else if (!searchSource.isNullOrBlank()) {
                    "Sumber: $searchSource"
                } else {
                    "Sumber: Database BPOM lokal terverifikasi"
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        sourceLabel,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = RoundedCornerShape(12.dp)) {
                        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
                    }
                }
            }

            if (selectedProduct != null) {
                item {
                    BpomProductDetailCard(selectedProduct!!)
                }
            }

            if (searchResults.isNotEmpty()) {
                item {
                    Text("Database Produk", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                items(searchResults) { product ->
                    BpomProductListCard(product = product) {
                        viewModel.checkRegistration(product.nomorReg ?: product.namaProduk ?: "")
                    }
                }
            }

            if (!isLoading && selectedProduct == null && searchResults.isEmpty() && query.isBlank()) {
                item {
                    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer))),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.VerifiedUser, null, tint = MaterialTheme.colorScheme.onPrimary) }
                            Spacer(modifier = Modifier.size(10.dp))
                            Column {
                                Text("Siap cek data BPOM", color = TextDark, fontWeight = FontWeight.Bold)
                                Text("Masukkan nama produk atau nomor registrasi.", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            if (!isLoading && selectedProduct == null && searchResults.isEmpty() && query.isNotBlank()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Data BPOM tidak ditemukan", color = TextDark, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Mode ini khusus verifikasi database resmi. Jika data tidak ada di database, hasil tidak ditampilkan.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Gunakan nomor registrasi resmi BPOM (NA/MD/TR/SD) agar hasil valid.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BpomProductDetailCard(product: BpomProduct) {
    val statusColor = when ((product.statusHalal ?: "").lowercase()) {
        "halal" -> Safe
        "haram" -> Danger
        else -> Warn
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Hasil Verifikasi", color = NavyA, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text((product.statusHalal ?: "UNKNOWN").uppercase(), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
            DetailRow("No. Reg", product.nomorReg ?: "-")
            DetailRow("Nama", product.namaProduk ?: "-")
            DetailRow("Kategori", product.kategori ?: "-")
            DetailRow("Produsen", product.pendaftar ?: "-")
            DetailRow("Berlaku", product.masaBerlaku ?: "-")
            product.analisisHalal?.takeIf { it.isNotBlank() }?.let { DetailRow("Analisis", it) }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(label, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
        Text("  :  ", color = TextMuted, fontSize = 12.sp)
        Text(value, color = TextDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun BpomProductListCard(product: BpomProduct, onClick: () -> Unit) {
    val statusColor = when ((product.statusHalal ?: "").lowercase()) {
        "halal" -> Safe
        "haram" -> Danger
        else -> Warn
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE0F2F1)),
                contentAlignment = Alignment.Center
            ) {
                Text("BP", color = NavyA, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.namaProduk ?: "Produk",
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(product.nomorReg ?: "No. reg tidak tersedia", color = TextMuted, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text((product.statusHalal ?: "cek").uppercase(), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
