package com.example.halalyticscompose.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Color Constants
private val Navy = Color(0xFF004D40)
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF212121)
private val TextMedium = Color(0xFF757575)
private val BgLight = Color(0xFFF4F9F8)

data class FeatureActionItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val iconTint: Color = Navy,
    val bgTint: Color = Color(0xFFE0F2F1)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllFeaturesScreen(navController: NavController) {
    // Categorize features for a true Super App feel
    val coreFeatures = listOf(
        FeatureActionItem("Scan Halal", Icons.Default.QrCode2, "scan", Color(0xFF00695C), Color(0xFFE0F2F1)),
        FeatureActionItem("BPOM", Icons.Default.HealthAndSafety, "bpom_scanner", Color(0xFF0277BD), Color(0xFFE1F5FE)),
        FeatureActionItem("Kosmetik", Icons.Default.AutoAwesome, "skincare_scanner", Color(0xFF7B1FA2), Color(0xFFF3E5F5)),
        FeatureActionItem("Cek Obat", Icons.Default.Medication, "drug_interaction", Color(0xFFD32F2F), Color(0xFFFFEBEE))
    )

    val healthSuiteFeatures = listOf(
        FeatureActionItem("Lab Scan", Icons.Default.Biotech, "lab_analysis", Color(0xFF388E3C), Color(0xFFE8F5E9)),
        FeatureActionItem("Medical Records", Icons.Default.MedicalServices, "medical_records", Color(0xFFE91E63), Color(0xFFFCE4EC)),
        FeatureActionItem("Medical Resume", Icons.Default.Description, "medical_resume", Color(0xFFF57C00), Color(0xFFFFF3E0)),
        FeatureActionItem("Pantauan Tubuh", Icons.Default.MonitorHeart, "health_monitor", Color(0xFFC2185B), Color(0xFFFCE4EC))
    )

    val smartAiFeatures = listOf(
        FeatureActionItem("AI Assistant", Icons.Default.SmartToy, "health_assistant", Color(0xFF512DA8), Color(0xFFEDE7F6)),
        FeatureActionItem("Health Journey", Icons.Default.CalendarMonth, "health_journey", Color(0xFF00796B), Color(0xFFE0F2F1)),
        FeatureActionItem("Nutrition Scan", Icons.Default.CameraAlt, "nutrition_scanner", Color(0xFFFBC02D), Color(0xFFFFF9C4)),
        FeatureActionItem("Health Diary", Icons.Default.Edit, "health_diary", Color(0xFF0097A7), Color(0xFFE0F7FA))
    )

    val aiExpansionFeatures = listOf(
        FeatureActionItem("OCR Produk", Icons.Default.QrCodeScanner, "ocr_scan", Color(0xFFE53935), Color(0xFFFFEBEE)),
        FeatureActionItem("Nutrisi AI", Icons.Default.MonitorHeart, "nutrition_dashboard", Color(0xFF00897B), Color(0xFFE0F2F1)),
        FeatureActionItem("Recipe AI", Icons.Default.MenuBook, "recipes", Color(0xFF6A1B9A), Color(0xFFF3E5F5)),
        FeatureActionItem("AR Finder", Icons.Default.ViewInAr, "ar_finder", Color(0xFFF57C00), Color(0xFFFFF3E0)),
        FeatureActionItem("Misi Harian", Icons.Default.TaskAlt, "daily_mission_dashboard", Color(0xFF1565C0), Color(0xFFE3F2FD))
    )

    val supportFeatures = listOf(
        FeatureActionItem("Halocode", Icons.Default.Chat, "halocode", Color(0xFF00695C), Color(0xFFE0F2F1)),
        FeatureActionItem("Marketplace", Icons.Default.Storefront, "marketplace", Color(0xFF2E7D32), Color(0xFFE8F5E9)),
        FeatureActionItem("Komunitas", Icons.Default.Groups, "community", Color(0xFF1976D2), Color(0xFFE3F2FD)),
        FeatureActionItem("Health Pass", Icons.Default.VerifiedUser, "health_pass", Color(0xFF455A64), Color(0xFFECEFF1)),
        FeatureActionItem("Emergency", Icons.Default.LocalHospital, "emergency_p3k", Color(0xFFD32F2F), Color(0xFFFFEBEE)),
        FeatureActionItem("Report Issue", Icons.Default.Warning, "report_issue/0/General", Color(0xFFF57C00), Color(0xFFFFF3E0)),
        FeatureActionItem("Intl Medicine", Icons.Default.Public, "international_medicine", Color(0xFF1976D2), Color(0xFFE3F2FD))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Semua Fitur", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardWhite,
                    titleContentColor = TextDark,
                    navigationIconContentColor = TextDark
                )
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            item {
                FeatureSectionGrid("Layanan Utama", coreFeatures, navController)
            }
            item {
                FeatureSectionGrid("Health Suite (Kesehatan)", healthSuiteFeatures, navController)
            }
            item {
                FeatureSectionGrid("Pintar & AI", smartAiFeatures, navController)
            }
            item {
                FeatureSectionGrid("AI Expansion", aiExpansionFeatures, navController)
            }
            item {
                FeatureSectionGrid("Dukungan & Lainnya", supportFeatures, navController)
            }
        }
    }
}

@Composable
private fun FeatureSectionGrid(
    title: String,
    features: List<FeatureActionItem>,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = TextDark,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                features.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (rowItems.size == 4) 8.dp else 0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowItems.forEach { item ->
                            val weightModifier = Modifier.weight(1f) // Distribute evenly
                            Box(modifier = weightModifier, contentAlignment = Alignment.Center) {
                                FeatureGridItem(item) {
                                    navController.navigate(item.route)
                                }
                            }
                        }
                        repeat(4 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureGridItem(item: FeatureActionItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(item.bgTint),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            color = TextMedium,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            modifier = Modifier.width(64.dp)
        )
    }
}
