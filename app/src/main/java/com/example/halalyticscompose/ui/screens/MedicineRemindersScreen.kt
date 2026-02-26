package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.components.MainLayout
import com.example.halalyticscompose.ui.viewmodel.MedicineViewModel
import com.example.halalyticscompose.ui.viewmodel.MedicineViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.halalyticscompose.Data.Network.ApiConfig
import com.example.halalyticscompose.utils.SessionManager
import androidx.compose.ui.platform.LocalContext

@Composable
fun MedicineRemindersScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val viewModel: MedicineViewModel = viewModel(
        factory = MedicineViewModelFactory(
            apiService = ApiConfig.apiService,
            sessionManager = SessionManager.getInstance(context)
        )
    )
    val reminders by viewModel.reminders.collectAsState()
    val nextDoses by viewModel.nextDoses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // State untuk dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var reminderToDelete by remember { mutableStateOf<com.example.halalyticscompose.Data.Model.MedicationReminderItem?>(null) }
    var showSnoozeToast by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadUserReminders()
        viewModel.getNextDoses()
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog && reminderToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Pengingat") },
            text = {
                val title = reminderToDelete?.medicineName
                    ?.takeIf { it.isNotBlank() }
                    ?: reminderToDelete?.drug?.name
                    ?: "obat ini"
                Text("Apakah Anda yakin ingin menghapus pengingat untuk $title?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        reminderToDelete?.let { viewModel.deleteReminder(it.id) }
                        showDeleteDialog = false
                        reminderToDelete = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
    
    // Snooze Toast feedback
    LaunchedEffect(showSnoozeToast) {
        if (showSnoozeToast) {
            kotlinx.coroutines.delay(2000)
            showSnoozeToast = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Medicine Reminders",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { navController.navigate("medication_reminder_advanced") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next Doses Section
        if (nextDoses.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Next Doses",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    nextDoses.take(3).forEach { dose ->
                        NextDoseCard(dose = dose)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error Message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Reminders List
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (reminders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.AlarmOff,
                        contentDescription = "No Reminders",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Medicine Reminders",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the + button to add your first reminder",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("medication_reminder_advanced") }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Reminder")
                    }
                }
            }
        } else {
            Text(
                text = "Active Reminders (${reminders.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn {
                items(reminders) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onMarkTaken = { viewModel.markAsTaken(reminder.id) },
                        onEdit = { 
                            // Navigate ke Health Assistant dengan mode edit
                            navController.navigate("health_assistant")
                        },
                        onDelete = {
                            reminderToDelete = reminder
                            showDeleteDialog = true
                        },
                        onSnooze = {
                            // Simulasi snooze - dalam produksi akan reschedule alarm
                            showSnoozeToast = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Snooze feedback
            if (showSnoozeToast) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Snooze, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pengingat ditunda 15 menit")
                    }
                }
            }
        }
    }
}

@Composable
fun NextDoseCard(dose: com.example.halalyticscompose.Data.Model.NextDose) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dose.medicine_name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Next: ${dose.next_dose_time}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val info = dose.dose_info
                if (info != null) {
                    Text(
                        text = info,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
                }
            }
            
            Icon(
                Icons.Default.Alarm,
                contentDescription = "Alarm",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ReminderCard(
    reminder: com.example.halalyticscompose.Data.Model.MedicationReminderItem,
    onMarkTaken: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSnooze: () -> Unit = {}
) {
    var showOptionsMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val displayMedicineName = reminder.medicineName
                        .takeIf { it.isNotBlank() }
                        ?: reminder.drug?.name
                        ?: "Obat"
                    Text(
                        text = displayMedicineName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val symptoms = reminder.symptoms
                    if (symptoms != null) {
                        Text(
                            text = "For: $symptoms",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        )
                    }
                    
                    Text(
                        text = "Frequency: ${reminder.frequencyPerDay}x per day",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
                    
                    Text(
                        text = "Times: ${reminder.scheduleTimes?.joinToString(", ") ?: "-"}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
                    
                    Text(
                        text = "Started: ${reminder.startDate}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
                    )
                    
                    val endDate = reminder.endDate
                    if (endDate != null) {
                        Text(
                            text = "Until: $endDate",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
                        )
                    }
                    
                    val notes = reminder.notes
                    if (notes != null) {
                        Text(
                            text = "Notes: $notes",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Box {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    
                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mark as Taken") },
                            leadingIcon = {
                                Icon(Icons.Default.Check, contentDescription = null)
                            },
                            onClick = {
                                onMarkTaken()
                                showOptionsMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            },
                            onClick = {
                                onEdit()
                                showOptionsMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus") },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            },
                            onClick = {
                                onDelete()
                                showOptionsMenu = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Taken Times
            if (!reminder.takenTimes.isNullOrEmpty()) {
                Text(
                    text = "Taken Times:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                val times = reminder.takenTimes.orEmpty().takeLast(5)
                for (time in times) {
                    Text(
                        text = "• $time",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkTaken,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mark as Taken")
                }
                
                OutlinedButton(
                    onClick = onSnooze,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Snooze, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tunda")
                }
            }
        }
    }
}
