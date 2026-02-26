package com.example.halalyticscompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.halalyticscompose.ui.theme.*

@Composable
fun ConfidenceBadge(
    score: Double,
    level: String, // "high", "medium", "low"
    message: String,
    modifier: Modifier = Modifier
) {
    val (color, icon, label) = when (level.lowercase()) {
        "high" -> Triple(
            Emerald500,
            Icons.Default.Verified,
            "AI CONFIDENCE: HIGH"
        )
        "medium" -> Triple(
            MushboohYellow,
            Icons.Default.Info,
            "AI CONFIDENCE: MEDIUM"
        )
        else -> Triple(
            HaramRed,
            Icons.Default.Warning,
            "AI CONFIDENCE: LOW"
        )
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgDarkSurface)
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = color,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(score * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )
                }
                
                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
