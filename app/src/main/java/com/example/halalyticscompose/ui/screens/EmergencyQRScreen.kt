package com.example.halalyticscompose.ui.screens

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.utils.QRGenerator
import com.example.halalyticscompose.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyQRScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    
    val name = sessionManager.getFullName() ?: "User"
    val bloodType = sessionManager.getBloodType() ?: "N/A"
    val medicalHistory = sessionManager.getMedicalHistory() ?: "N/A"
    val allergies = sessionManager.getAllergy() ?: "N/A"
    val contact = sessionManager.getEmergencyContact() ?: "N/A"
    
    val qrContent = """
        EMERGENCY MEDICAL INFO
        Name: $name
        Blood Type: $bloodType
        Medical History: $medicalHistory
        Allergies: $allergies
        Emergency Contact: $contact
    """.trimIndent()
    
    val qrBitmap = remember { QRGenerator.generateQRCode(qrContent) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency medical QR", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tunjukkan QR ini kepada tenaga medis dalam keadaan darurat.",
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // QR Code Card
            Card(
                modifier = Modifier
                    .size(300.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Medical QR Code",
                            modifier = Modifier.size(260.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalInformation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Ringkasan Medis", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Golongan Darah: $bloodType", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Text("Alergi: $allergies", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Text("Kontak Darurat: $contact", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                }
            }
        }
    }
}
