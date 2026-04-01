package com.example.halalyticscompose.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Halal Status Colors
object HalalColors {
    val HalalLight = Color(0xFFE8F5E9)      // Hijau Soft
    val HalalDark = Color(0xFF2E7D32)       // Hijau Tua
    val HalalMain = Color(0xFF4CAF50)      // Hijau Utama
    
    val SyubhatLight = Color(0xFFFFF3E0)    // Kuning/Orange Soft
    val SyubhatDark = Color(0xFFEF6C00)     // Orange Tua
    val SyubhatMain = Color(0xFFFF9800)     // Orange Utama
    
    val HaramLight = Color(0xFFFFEBEE)      // Merah Soft
    val HaramDark = Color(0xFFC62828)       // Merah Tua
    val HaramMain = Color(0xFFF44336)       // Merah Utama
    
    val UnknownLight = Color(0xFFF5F5F5)    // Abu-abu Soft
    val UnknownDark = Color(0xFF616161)     // Abu-abu Tua
    val UnknownMain = Color(0xFF9E9E9E)     // Abu-abu Utama
}

@Composable
fun getHalalColor(status: String): Color {
    return when (status.lowercase()) {
        "halal" -> HalalColors.HalalLight
        "syubhat", "doubtful" -> HalalColors.SyubhatLight
        "haram", "non_halal" -> HalalColors.HaramLight
        else -> HalalColors.UnknownLight
    }
}

@Composable
fun getHalalColorDark(status: String): Color {
    return when (status.lowercase()) {
        "halal" -> HalalColors.HalalDark
        "syubhat", "doubtful" -> HalalColors.SyubhatDark
        "haram", "non_halal" -> HalalColors.HaramDark
        else -> HalalColors.UnknownDark
    }
}

@Composable
fun getHalalColorMain(status: String): Color {
    return when (status.lowercase()) {
        "halal" -> HalalColors.HalalMain
        "syubhat", "doubtful" -> HalalColors.SyubhatMain
        "haram", "non_halal" -> HalalColors.HaramMain
        else -> HalalColors.UnknownMain
    }
}

@Composable
fun getHalalIcon(status: String) {
    val icon = when (status.lowercase()) {
        "halal" -> Icons.Default.CheckCircle
        "syubhat", "doubtful" -> Icons.Default.Warning
        "haram", "non_halal" -> Icons.Default.Warning
        else -> Icons.AutoMirrored.Filled.Help
    }
    
    val color = getHalalColorMain(status)
    
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun HalalStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = getHalalColor(status)
    val textColor = getHalalColorDark(status)
    val borderColor = getHalalColorMain(status)
    
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(animationSpec = tween(350)) + fadeIn(animationSpec = tween(350))
    ) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                getHalalIcon(status)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = status.uppercase(),
                    color = textColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HalalMeterIndicator(
    status: String,
    confidence: Float? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = getHalalColor(status)
    val progressColor = getHalalColorMain(status)
    
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Icon
                getHalalIcon(status)
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Status Text
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (status.lowercase()) {
                            "halal" -> "PRODUK HALAL ✓"
                            "syubhat", "doubtful" -> "PRODUK SYUBHAT ⚠️"
                            "haram", "non_halal" -> "PRODUK TIDAK HALAL ✗"
                            else -> "STATUS TIDAK DIKETAHUI"
                        },
                        color = getHalalColorDark(status),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    confidence?.let {
                        Text(
                            text = "Tingkat Kepercayaan: ${(it * 100).toInt()}%",
                            color = getHalalColorDark(status),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Confidence Bar (if available)
            confidence?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(2.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(it)
                            .height(4.dp)
                            .background(
                                color = progressColor,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun HalalColorIndicator(
    status: String,
    modifier: Modifier = Modifier
) {
    val color = getHalalColorMain(status)
    
    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}
