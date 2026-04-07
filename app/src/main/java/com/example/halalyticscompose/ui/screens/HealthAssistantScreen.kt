package com.example.halalyticscompose.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.ui.viewmodel.MedicineViewModel
import com.example.halalyticscompose.utils.VoiceRecognitionHelper
import com.example.halalyticscompose.utils.TextToSpeechHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthAssistantScreen(
    navController: NavController,
    initialSymptom: String? = null
) {
    val context = LocalContext.current
    val viewModel: MedicineViewModel = hiltViewModel()
    var symptoms by remember { mutableStateOf(initialSymptom ?: "") }
    var showResults by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<com.example.halalyticscompose.Data.Model.MedicineData?>(null) }
    var showReminderDialog by remember { mutableStateOf(false) }

    // Voice Note STT/TTS state
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var voiceStatus by remember { mutableStateOf("") }
    val voiceHelper = remember { VoiceRecognitionHelper(context) }
    val ttsHelper = remember {
        TextToSpeechHelper(context).also { helper ->
            helper.onSpeakingStarted = { isSpeaking = true }
            helper.onSpeakingDone = { isSpeaking = false }
        }
    }

    // Mic pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.35f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Runtime permission launcher for RECORD_AUDIO
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            isListening = true
            voiceStatus = "Mendengarkan..."
            voiceHelper.startListening(
                onResult = { result ->
                    symptoms = result
                    isListening = false
                    voiceStatus = "✓ Suara terdeteksi"
                },
                onError = { msg ->
                    isListening = false
                    voiceStatus = msg
                },
                onPartial = { partial -> symptoms = partial },
                onListeningStarted = { isListening = true },
                onListeningEnded = { isListening = false }
            )
        } else {
            Toast.makeText(context, "Izin mikrofon diperlukan untuk fitur suara", Toast.LENGTH_SHORT).show()
        }
    }

    // Auto-read AI response with TTS
    val symptomsAnalysis by viewModel.symptomsAnalysis.collectAsState()
    LaunchedEffect(symptomsAnalysis) {
        symptomsAnalysis?.let { analysis ->
            ttsHelper.speakDiagnosisReport(
                condition = analysis.condition,
                severity = analysis.severity,
                causes = analysis.possible_causes,
                recommendation = analysis.lifestyle_advice ?: analysis.recommendation,
                medicines = analysis.recommended_medicines_list,
                shouldSeeDoctor = analysis.should_seek_doctor
            )
        }
    }

    val recommendedMedicines by viewModel.recommendedMedicines.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            voiceHelper.destroy()
            ttsHelper.shutdown()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserReminders()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI Medical Assistant", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.padding(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurface.copy(0.05f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("medicine_reminders") },
                        modifier = Modifier.padding(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurface.copy(0.05f))
                    ) {
                        Icon(Icons.Outlined.NotificationsActive, "Reminders", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Icon(
                        Icons.Default.MedicalServices, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.onPrimary, 
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "How are you feeling today?", 
                        style = MaterialTheme.typography.headlineSmall, 
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Describe your symptoms below for an instant AI-powered health analysis and halal medicine recommendations.", 
                        style = MaterialTheme.typography.bodySmall, 
                        color = MaterialTheme.colorScheme.onPrimary.copy(0.8f),
                        lineHeight = 18.sp
                    )
                }
            }

            // Input Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.05f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    OutlinedTextField(
                        value = symptoms,
                        onValueChange = { symptoms = it },
                        placeholder = { Text("Ceritakan keluhan Anda atau klik mic...", color = MaterialTheme.colorScheme.onSurface.copy(0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 6,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(0.1f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    // Voice status indicator
                    if (voiceStatus.isNotEmpty()) {
                        Text(
                            text = voiceStatus,
                            fontSize = 11.sp,
                            color = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.5f),
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Voice Note + Analyze buttons row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 🎤 Mic Button with pulse animation
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .scale(if (isListening) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(
                                    if (isListening) Color(0xFFE53935)
                                    else MaterialTheme.colorScheme.primary.copy(0.15f)
                                )
                                .clickable {
                                    if (isListening) {
                                        voiceHelper.stopListening()
                                        isListening = false
                                        voiceStatus = "Dihentikan"
                                    } else {
                                        // Stop TTS if it's speaking
                                        ttsHelper.stop()
                                        // Check permission first
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                            isListening = true
                                            voiceStatus = "Mendengarkan..."
                                            voiceHelper.startListening(
                                                onResult = { result ->
                                                    symptoms = result
                                                    isListening = false
                                                    voiceStatus = "✓ Suara terdeteksi"
                                                },
                                                onError = { msg ->
                                                    isListening = false
                                                    voiceStatus = msg
                                                },
                                                onPartial = { partial -> symptoms = partial },
                                                onListeningStarted = { isListening = true },
                                                onListeningEnded = { isListening = false }
                                            )
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isListening) "Stop" else "Voice Note",
                                tint = if (isListening) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Analyze button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (symptoms.isNotBlank() && !isLoading) 
                                        Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
                                    else 
                                        Brush.linearGradient(listOf(MaterialTheme.colorScheme.onSurface.copy(0.1f), MaterialTheme.colorScheme.onSurface.copy(0.05f)))
                                )
                                .clickable(enabled = symptoms.isNotBlank() && !isLoading) {
                                    ttsHelper.stop()
                                    viewModel.analyzeSymptoms(symptoms)
                                    showResults = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Analyze", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // 🔊 TTS Toggle button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSpeaking) MaterialTheme.colorScheme.secondary.copy(0.2f)
                                    else MaterialTheme.colorScheme.onSurface.copy(0.05f)
                                )
                                .clickable {
                                    if (isSpeaking) {
                                        ttsHelper.stop()
                                        isSpeaking = false
                                    } else {
                                        // Re-read the last analysis (full report)
                                        symptomsAnalysis?.let { analysis ->
                                            ttsHelper.speakDiagnosisReport(
                                                condition = analysis.condition,
                                                severity = analysis.severity,
                                                causes = analysis.possible_causes,
                                                recommendation = analysis.lifestyle_advice ?: analysis.recommendation,
                                                medicines = analysis.recommended_medicines_list,
                                                shouldSeeDoctor = analysis.should_seek_doctor
                                            )
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "TTS",
                                tint = if (isSpeaking) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(0.5f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            errorMessage?.takeIf { it.isNotBlank() }?.let { message ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.10f))
                        .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Results Section
            val analysis = symptomsAnalysis
            if (showResults && analysis != null) {
                // TTS indicator
                if (isSpeaking) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VolumeUp, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sedang membacakan hasil analisis...", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    // Emergency Alert
                    if (analysis.emergency_warning != null || analysis.severity.equals("emergency", ignoreCase = true)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.error.copy(0.1f))
                                .border(1.dp, MaterialTheme.colorScheme.error.copy(0.3f), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.GppBad, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("URGENT WARNING", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                    Text(analysis.emergency_warning ?: "Seek immediate professional help.", color = MaterialTheme.colorScheme.onSurface.copy(0.8f), fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { navController.navigate("emergency_p3k") },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Icon(Icons.Default.LocalHospital, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Buka Panduan IGD")
                                    }
                                }
                            }
                        }
                    }

                    // Analysis Report Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.05f), RoundedCornerShape(28.dp))
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("DIAGNOSIS REPORT", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(analysis.condition, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val severityColor = when (analysis.severity.lowercase()) {
                                "mild" -> MaterialTheme.colorScheme.primary
                                "moderate" -> MushboohYellow
                                else -> MaterialTheme.colorScheme.error
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(severityColor.copy(0.1f))
                                    .border(1.dp, severityColor.copy(0.2f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(analysis.severity.uppercase(), color = severityColor, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(0.05f))
                            Spacer(modifier = Modifier.height(24.dp))

                            // 1. Akar Masalah (Why it happened)
                            if (!analysis.why_it_happened.isNullOrBlank()) {
                                SectionTitle("Mengapa ini terjadi?", Icons.Outlined.Science, MaterialTheme.colorScheme.primary)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(0.05f))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = analysis.why_it_happened!!,
                                        color = MaterialTheme.colorScheme.onSurface.copy(0.85f),
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            // 2. Gejala Terkait
                            if (analysis.gejala_terkait.isNotEmpty() || analysis.possible_causes.isNotEmpty()) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    if (analysis.gejala_terkait.isNotEmpty()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            SectionTitle("Gejala Terkait", Icons.Outlined.MonitorHeart, MaterialTheme.colorScheme.secondary)
                                            analysis.gejala_terkait.forEach { symptom ->
                                                Text("• $symptom", color = MaterialTheme.colorScheme.onSurface.copy(0.75f), fontSize = 13.sp, lineHeight = 20.sp)
                                            }
                                        }
                                    }
                                    if (analysis.possible_causes.isNotEmpty()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            SectionTitle("Penyebab Potensial", Icons.Outlined.Lightbulb, MaterialTheme.colorScheme.primary)
                                            analysis.possible_causes.forEach { cause ->
                                                Text("• $cause", color = MaterialTheme.colorScheme.onSurface.copy(0.75f), fontSize = 13.sp, lineHeight = 20.sp)
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(0.05f))
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // 3. Saran Gaya Hidup & Pencegahan
                            if (!analysis.lifestyle_advice.isNullOrBlank() || !analysis.future_prevention.isNullOrBlank()) {
                                SectionTitle("Saran Penanganan & Pencegahan", Icons.Outlined.Spa, MaterialTheme.colorScheme.secondary)
                                if (!analysis.lifestyle_advice.isNullOrBlank()) {
                                    Text("Penanganan Awal:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                                    Text(analysis.lifestyle_advice!!, color = MaterialTheme.colorScheme.onSurface.copy(0.75f), fontSize = 13.sp, lineHeight = 20.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                if (!analysis.future_prevention.isNullOrBlank()) {
                                    Text("Pencegahan Masa Depan:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                                    Text(analysis.future_prevention!!, color = MaterialTheme.colorScheme.onSurface.copy(0.75f), fontSize = 13.sp, lineHeight = 20.sp)
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(0.05f))
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // 4. Rekomendasi Terapi (Obat Paten & Generik)
                            if (analysis.recommended_medicines_list.isNotEmpty() || analysis.alternative_medicines.isNotEmpty()) {
                                SectionTitle("Rekomendasi Terapi", Icons.Outlined.Medication, MaterialTheme.colorScheme.primary)
                                
                                if (analysis.medicine_categories.isNotEmpty()) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                                        analysis.medicine_categories.forEach { category ->
                                            Box(
                                                modifier = Modifier
                                                    .padding(end = 8.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(category, color = MaterialTheme.colorScheme.onPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    if (analysis.recommended_medicines_list.isNotEmpty()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Obat Apotek (Paten/Generik):", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
                                            analysis.recommended_medicines_list.forEach { med ->
                                                Text("• $med", color = MaterialTheme.colorScheme.onSurface.copy(0.8f), fontSize = 13.sp)
                                            }
                                        }
                                    }
                                    if (analysis.alternative_medicines.isNotEmpty()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Alternatif / Herbal:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
                                            analysis.alternative_medicines.forEach { alt ->
                                                Text("• $alt", color = MaterialTheme.colorScheme.onSurface.copy(0.8f), fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                                
                                // Halal Verification Sub-box
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (analysis.halal_check?.status?.lowercase() == "halal") MaterialTheme.colorScheme.secondary.copy(0.1f) else MushboohYellow.copy(0.1f))
                                        .border(1.dp, if (analysis.halal_check?.status?.lowercase() == "halal") MaterialTheme.colorScheme.secondary.copy(0.3f) else MushboohYellow.copy(0.3f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.VerifiedUser, null, tint = if (analysis.halal_check?.status?.lowercase() == "halal") MaterialTheme.colorScheme.secondary else MushboohYellow, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Verifikasi Halal: ${analysis.halal_check?.status?.uppercase() ?: "UNKNOWN"}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                            Text(analysis.halal_check?.notes ?: "Processing...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // 5. Kartu Dosis & Efek Samping
                            if (!analysis.dosage_guidelines.isNullOrBlank() || !analysis.when_to_take_and_frequency.isNullOrBlank() || analysis.side_effects.isNotEmpty()) {
                                SectionTitle("Aturan Pakai & Perhatian Khusus", Icons.Outlined.Info, MaterialTheme.colorScheme.primary)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.onSurface.copy(0.03f))
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        if (!analysis.dosage_guidelines.isNullOrBlank()) {
                                            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                                                Icon(Icons.Outlined.Scale, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Dosis: ${analysis.dosage_guidelines}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.9f))
                                            }
                                        }
                                        if (!analysis.when_to_take_and_frequency.isNullOrBlank()) {
                                            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                                                Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Waktu: ${analysis.when_to_take_and_frequency}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.9f), fontWeight = FontWeight.Medium)
                                            }
                                        }
                                        if (!analysis.usage_instructions.isNullOrBlank()) {
                                            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                                                Icon(Icons.Outlined.IntegrationInstructions, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Instruksi: ${analysis.usage_instructions}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.9f))
                                            }
                                        }
                                        if (analysis.side_effects.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.Top) {
                                                Icon(Icons.Outlined.WarningAmber, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text("Efek Samping Potensial:", fontSize = 13.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
                                                    analysis.side_effects.forEach { effect ->
                                                        Text("• $effect", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                            
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(0.05f))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 6. Penutup / Rekomendasi Dokter
                            if (!analysis.triage_action.isNullOrBlank() || analysis.should_seek_doctor) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (analysis.should_seek_doctor) MaterialTheme.colorScheme.error.copy(0.10f)
                                            else MaterialTheme.colorScheme.primary.copy(0.08f)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            if (analysis.should_seek_doctor) "Saran Dokter Segera" else "Pantauan Kondisi",
                                            fontWeight = FontWeight.Bold,
                                            color = if (analysis.should_seek_doctor) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            analysis.doctor_recommendation ?: analysis.triage_action ?: analysis.recommendation,
                                            color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            if (analysis.should_seek_doctor) {
                                                Button(
                                                    onClick = { navController.navigate("emergency_p3k") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                                ) {
                                                    Icon(Icons.Default.LocalHospital, null)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Ke IGD/Dokter")
                                                }
                                            } else {
                                                OutlinedButton(onClick = { navController.navigate("medicine_reminders") }) {
                                                    Icon(Icons.Default.Alarm, null)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Atur Pengingat Obat")
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Disclaimer: Analisis AI ini hanya edukasi awal, bukan diagnosis dokter.",
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    if (recommendedMedicines.isNotEmpty()) {
                        Text("Recommended Halal Medicines", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        recommendedMedicines.forEach { medicine ->
                            MedicineCardPremium(
                                medicine = medicine,
                                onSetReminder = {
                                    selectedMedicine = medicine
                                    showReminderDialog = true
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Reuse or update ReminderDialog
    if (showReminderDialog && selectedMedicine != null) {
        ReminderDialogPremium(
            medicine = selectedMedicine!!,
            onDismiss = { showReminderDialog = false },
            onConfirm = { freq, start, end, notes ->
                viewModel.createReminder(selectedMedicine!!.idMedicine ?: 0, symptoms, freq, start, end, notes)
                showReminderDialog = false
                Toast.makeText(context, "Reminder set for ${selectedMedicine!!.name}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MedicineCardPremium(
    medicine: com.example.halalyticscompose.Data.Model.MedicineData,
    onSetReminder: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Medication, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(medicine.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(medicine.brandName ?: "Generic", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(0.1f))
                        .clickable { onSetReminder() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddAlarm, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Set Reminder", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("HALAL", color = MaterialTheme.colorScheme.onPrimary, fontSize = 9.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialogPremium(
    medicine: com.example.halalyticscompose.Data.Model.MedicineData,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String?, String?) -> Unit
) {
    var frequency by remember { mutableStateOf("3") }
    var startDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var notes by remember { mutableStateOf("") }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.05f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Schedule Dose", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                Text(medicine.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Doses per day") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(0.1f))
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(0.1f))
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onConfirm(frequency.toIntOrNull() ?: 3, startDate, null, notes) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Save Reminder", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
