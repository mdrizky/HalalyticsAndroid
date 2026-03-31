package com.example.halalyticscompose.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.Data.Model.User
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import com.example.halalyticscompose.utils.SessionManager
import java.io.File
import java.io.FileOutputStream
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userData by mainViewModel.userData.collectAsState()
    
    // Simple state management populated from current user data
    var isLoading by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("70") }
    var age by remember { mutableStateOf("28") }
    var selectedDiet by remember { mutableStateOf("None") }
    var gender by remember { mutableStateOf("Male") }
    var activityLevel by remember { mutableStateOf("medium") }
    var allergy by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    
    // Photo picker state
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }
    
    // Sync state when userData arrives
    LaunchedEffect(userData) {
        userData?.let { user ->
            if (!isInitialized) {
                fullName = user.fullName ?: ""
                phone = user.phone ?: ""
                height = user.height?.toString() ?: "175"
                weight = user.weight?.toString() ?: "70"
                age = user.age?.toString() ?: "28"
                selectedDiet = user.dietPreference ?: "None"
                gender = user.gender ?: "Male"
                activityLevel = user.activityLevel ?: "medium"
                allergy = user.allergy ?: ""
                medicalHistory = user.medicalHistory ?: ""
                bio = user.bio ?: ""
                isInitialized = true
            }
        }
    }

    fun buildSelectedImageFile(): File? {
        return selectedImageUri?.let { uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                file
            } catch (e: Exception) {
                null
            }
        }
    }

    fun persistProfile(redirectAfterSave: Boolean = false, showSuccessToast: Boolean = false) {
        val imageFile = buildSelectedImageFile()

        mainViewModel.updateProfile(
            fullName = fullName,
            phone = phone,
            height = height.toDoubleOrNull(),
            weight = weight.toDoubleOrNull(),
            age = age.toIntOrNull(),
            dietPreference = selectedDiet,
            gender = gender,
            activityLevel = activityLevel,
            allergy = allergy,
            medicalHistory = medicalHistory,
            bio = bio,
            image = imageFile,
            onSuccess = {
                if (showSuccessToast) {
                    Toast.makeText(context, "Profil berhasil diperbarui ✓", Toast.LENGTH_SHORT).show()
                }
                if (redirectAfterSave) {
                    navController.navigate("profile_status")
                }
            },
            onError = { msg ->
                Toast.makeText(context, "Gagal save: $msg", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    // Auto-save debouncer
    LaunchedEffect(fullName, phone, height, weight, age, selectedDiet, gender, activityLevel, allergy, medicalHistory, bio, selectedImageUri) {
        if (isInitialized) {
            kotlinx.coroutines.delay(1000)

            persistProfile()
        }
    }
    
    // Function to show toast
    fun showToast(message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
    
    // Delete Account Confirmation Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Hapus Akun", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)) },
            text = { 
                Column {
                    Text("Apakah Anda yakin ingin menghapus akun?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tindakan ini tidak dapat dibatalkan. Semua data Anda akan dihapus secara permanen.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        // Clear session and navigate to login
                        SessionManager.getInstance(context).logout()
                        showToast("Akun berhasil dihapus")
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Ya, Hapus Akun")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
    
    // Removed save success dialog as it is auto-save now
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            persistProfile(
                                redirectAfterSave = true,
                                showSuccessToast = true
                            )
                        }
                    ) {
                        Text("Simpan")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Header
                item {
                    ProfileHeaderSection(userData)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Preview foto profil",
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(44.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Foto Profil", fontWeight = FontWeight.SemiBold)
                            }
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                }
                            ) {
                                Text("Ganti")
                            }
                        }
                    }
                }
                
                // Health Profile
                item {
                    HealthProfileSection(
                        height = height,
                        weight = weight,
                        age = age,
                        gender = gender,
                        activityLevel = activityLevel,
                        medicalHistory = medicalHistory,
                        onHeightChange = { height = it },
                        onWeightChange = { weight = it },
                        onAgeChange = { age = it },
                        onGenderChange = { gender = it },
                        onActivityLevelChange = { activityLevel = it },
                        onMedicalHistoryChange = { medicalHistory = it }
                    )
                }
                
                // Dietary Preferences
                item {
                    DietaryPreferencesSection(
                        selectedDiet = selectedDiet,
                        allergyText = allergy,
                        onDietChange = { selectedDiet = it },
                        onAllergyChange = { allergy = it }
                    )
                }
                
                // User Stats
                item {
                    StatsSection()
                }
                
                // Account Settings
                item {
                    AccountSettingsSection(
                        navController = navController,
                        onDeleteAccountClick = { showDeleteAccountDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderSection(userData: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Photo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userData?.fullName ?: "User",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = userData?.email ?: "user@halalytics.com",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Indonesia",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = "Language",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Bahasa Indonesia",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthProfileSection(
    height: String,
    weight: String,
    age: String,
    gender: String,
    activityLevel: String,
    medicalHistory: String,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onActivityLevelChange: (String) -> Unit,
    onMedicalHistoryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏃‍♂️ Health Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Height, Weight, Age
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = height,
                    onValueChange = onHeightChange,
                    label = { Text("Height (cm)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = weight,
                    onValueChange = onWeightChange,
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = age,
                    onValueChange = onAgeChange,
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Gender
            Text(
                text = "Gender",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Male", "Female", "Other").forEach { option ->
                    FilterChip(
                        selected = gender == option,
                        onClick = { onGenderChange(option) },
                        label = { Text(option) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Activity Level
            Text(
                text = "Activity Level",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Low", "Medium", "High").forEach { level ->
                    FilterChip(
                        selected = activityLevel == level.lowercase(),
                        onClick = { onActivityLevelChange(level.lowercase()) },
                        label = { Text(level) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Medical History
            OutlinedTextField(
                value = medicalHistory,
                onValueChange = onMedicalHistoryChange,
                label = { Text("Riwayat Medis (misal: Diabetes, Hipertensi)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tuliskan riwayat medis Anda di sini...") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // BMI Display
            if (height.isNotBlank() && weight.isNotBlank()) {
                val heightM = height.toDoubleOrNull()?.div(100) ?: 0.0
                val weightKg = weight.toDoubleOrNull() ?: 0.0
                val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0.0
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            bmi < 18.5 -> Color(0xFF3B82F6) // Underweight
                            bmi < 25 -> Color(0xFF10B981) // Normal
                            bmi < 30 -> Color(0xFFF59E0B) // Overweight
                            else -> Color(0xFFEF4444) // Obese
                        }.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BMI: ${String.format("%.1f", bmi)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                bmi < 18.5 -> Color(0xFF3B82F6)
                                bmi < 25 -> Color(0xFF10B981)
                                bmi < 30 -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            }
                        )
                        Text(
                            text = when {
                                bmi < 18.5 -> "Underweight"
                                bmi < 25 -> "Normal weight"
                                bmi < 30 -> "Overweight"
                                else -> "Obese"
                            },
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DietaryPreferencesSection(
    selectedDiet: String,
    allergyText: String,
    onDietChange: (String) -> Unit,
    onAllergyChange: (String) -> Unit
) {
    var selectedAllergies by remember { mutableStateOf(setOf("Peanuts", "Shellfish")) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🥗 Dietary Preferences",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Diet Type
            Text(
                text = "Diet Type",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val dietOptions = listOf("None", "Vegetarian", "Vegan", "Diabetes-friendly", "Low-sugar", "Low-fat")
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                dietOptions.forEach { diet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDietChange(diet) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDiet == diet,
                            onClick = { onDietChange(diet) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(diet)
                    }
                }
            }
            
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Allergies Text Input
            OutlinedTextField(
                value = allergyText,
                onValueChange = onAllergyChange,
                label = { Text("Alergi (misal: Kacang, Susu, Seafood)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tuliskan semua alergi Anda...") }
            )
        }
    }
}

@Composable
fun StatsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏆 Your Stats",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.QrCodeScanner,
                    label = "Total Scans",
                    value = "234"
                )
                StatItem(
                    icon = Icons.Default.Upload,
                    label = "Contributions",
                    value = "45"
                )
                StatItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Days Active",
                    value = "42"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF3B82F6),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun AccountSettingsSection(
    navController: NavController,
    onDeleteAccountClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "⚙️ Account Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Button(
                onClick = { /* Change password */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B7280)
                )
            ) {
                Icon(Icons.Default.Lock, "Change Password")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Password")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onDeleteAccountClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626)
                )
            ) {
                Icon(Icons.Default.Delete, "Hapus Akun")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hapus Akun Permanen")
            }
        }
    }
}
