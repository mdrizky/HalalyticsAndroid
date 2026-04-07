package com.example.halalyticscompose.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.presentation.viewmodel.ArViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ArFinderScreen(
    onNavigateBack: () -> Unit,
    viewModel: ArViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val uiState by viewModel.uiState.collectAsState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) permissionState.launchPermissionRequest()
        if (!locationPermission.status.isGranted) locationPermission.launchPermissionRequest()
        
        // Mock location if not available or get real
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                viewModel.loadNearbyPois(loc.latitude, loc.longitude)
            } else {
                // Default to Jakarta for demo
                viewModel.loadNearbyPois(-6.2088, 106.8456)
            }
        }
    }

    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (permissionState.status.isGranted) {
                // Camera Preview for AR background
                CameraPreview(modifier = Modifier.fillMaxSize(), onTextDetected = {})
                
                // AR Overlay (Simulated Pins)
                Box(modifier = Modifier.fillMaxSize()) {
                    uiState.pois.forEachIndexed { index, poi ->
                        // Simple 2D placement based on index for demo
                        ArPin(
                            poi = poi,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(
                                    x = ((index - 1) * 100).dp,
                                    y = ((index % 2) * -50).dp
                                )
                        )
                    }
                }
            }

            // Top Bar Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("AR Finder", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF00BFA5))
            }

            // Bottom Carousel
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.pois) { poi ->
                        Card(
                            modifier = Modifier.width(280.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(poi.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(poi.type, color = Color.Gray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF00BFA5))
                                    Text("${poi.distance ?: 0.0} km away", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArPin(poi: com.example.halalyticscompose.Data.Model.ArPOI, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF00BFA5),
            shadowElevation = 4.dp
        ) {
            Text(
                poi.name,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color(0xFF00BFA5), CircleShape)
                .padding(4.dp)
        ) {
            Icon(Icons.Default.Place, contentDescription = null, tint = Color.White)
        }
    }
}
