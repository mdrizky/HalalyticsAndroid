package com.example.halalyticscompose.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.ui.viewmodel.MedicineViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthAssistantScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val viewModel: MedicineViewModel = hiltViewModel()
    var symptoms by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<com.example.halalyticscompose.Data.Model.MedicineData?>(null) }
    var showReminderDialog by remember { mutableStateOf(false) }

    val symptomsAnalysis by viewModel.symptomsAnalysis.collectAsState()
    val recommendedMedicines by viewModel.recommendedMedicines.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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
                        placeholder = { Text("e.g. I have a persistent headache and feel slightly nauseous...", color = MaterialTheme.colorScheme.onSurface.copy(0.6f)) },
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
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (symptoms.isNotBlank() && !isLoading) 
                                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
                                else 
                                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.onSurface.copy(0.1f), MaterialTheme.colorScheme.onSurface.copy(0.05f)))
                            )
                            .clickable(enabled = symptoms.isNotBlank() && !isLoading) {
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
                                Text("Analyze Symptoms", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Results Section
            val analysis = symptomsAnalysis
            if (showResults && analysis != null) {
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

                            if (analysis.possible_causes.isNotEmpty()) {
                                SectionTitle("Kemungkinan Penyebab", Icons.Outlined.Lightbulb, MaterialTheme.colorScheme.primary)
                                analysis.possible_causes.forEach { cause ->
                                    Text("• $cause", color = MaterialTheme.colorScheme.onSurface.copy(0.75f), fontSize = 13.sp, lineHeight = 20.sp)
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                            
                            SectionTitle("Halal & Safety Verification", Icons.Default.VerifiedUser, MaterialTheme.colorScheme.primary)
                            Text(analysis.halal_check?.notes ?: "Processing halal check...", color = MaterialTheme.colorScheme.onSurface.copy(0.7f), fontSize = 14.sp, lineHeight = 22.sp)
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            SectionTitle("Usage & Dosage Info", Icons.Outlined.Info, MaterialTheme.colorScheme.secondary)
                            Text(analysis.usage_instructions ?: "No usage guidance available.", color = MaterialTheme.colorScheme.onSurface.copy(0.7f), fontSize = 14.sp, lineHeight = 22.sp)
                            
                            if (!analysis.dosage_guidelines.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.onSurface.copy(0.03f))
                                        .padding(12.dp)
                                ) {
                                    Text(analysis.dosage_guidelines!!, color = MaterialTheme.colorScheme.onSurface.copy(0.9f), fontSize = 13.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                }
                            }

                            if (!analysis.doctor_recommendation.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(20.dp))
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
                                            if (analysis.should_seek_doctor) "Saran Dokter" else "Pantauan Kondisi",
                                            fontWeight = FontWeight.Bold,
                                            color = if (analysis.should_seek_doctor) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            analysis.doctor_recommendation ?: "",
                                            color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
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
