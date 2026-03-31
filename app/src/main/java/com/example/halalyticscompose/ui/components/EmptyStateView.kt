package com.example.halalyticscompose.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable empty state view with contextual illustrations and CTA.
 */
@Composable
fun EmptyStateView(
    title: String = "Belum Ada Data",
    description: String = "Data akan muncul di sini setelah Anda mulai menggunakannya.",
    icon: ImageVector = Icons.Default.Inbox,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAction,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(actionLabel)
            }
        }
    }
}

// ========== Convenience Composables ==========

@Composable
fun EmptyProductsView(onScan: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    EmptyStateView(
        title = "Belum Ada Produk",
        description = "Scan barcode atau cari produk untuk mulai melihat data di sini.",
        icon = Icons.Default.QrCodeScanner,
        actionLabel = "Mulai Scan",
        onAction = onScan,
        modifier = modifier
    )
}

@Composable
fun EmptyHistoryView(onScan: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    EmptyStateView(
        title = "Belum Ada Riwayat",
        description = "Riwayat scan produk Anda akan muncul di sini.",
        icon = Icons.Default.History,
        actionLabel = "Mulai Scan",
        onAction = onScan,
        modifier = modifier
    )
}

@Composable
fun EmptyFavoritesView(onBrowse: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    EmptyStateView(
        title = "Belum Ada Favorit",
        description = "Produk yang Anda sukai akan tersimpan di sini.",
        icon = Icons.Default.FavoriteBorder,
        actionLabel = "Jelajahi Produk",
        onAction = onBrowse,
        modifier = modifier
    )
}

@Composable
fun EmptySearchView(modifier: Modifier = Modifier) {
    EmptyStateView(
        title = "Tidak Ditemukan",
        description = "Coba kata kunci lain atau periksa ejaan.",
        icon = Icons.Default.SearchOff,
        modifier = modifier
    )
}

@Composable
fun EmptyNotificationsView(modifier: Modifier = Modifier) {
    EmptyStateView(
        title = "Belum Ada Notifikasi",
        description = "Notifikasi baru akan muncul di sini.",
        icon = Icons.Default.NotificationsNone,
        modifier = modifier
    )
}
