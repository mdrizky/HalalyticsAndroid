package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Palette
private val PrimaryPurple = Color(0xFF7C3AED)
private val LightPurple = Color(0xFFE9D5FF)
private val GreenHalal = Color(0xFF10B981)
private val RedHaram = Color(0xFFEF4444)
private val BackgroundGray = Color(0xFFF8FAFC)
private val TextDark = Color(0xFF1E293B)
private val TextGray = Color(0xFF64748B)
private val CardWhite = Color.White

enum class HistoryFilter {
    ALL, HALAL, NOT_HALAL
}

data class ScanHistoryItem(
    val id: Int,
    val productName: String,
    val category: String,
    val weight: String,
    val isHalal: Boolean,
    val date: String,
    val dateGroup: String, // HARI INI, KEMARIN, MINGGU LALU
    val imageEmoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBackClick: () -> Unit = {},
    onItemClick: (Int) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(HistoryFilter.ALL) }
    
    // Sample data
    val historyItems = remember {
        listOf(
            ScanHistoryItem(1, "Keripik Kentang Original", "Makanan", "150g", true, "12 Okt, 14:33", "HARI INI", "🥔"),
            ScanHistoryItem(2, "Spicy Ramen Cup Import", "Mie Instan", "80g", true, "11 Okt, 09:00", "HARI INI", "🍜"),
            ScanHistoryItem(3, "Dark Chocolate 70%", "Snack Cocos", "85g", false, "10 Okt, 15:30", "KEMARIN", "🍫"),
            ScanHistoryItem(4, "Gummy Bears Mix", "Permen", "120g", false, "10 Okt, 10:20", "KEMARIN", "🍬"),
            ScanHistoryItem(5, "Kopi Instan Gold", "Minuman", "150g", true, "5 Okt, 08:00", "MINGGU LALU", "☕")
        )
    }
    
    val filteredItems = historyItems.filter { item ->
        val matchesSearch = item.productName.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            HistoryFilter.ALL -> true
            HistoryFilter.HALAL -> item.isHalal
            HistoryFilter.NOT_HALAL -> !item.isHalal
        }
        matchesSearch && matchesFilter
    }
    
    val groupedItems = filteredItems.groupBy { it.dateGroup }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Scan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundGray
                )
            )
        },
        containerColor = BackgroundGray
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    placeholder = {
                        Text(
                            "Cari nama produk...",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = TextGray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite,
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
            
            // Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipButton(
                        text = "Semua",
                        isSelected = selectedFilter == HistoryFilter.ALL,
                        onClick = { selectedFilter = HistoryFilter.ALL }
                    )
                    FilterChipButton(
                        text = "Halal",
                        isSelected = selectedFilter == HistoryFilter.HALAL,
                        onClick = { selectedFilter = HistoryFilter.HALAL }
                    )
                    FilterChipButton(
                        text = "Tidak Halal",
                        isSelected = selectedFilter == HistoryFilter.NOT_HALAL,
                        onClick = { selectedFilter = HistoryFilter.NOT_HALAL }
                    )
                }
            }
            
            // History Items grouped by date
            groupedItems.forEach { (dateGroup, items) ->
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dateGroup,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                items(items) { historyItem ->
                    HistoryItemCard(
                        item = historyItem,
                        onClick = { onItemClick(historyItem.id) }
                    )
                }
            }
            
            // Bottom spacing for navigation bar
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun FilterChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) PrimaryPurple else LightPurple
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else PrimaryPurple
        )
    }
}

@Composable
private fun HistoryItemCard(
    item: ScanHistoryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Product Image
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color(0xFFF1F5F9),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.imageEmoji,
                        fontSize = 28.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = item.productName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                    Text(
                        text = "${item.category} • ${item.weight}",
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }
            }
            
            // Halal Badge
            Box(
                modifier = Modifier
                    .background(
                        if (item.isHalal) GreenHalal else RedHaram,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (item.isHalal) "HALAL" else "HARAM",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
