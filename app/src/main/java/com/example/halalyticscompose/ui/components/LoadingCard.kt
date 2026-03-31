package com.example.halalyticscompose.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ========== SHIMMER EFFECT ==========

@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(shimmerBrush())
    )
}

// ========== LOADING CARD VARIANTS ==========

/**
 * Skeleton shimmer loading card for product lists.
 */
@Composable
fun LoadingProductCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product image placeholder
        ShimmerBox(
            modifier = Modifier.size(64.dp),
            cornerRadius = 12.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Title
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Subtitle
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Badge
            ShimmerBox(
                modifier = Modifier
                    .width(80.dp)
                    .height(20.dp),
                cornerRadius = 10.dp
            )
        }
    }
}

/**
 * Skeleton shimmer loading card for articles.
 */
@Composable
fun LoadingArticleCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(180.dp)
            .padding(8.dp)
    ) {
        // Image placeholder
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            cornerRadius = 12.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Title line 1
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(14.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Title line 2
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(14.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Date
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(10.dp)
        )
    }
}

/**
 * Skeleton shimmer loading for medicine list items.
 */
@Composable
fun LoadingMedicineCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pill icon placeholder
        ShimmerBox(
            modifier = Modifier.size(48.dp),
            cornerRadius = 24.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
            )
        }

        // Status badge
        ShimmerBox(
            modifier = Modifier
                .width(60.dp)
                .height(24.dp),
            cornerRadius = 12.dp
        )
    }
}

/**
 * Skeleton shimmer for profile header.
 */
@Composable
fun LoadingProfileCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        ShimmerBox(
            modifier = Modifier.size(80.dp),
            cornerRadius = 40.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Name
        ShimmerBox(
            modifier = Modifier
                .width(160.dp)
                .height(20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Email
        ShimmerBox(
            modifier = Modifier
                .width(200.dp)
                .height(14.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ShimmerBox(
                        modifier = Modifier
                            .width(40.dp)
                            .height(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .width(60.dp)
                            .height(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * Full-page loading screen with multiple product card skeletons.
 */
@Composable
fun LoadingListView(
    itemCount: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        repeat(itemCount) {
            LoadingProductCard()
            if (it < itemCount - 1) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
