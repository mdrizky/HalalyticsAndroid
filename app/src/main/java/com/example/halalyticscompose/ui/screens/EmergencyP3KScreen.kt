package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.DarkBackground
import com.example.halalyticscompose.ui.theme.DarkCard
import com.example.halalyticscompose.ui.viewmodel.EmergencyP3KViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyP3KScreen(
    navController: NavController,
    viewModel: EmergencyP3KViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val guidance by viewModel.guidance.collectAsState()
    val error by viewModel.error.collectAsState()

    var emergencyInput by remember { mutableStateOf("") }
    var isEmergencyMode by remember { mutableStateOf(false) } // To toggle red flash UI

    LaunchedEffect(isEmergencyMode) {
        if (isEmergencyMode) {
            delay(3000) // Stop flashing after AI responds or 3s
            isEmergencyMode = false
        }
    }

    val backgroundColor = if (isEmergencyMode) Color(0xFF4A0000) else MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusat Gawat Darurat AI") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Icon(
                Icons.Default.LocalHospital,
                contentDescription = "Emergency",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Apa keadaan daruratnya?",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "AI Medis akan memberikan panduan cepat & mengirim sinyal ke Admin RS terdekat",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = emergencyInput,
                onValueChange = { emergencyInput = it },
                label = { Text("Contoh: Anak tersedak bakso, Luka bakar minyak goreng") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.error,
                    focusedLabelColor = MaterialTheme.colorScheme.error
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (emergencyInput.isNotBlank()) {
                        isEmergencyMode = true
                        viewModel.triggerEmergency(emergencyInput)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onError, modifier = Modifier.size(24.dp))
                } else {
                    Text("TERIAK MINTA TOLONG (AI)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp), fontWeight = FontWeight.Bold)
            }

            if (guidance.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalHospital, contentDescription = "Medic", tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LANGKAH P3K KILAT", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(0.1f), modifier = Modifier.padding(vertical = 12.dp))
                        
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            itemsIndexed(guidance) { index, step ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(step, color = Color.White, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
