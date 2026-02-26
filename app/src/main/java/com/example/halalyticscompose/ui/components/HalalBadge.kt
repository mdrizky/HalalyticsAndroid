package com.example.halalyticscompose.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.halalyticscompose.ui.theme.HalalGreen

@Composable
fun HalalBadge(status: String) {
    val badgeColor = when(status.lowercase()) {
        "halal" -> HalalGreen
        "haram" -> Color.Red
        else -> Color(0xFFFFCC00)
    }

    Surface(
        color = badgeColor.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.uppercase(),
            color = badgeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
