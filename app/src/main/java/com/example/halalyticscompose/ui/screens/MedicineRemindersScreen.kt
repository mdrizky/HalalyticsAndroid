package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.viewmodel.MedicineViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

// ═══════════════════════════════════════════════════════════════════
// COLOR CONSTANTS — Emerald Forest Premium
// ═══════════════════════════════════════════════════════════════════
private val EmeraldDark = Color(0xFF004D40)
private val EmeraldMedium = Color(0xFF00695C)
private val EmeraldLight = Color(0xFF26A69A)
private val SageBg = Color(0xFFF4F9F8)
private val SoftSage = Color(0xFFE0F2F1)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF212121)
private val TextMedium = Color(0xFF757575)
private val TextLight = Color(0xFF9E9E9E)

@Composable
fun MedicineRemindersScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val viewModel: MedicineViewModel = hiltViewModel()
    val reminders by viewModel.reminders.collectAsState()
    val nextDoses by viewModel.nextDoses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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
            title = {
                Text("Hapus Pengingat", fontWeight = FontWeight.Bold, color = TextDark)
            },
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
                    Text("Hapus", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = EmeraldDark)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Snooze Toast feedback
    LaunchedEffect(showSnoozeToast) {
        if (showSnoozeToast) {
            kotlinx.coroutines.delay(2000)
            showSnoozeToast = false
        }
    }

    Scaffold(containerColor = SageBg) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── EMERALD GRADIENT HEADER ──
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldDark, EmeraldMedium, EmeraldLight)
                            )
                        )
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 28.dp)
                ) {
                    Column {
                        // Back + Title Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { navController.navigateUp() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, null,
                                    tint = Color.White, modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                "Pengingat Obat",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { navController.navigate("add_medicine_reminder") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add, null,
                                    tint = Color.White, modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Stats Row
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.18f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "💊",
                                        fontSize = 22.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "${reminders.size}",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        "Aktif",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp)
                                        .background(Color.White.copy(alpha = 0.3f))
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "⏰",
                                        fontSize = 22.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "${nextDoses.size}",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        "Jadwal Berikutnya",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── NEXT DOSES SECTION ──
            if (nextDoses.isNotEmpty()) {
                item {
                    Text(
                        "Jadwal Berikutnya",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextDark,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(nextDoses.take(3)) { dose ->
                    NextDoseCard(dose = dose)
                }
            }

            // ── ERROR MESSAGE ──
            errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFD32F2F).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Error, null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = error,
                                color = Color(0xFFD32F2F),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // ── LOADING STATE ──
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EmeraldDark)
                    }
                }
            }
            // ── EMPTY STATE ──
            else if (reminders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(SoftSage),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💊", fontSize = 32.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Belum Ada Pengingat",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Buat pengingat obat pertama Anda untuk\ntidak lupa minum obat tepat waktu",
                                fontSize = 13.sp,
                                color = TextMedium,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { navController.navigate("add_medicine_reminder") },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                modifier = Modifier.height(46.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add, null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Buat Pengingat Baru",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
            // ── REMINDERS LIST ──
            else {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Pengingat Aktif (${reminders.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextDark
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SoftSage)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Hari Ini",
                                color = EmeraldDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                items(reminders) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onMarkTaken = { viewModel.markAsTaken(reminder.id) },
                        onEdit = {
                            navController.navigate("health_assistant")
                        },
                        onDelete = {
                            reminderToDelete = reminder
                            showDeleteDialog = true
                        },
                        onSnooze = {
                            showSnoozeToast = true
                        }
                    )
                }
            }

            // ── SNOOZE TOAST ──
            if (showSnoozeToast) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftSage),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Snooze, null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Pengingat ditunda 15 menit",
                                color = EmeraldDark,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// NEXT DOSE CARD — Emerald Forest
// ═══════════════════════════════════════════════════════════════════

@Composable
fun NextDoseCard(dose: com.example.halalyticscompose.Data.Model.NextDose) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldDark.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Alarm, null,
                    tint = EmeraldDark,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    dose.medicine_name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Selanjutnya: ${dose.next_dose_time}",
                    fontSize = 12.sp,
                    color = EmeraldMedium,
                    fontWeight = FontWeight.Medium
                )
                val info = dose.dose_info
                if (info != null) {
                    Text(
                        info,
                        fontSize = 11.sp,
                        color = TextLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SoftSage)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    "⏰",
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// REMINDER CARD — Emerald Forest Premium
// ═══════════════════════════════════════════════════════════════════

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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftSage),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💊", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        val displayName = reminder.medicineName
                            .takeIf { it.isNotBlank() }
                            ?: reminder.drug?.name
                            ?: "Obat"
                        Text(
                            displayName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        val symptoms = reminder.symptoms
                        if (symptoms != null) {
                            Text(
                                "Untuk: $symptoms",
                                fontSize = 12.sp,
                                color = TextMedium
                            )
                        }
                    }
                }

                Box {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert, null,
                            tint = TextLight
                        )
                    }
                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tandai Sudah Minum") },
                            leadingIcon = {
                                Icon(Icons.Default.Check, null, tint = EmeraldDark)
                            },
                            onClick = {
                                onMarkTaken()
                                showOptionsMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, null, tint = EmeraldMedium)
                            },
                            onClick = {
                                onEdit()
                                showOptionsMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = Color(0xFFD32F2F)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete, null,
                                    tint = Color(0xFFD32F2F)
                                )
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

            // Info chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SoftSage
                ) {
                    Text(
                        "${reminder.frequencyPerDay}x/hari",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF8E1)
                ) {
                    Text(
                        reminder.scheduleTimes?.joinToString(", ") ?: "-",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF57C00)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Date info
            Row {
                Text(
                    "Mulai: ${reminder.startDate}",
                    fontSize = 11.sp,
                    color = TextLight
                )
                val endDate = reminder.endDate
                if (endDate != null) {
                    Text(
                        " · Sampai: $endDate",
                        fontSize = 11.sp,
                        color = TextLight
                    )
                }
            }

            val notes = reminder.notes
            if (notes != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "📝 $notes",
                    fontSize = 11.sp,
                    color = EmeraldMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Taken Times
            if (!reminder.takenTimes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Riwayat Minum:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )
                val times = reminder.takenTimes.orEmpty().takeLast(3)
                times.forEach { time ->
                    Text(
                        "  ✓ $time",
                        fontSize = 10.sp,
                        color = TextLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onMarkTaken,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Icon(
                        Icons.Default.Check, null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Sudah Minum",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(
                    onClick = onSnooze,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EmeraldDark.copy(alpha = 0.3f))
                ) {
                    Icon(
                        Icons.Default.Snooze, null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Tunda",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )
                }
            }
        }
    }
}
