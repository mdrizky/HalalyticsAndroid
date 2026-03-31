package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class CertVerifyResult(
    val valid: Boolean,
    val certificateNumber: String,
    val productName: String,
    val manufacturer: String,
    val issuingBody: String,
    val issuedAt: String,
    val expiresAt: String,
    val status: String,
    val daysUntilExpiry: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalalCertVerifyScreen(
    navController: NavController,
    onVerify: (String) -> Unit = {},
    result: CertVerifyResult? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var certNumber by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verifikasi Sertifikat Halal") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ──────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00695C), Color(0xFF4DB6AC))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌙", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Cek Keaslian Sertifikat",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "MUI • LPPOM • BPJPH",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Input Field ──────────────────────
            OutlinedTextField(
                value = certNumber,
                onValueChange = { certNumber = it.uppercase() },
                label = { Text("Nomor Sertifikat") },
                placeholder = { Text("Contoh: MUI-2024-12345") },
                leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (certNumber.isNotBlank()) {
                            focusManager.clearFocus()
                            onVerify(certNumber.trim())
                        }
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (certNumber.isNotBlank()) {
                        focusManager.clearFocus()
                        onVerify(certNumber.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = certNumber.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Memverifikasi...")
                } else {
                    Icon(Icons.Default.Verified, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verifikasi Sertifikat")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Result ──────────────────────
            AnimatedVisibility(
                visible = result != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 30 })
            ) {
                result?.let { cert ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (cert.valid) Color(0xFF1B5E20).copy(alpha = 0.08f)
                            else Color(0xFFC62828).copy(alpha = 0.08f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (cert.valid) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (cert.valid) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (cert.valid) "SERTIFIKAT VALID" else "SERTIFIKAT ${cert.status.uppercase()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (cert.valid) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                            CertInfoRow("Produk", cert.productName)
                            CertInfoRow("Produsen", cert.manufacturer)
                            CertInfoRow("No. Sertifikat", cert.certificateNumber)
                            CertInfoRow("Diterbitkan", cert.issuedAt)
                            CertInfoRow("Berlaku Hingga", cert.expiresAt)
                            CertInfoRow("Penerbit", cert.issuingBody)

                            if (cert.valid && cert.daysUntilExpiry <= 90) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF57C00).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        "⚠️ Berlaku ${cert.daysUntilExpiry} hari lagi",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 13.sp,
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Error ──────────────────────
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn()
            ) {
                errorMessage?.let { msg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFC62828).copy(alpha = 0.08f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFC62828))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(msg, fontSize = 14.sp, color = Color(0xFFC62828))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CertInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
