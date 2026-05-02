package com.example.halalyticscompose.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.data.model.MedicalRecordRequest
import com.example.halalyticscompose.ui.viewmodel.MedicalRecordsViewModel
import androidx.compose.ui.res.stringResource
import com.example.halalyticscompose.R
import com.example.halalyticscompose.utils.SessionManager
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalRecordsScreen(
    navController: NavController,
    viewModel: MedicalRecordsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val color = MaterialTheme.colorScheme
    
    val isLoading by viewModel.isLoading.collectAsState()
    val records by viewModel.records.collectAsState()
    val error by viewModel.error.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadRecords()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.medical_records_title),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = color.background,
                    titleContentColor = color.onBackground,
                    navigationIconContentColor = color.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = color.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record", tint = color.onPrimary)
            }
        },
        containerColor = color.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            if (isLoading && records.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = color.primary)
            } else if (!error.isNullOrBlank() && records.isEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = color.errorContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.medical_records_error_load), color = color.onErrorContainer, fontWeight = FontWeight.Bold)
                        Text(error.orEmpty(), color = color.onErrorContainer, style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { viewModel.loadRecords() }) {
                            Text(stringResource(R.string.error_retry))
                        }
                    }
                }
            } else if (records.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(color.primary.copy(alpha = 0.1f), RoundedCornerShape(32.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = color.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        stringResource(R.string.medical_records_empty_title),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium,
                        color = color.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.medical_records_empty_desc),
                        color = color.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(0.8f),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(stringResource(R.string.medical_records_add), fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(records) { record ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Show Detail */ },
                            colors = CardDefaults.cardColors(containerColor = color.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(color.primary.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = "Doc",
                                        tint = color.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        record.title,
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = color.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "${record.recordType} • ${record.recordDate}",
                                        color = color.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (!record.hospitalName.isNullOrEmpty()) {
                                        Text(
                                            record.hospitalName,
                                            color = color.primary,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold
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

    if (showAddDialog) {
        AddMedicalRecordDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, type, date, hospital, doctor, desc, base64Image ->
                val userId = sessionManager.getUserId()
                if(userId <= 0) return@AddMedicalRecordDialog
                viewModel.addRecord(
                    MedicalRecordRequest(
                        userId = userId,
                        recordType = type,
                        recordDate = date,
                        title = title,
                        hospitalName = hospital,
                        doctorName = doctor,
                        description = desc,
                        imageBase64 = base64Image
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddMedicalRecordDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Resep") }
    var hospital by remember { mutableStateOf("") }
    var doctor by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = sdf.format(Date())

    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageBitmap = uri?.let { loadBitmapFromUri(context, it) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.medical_records_add)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.medical_records_dialog_title)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Simplified type selection for brevity
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text(stringResource(R.string.medical_records_dialog_type)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = hospital,
                    onValueChange = { hospital = it },
                    label = { Text(stringResource(R.string.medical_records_dialog_hospital)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = doctor,
                    onValueChange = { doctor = it },
                    label = { Text(stringResource(R.string.medical_records_dialog_doctor)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { galleryLauncher.launch("image/*") }) {
                    Text(stringResource(R.string.medical_records_dialog_upload))
                }
                
                if (selectedImageBitmap != null) {
                    Text(stringResource(R.string.medical_records_photo_selected), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var base64: String? = null
                    selectedImageBitmap?.let { bmp ->
                        val stream = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                        base64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
                    }
                    onSave(title, type, currentDate, hospital, doctor, desc, base64)
                },
                enabled = title.isNotBlank() && type.isNotBlank()
            ) {
                Text(stringResource(R.string.common_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}

private fun loadBitmapFromUri(context: android.content.Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
    } catch (_: IOException) {
        null
    }
}
