package com.example.halalyticscompose.ui.screens

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.Data.Model.ArPOI
import com.example.halalyticscompose.presentation.viewmodel.ArViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.ar.core.ArCoreApk

@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
fun ArFinderScreen(
    onNavigateBack: () -> Unit,
    viewModel: ArViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val arAvailability = remember {
        try {
            ArCoreApk.getInstance().checkAvailability(context)
        } catch (_: Exception) {
            ArCoreApk.Availability.UNKNOWN_ERROR
        }
    }
    val arSupported = remember(arAvailability) {
        arAvailability == ArCoreApk.Availability.SUPPORTED_INSTALLED ||
            arAvailability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD ||
            arAvailability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED
    }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.loadNearbyPois(location.latitude, location.longitude)
                } else {
                    viewModel.loadNearbyPois(-6.2088, 106.8456)
                }
            }
        }
    }

    if (!cameraPermission.status.isGranted || !locationPermission.status.isGranted) {
        PermissionFallback(
            onNavigateBack = onNavigateBack,
            onGrantPermissions = {
                cameraPermission.launchPermissionRequest()
                locationPermission.launchPermissionRequest()
            },
        )
        return
    }

    if (!arSupported) {
        ArFallbackScreen(
            pois = uiState.pois,
            isLoading = uiState.isLoading,
            error = uiState.error,
            onNavigateBack = onNavigateBack,
        )
        return
    }

    val uriHandler = LocalUriHandler.current

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onTextDetected = {},
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.08f)),
        ) {
            uiState.pois.take(6).forEachIndexed { index, poi ->
                ArPin(
                    poi = poi,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(
                            start = ((index % 3) * 110).dp,
                            top = ((index / 3) * 120).dp,
                        ),
                    onClick = { viewModel.selectPoi(poi) },
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.45f), CircleShape),
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                color = Color.Black.copy(alpha = 0.45f),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(
                    text = "${uiState.pois.size} lokasi sekitar kamu",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        if (uiState.isLoading && uiState.pois.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Mencari lokasi sekitar...", color = Color.White)
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.selectedPoi != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            uiState.selectedPoi?.let { poi ->
                ArPoiInfoCard(
                    poi = poi,
                    onDismiss = { viewModel.selectPoi(null) },
                    onDirections = {
                        uriHandler.openUri(
                            "https://www.google.com/maps/dir/?api=1&destination=${poi.latitude},${poi.longitude}",
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionFallback(
    onNavigateBack: () -> Unit,
    onGrantPermissions: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AR Finder") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Izin kamera dan lokasi diperlukan untuk AR Finder", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Kami butuh akses kamera untuk tampilan visual dan lokasi untuk menghitung tujuan terdekat.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onGrantPermissions) {
                    Text("Izinkan Sekarang")
                }
            }
        }
    }
}

@Composable
private fun ArPin(
    poi: ArPOI,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val icon = when (poi.type) {
        "klinik", "rs", "puskesmas" -> Icons.Default.LocalHospital
        "apotek" -> Icons.Default.LocalPharmacy
        "restoran_halal" -> Icons.Default.Restaurant
        else -> Icons.Default.Store
    }
    val color = when (poi.type) {
        "klinik", "rs", "puskesmas" -> Color(0xFF1976D2)
        "apotek" -> Color(0xFF2E7D32)
        "restoran_halal" -> Color(0xFFEF6C00)
        else -> Color(0xFF6A1B9A)
    }

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.72f),
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                Text(
                    text = poi.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
                Text(
                    text = formatDistance(poi.distance),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = color,
            shape = CircleShape,
            shadowElevation = 8.dp,
            modifier = Modifier.size(38.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(9.dp),
            )
        }
    }
}

@Composable
private fun ArPoiInfoCard(
    poi: ArPOI,
    onDirections: () -> Unit,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(poi.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = typeLabel(poi.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Surface(
                    color = Color(0xFFE0F2F1),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = formatDistance(poi.distance),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = Color(0xFF004D40),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onDirections,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Petunjuk Arah")
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArFallbackScreen(
    pois: List<ArPOI>,
    isLoading: Boolean,
    error: String?,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lokasi Terdekat") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFF57F17))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Perangkat kamu belum mendukung ARCore. Kami tampilkan mode daftar supaya pencarian lokasi tetap jalan.",
                        color = Color(0xFF6D4C41),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading && pois.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null && pois.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(error, textAlign = TextAlign.Center)
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(pois) { poi ->
                            Card(shape = RoundedCornerShape(18.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Surface(
                                        color = Color(0xFFE0F2F1),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(48.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = Color(0xFF004D40),
                                            modifier = Modifier.padding(12.dp),
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(poi.name, fontWeight = FontWeight.Bold)
                                        Text(
                                            typeLabel(poi.type),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    Text(
                                        formatDistance(poi.distance),
                                        color = Color(0xFF004D40),
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDistance(distanceKm: Double?): String {
    val safeDistance = distanceKm ?: 0.0
    return if (safeDistance < 1.0) {
        "${(safeDistance * 1000).toInt()} m"
    } else {
        String.format("%.1f km", safeDistance)
    }
}

private fun typeLabel(type: String): String {
    return when (type) {
        "klinik" -> "Klinik"
        "rs" -> "Rumah Sakit"
        "puskesmas" -> "Puskesmas"
        "apotek" -> "Apotek"
        "restoran_halal" -> "Restoran Halal"
        "toko_halal" -> "Toko Halal"
        else -> type.replace('_', ' ')
    }
}
