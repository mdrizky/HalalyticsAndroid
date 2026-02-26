package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val SearchBg = Color(0xFFF7FAFC)
private val SearchPrimary = Color(0xFF00C896)
private val SearchText = Color(0xFF0A2540)
private val SearchMuted = Color(0xFF64748B)
private val BpomNavy = Color(0xFF1A237E)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchHubScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Semua") }
    val chips = listOf("Semua", "Makanan", "Obat", "Kosmetik")

    Scaffold(
        containerColor = SearchBg,
        topBar = {
            TopAppBar(
                title = { Text("Cari Produk", color = SearchText, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(34.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color.White)
                            .clickable { navController.navigateUp() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SearchText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SearchBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                placeholder = { Text("Cari makanan, obat, kosmetik...", fontSize = 13.sp, color = SearchMuted) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = SearchMuted) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = SearchPrimary,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        when (filter) {
                            "Obat" -> navController.navigate("international_medicine")
                            "Kosmetik" -> navController.navigate("skincare_scanner")
                            else -> navController.navigate("search_external")
                        }
                    }
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chips.forEach { item ->
                    FilterChip(
                        selected = filter == item,
                        onClick = { filter = item },
                        label = { Text(item, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SearchPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = SearchPrimary,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = SearchMuted
                        )
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .clickable { navController.navigate("bpom_scanner") },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BpomNavy)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("BPOM", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Verifikasi BPOM RI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Cek nomor registrasi resmi", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                    Text("Buka", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Hasil populer",
                color = SearchText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            SearchResultCard("Indomie Goreng", "BPOM: MD 123456789", "HALAL", Color(0xFF00C896)) {
                navController.navigate("search_external")
            }
            SearchResultCard("Paracetamol 500mg", "BPOM: DBL 1234567890", "HALAL", Color(0xFF00C896)) {
                navController.navigate("international_medicine")
            }
            SearchResultCard("Lipstik Matte", "BPOM: NA 18201234567", "SYUBHAT", Color(0xFFF5A623)) {
                navController.navigate("skincare_scanner")
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    name: String,
    subtitle: String,
    status: String,
    statusColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) { Text("📦") }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = SearchText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, color = SearchMuted, fontSize = 10.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(status, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
