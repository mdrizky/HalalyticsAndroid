package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.R

// ═══════════════════════════════════════════════════════════════════
// SCAN HUB SCREEN — UNIFIED SMART SCAN CENTER
// Modern design with AI auto-detect hero, scan modes, gallery import
// ═══════════════════════════════════════════════════════════════════

@Composable
fun ScanHubScreen(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ─── Header ─────────────────────────────────────────
        Text(
            text = "Scan Center",
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Pindai produk, obat, dan makanan dengan AI",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        // ─── Hero Card: Smart AI Scanner ────────────────────
        SmartScanHeroCard(
            onClick = { navController.navigate("scan") }
        )

        // ─── Scan Mode Grid ─────────────────────────────────
        Text(
            "Pilih Mode Scan",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ScanModeCard(
                title = stringResource(R.string.scan_hub_barcode_title),
                subtitle = "Barcode & QR",
                icon = Icons.Default.QrCodeScanner,
                gradientColors = listOf(Color(0xFF004D40), Color(0xFF00695C)),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("scan") }
            )
            ScanModeCard(
                title = stringResource(R.string.scan_hub_ocr_title),
                subtitle = "Baca Komposisi",
                icon = Icons.Default.TextSnippet,
                gradientColors = listOf(Color(0xFF00695C), Color(0xFF42A5F5)),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("enhanced_ocr") }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ScanModeCard(
                title = stringResource(R.string.scan_hub_meal_title),
                subtitle = "Foto Makanan",
                icon = Icons.Default.Restaurant,
                gradientColors = listOf(Color(0xFF004D40), Color(0xFF26A69A)),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("food_scan") }
            )
            ScanModeCard(
                title = stringResource(R.string.scan_hub_bpom_title),
                subtitle = "Cek Registrasi",
                icon = Icons.Default.VerifiedUser,
                gradientColors = listOf(Color(0xFF004D40), Color(0xFF26A69A)),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("bpom_scanner") }
            )
        }

        // ─── Additional Scan Features ───────────────────────
        Text(
            "Fitur Lainnya",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = colorScheme.onBackground
        )

        ScanFeatureRow(
            title = "Pill Identifier",
            subtitle = "Identifikasi obat dari foto pil/tablet",
            icon = Icons.Default.Medication,
            iconTint = Color(0xFFE91E63),
            onClick = { navController.navigate("pill_scanner") }
        )

        ScanFeatureRow(
            title = "Skincare Scanner",
            subtitle = "Analisis keamanan bahan skincare",
            icon = Icons.Default.Face,
            iconTint = Color(0xFF26A69A),
            onClick = { navController.navigate("skincare_scanner") }
        )

        ScanFeatureRow(
            title = "Nutrisi Scanner",
            subtitle = "Scan label nutrisi produk",
            icon = Icons.Default.CameraAlt,
            iconTint = Color(0xFFF57C00),
            onClick = { navController.navigate("nutrition_scanner") }
        )

        ScanFeatureRow(
            title = "Galeri Import",
            subtitle = "Analisis gambar dari galeri kamu",
            icon = Icons.Default.PhotoLibrary,
            iconTint = Color(0xFF5C6BC0),
            onClick = { navController.navigate("enhanced_ocr") }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════
// HERO CARD — Smart AI Scanner
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun SmartScanHeroCard(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF004D40), Color(0xFF00695C), Color(0xFF26A69A))
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF26A69A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "AI Smart Scan",
                            color = Color(0xFF26A69A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Arahkan kamera ke produk apapun",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "AI akan otomatis mendeteksi barcode, teks, atau gambar makanan",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFF26A69A))
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Buka Scanner",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Pulsating scan icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(Color(0xFF26A69A).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF26A69A).copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// SCAN MODE CARD (Grid item)
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ScanModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(
                        title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        subtitle,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// SCAN FEATURE ROW (List item)
// ═══════════════════════════════════════════════════════════════════

@Composable
private fun ScanFeatureRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    subtitle,
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
