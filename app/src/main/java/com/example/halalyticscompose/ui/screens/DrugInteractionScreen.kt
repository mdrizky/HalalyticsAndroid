package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.MushboohYellow
import com.example.halalyticscompose.ui.viewmodel.HealthAiViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugInteractionScreen(
    navController: NavController,
    viewModel: HealthAiViewModel = hiltViewModel()
) {
    var drugA by remember { mutableStateOf("") }
    var drugB by remember { mutableStateOf("") }
    
    val interactionResult by viewModel.interactionResult.collectAsState()
    val interactionSource by viewModel.interactionSource.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.drug_interaction_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header Illustration/Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        Icons.Default.SyncAlt,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).padding(horizontal = 8.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.4f)
                    )
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Text(
                text = "Periksa Interaksi Obat",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Pastikan obat yang Anda konsumsi aman untuk diminum bersamaan menggunakan analisis AI.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // Input Fields
            OutlinedTextField(
                value = drugA,
                onValueChange = { drugA = it },
                label = { Text("Nama Obat Pertama") },
                placeholder = { Text("Contoh: Paracetamol") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = drugB,
                onValueChange = { drugB = it },
                label = { Text("Nama Obat Kedua") },
                placeholder = { Text("Contoh: Ibuprofen") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.checkInteraction(drugAName = drugA, drugBName = drugB) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = drugA.isNotBlank() && drugB.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Cek Interaksi Sekarang", fontWeight = FontWeight.Bold)
                }
            }

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Result Area
            AnimatedVisibility(
                visible = interactionResult != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                interactionResult?.let { result ->
                    InteractionResultCard(result, interactionSource)
                }
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun InteractionResultCard(
    data: com.example.halalyticscompose.Data.Model.DrugInteractionData,
    source: String? = null
) {
    val severityColor = when (data.severity.lowercase()) {
        "contraindicated" -> MaterialTheme.colorScheme.error
        "major" -> MaterialTheme.colorScheme.error
        "moderate" -> MushboohYellow // Custom theme color
        "minor" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(severityColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (data.hasInteraction) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = severityColor
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = if (data.hasInteraction) "Interaksi Terdeteksi" else "Aman untuk Diminum Bersama",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tingkat Keparahan: ${data.severity.uppercase()}",
                        fontSize = 12.sp,
                        color = severityColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!source.isNullOrBlank()) {
                        Text(
                            text = "Sumber: $source",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(0.05f))
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Analisis AI:",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                fontSize = 14.sp
            )
            
            Text(
                text = data.description,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (!data.recommendation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(0.05f))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Rekomendasi Medis:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = data.recommendation,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            if (!data.scientificBasis.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Dasar Ilmiah:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.4f),
                    lineHeight = 16.sp
                )
                Text(
                    text = data.scientificBasis,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.4f),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (!data.sources.isNullOrEmpty() || !data.disclaimer.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(0.08f))
                Spacer(modifier = Modifier.height(10.dp))
                if (!data.sources.isNullOrEmpty()) {
                    Text(
                        text = "Data source: ${data.sources.joinToString(", ")}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.55f)
                    )
                }
                if (!data.disclaimer.isNullOrBlank()) {
                    Text(
                        text = data.disclaimer ?: "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.55f),
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
