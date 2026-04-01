package com.example.halalyticscompose.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════
// SHIMMER EFFECT — Premium Loading Placeholder
// Matches Emerald Forest palette (#F4F9F8 background)
// ═══════════════════════════════════════════════════════════════════

/**
 * Modifier extension that applies a shimmer loading effect.
 * Use this instead of CircularProgressIndicator for premium feel.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "shimmer_offset"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFE0E0E0),  // Base gray
                Color(0xFFF5F5F5),  // Highlight flash
                Color(0xFFE0E0E0),  // Base gray
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

// ═══════════════════════════════════════════════════════════════════
// SHIMMER PLACEHOLDERS — Ready-to-use loading skeletons
// ═══════════════════════════════════════════════════════════════════

/**
 * Shimmer placeholder for a product/scan history card.
 * Mimics the layout of RecentScanCard and HistoryCard.
 */
@Composable
fun ShimmerProductItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            // Title line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Subtitle line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // Badge placeholder
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(100.dp))
                .shimmerEffect()
        )
    }
}

/**
 * Shimmer placeholder for the BentoGrid / Health Dashboard area.
 */
@Composable
fun ShimmerBentoGrid(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // AI Insight card shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(18.dp))
                .shimmerEffect()
        )
        // Row of two cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

/**
 * Shimmer placeholder for article cards.
 */
@Composable
fun ShimmerArticleItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 90.dp, height = 68.dp)
                .clip(RoundedCornerShape(10.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

/**
 * Shimmer placeholder for the banner area.
 */
@Composable
fun ShimmerBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .shimmerEffect()
    )
}

/**
 * Shimmer placeholder for profile header.
 */
@Composable
fun ShimmerProfileHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(12.dp))
        // Name
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Email
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
    }
}
