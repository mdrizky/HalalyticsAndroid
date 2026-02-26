package com.example.halalyticscompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.halalyticscompose.ui.theme.*

@Composable
fun QuickInsightsCards(
    totalScans: Int,
    halalProducts: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total Scans Card
        InsightCardPremium(
            title = "Records",
            value = totalScans.toString(),
            icon = Icons.Default.Dataset,
            color = Color(0xFF3B82F6),
            gradientColors = listOf(Color(0xFF3B82F6).copy(0.15f), Color(0xFF3B82F6).copy(0.05f))
        )
        
        // Halal Products Card
        InsightCardPremium(
            title = "Halal",
            value = halalProducts.toString(),
            icon = Icons.Default.Verified,
            color = Emerald500,
            gradientColors = listOf(Emerald500.copy(0.15f), Emerald500.copy(0.05f))
        )
        
        // Safety Progress Card
        val halalPercentage = if (totalScans > 0) {
            (halalProducts.toFloat() / totalScans * 100).toInt()
        } else 0
        
        val percentageColor = if (halalPercentage >= 80) Emerald500 else if (halalPercentage >= 50) MushboohYellow else HaramRed
        
        InsightCardPremium(
            title = "Safety Score",
            value = "$halalPercentage%",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            color = percentageColor,
            gradientColors = listOf(percentageColor.copy(0.15f), percentageColor.copy(0.05f))
        )
    }
}

@Composable
fun RowScope.InsightCardPremium(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    gradientColors: List<Color>
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(100.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(gradientColors))
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-1).sp
            )
            Text(
                text = title.uppercase(),
                fontSize = 8.sp,
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
