package com.example.halalyticscompose.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.halalyticscompose.data.model.User
import com.example.halalyticscompose.ui.viewmodel.AuthViewModel
import com.example.halalyticscompose.utils.SessionManager
import java.io.File
import java.io.FileOutputStream
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userData by viewModel.userData.collectAsState()
    val isLoadingVM by viewModel.isLoading.collectAsState()
    
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
    
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    var isInitialized by remember { mutableStateOf(false) }
    
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

    fun persistProfile(redirectAfterSave: Boolean = false, showSuccessToast: Boolean = false) {
        val imageFile = selectedImageUri?.let { uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                file
            } catch (e: Exception) { null }
        }

        viewModel.updateProfile(
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
                    navController.popBackStack()
                }
            },
            onError = { msg ->
                Toast.makeText(context, "Gagal simpan: $msg", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
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
                    TextButton(onClick = { persistProfile(redirectAfterSave = true, showSuccessToast = true) }) {
                        Text("Simpan")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoadingVM && !isInitialized) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ProfileHeaderSection(userData) }
                
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (selectedImageUri != null) {
                                    AsyncImage(model = selectedImageUri, contentDescription = null, modifier = Modifier.size(44.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(44.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Foto Profil", fontWeight = FontWeight.SemiBold)
                            }
                            OutlinedButton(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                                Text("Ganti")
                            }
                        }
                    }
                }
                
                item {
                    HealthProfileSection(
                        height = height, weight = weight, age = age, gender = gender, activityLevel = activityLevel, medicalHistory = medicalHistory,
                        onHeightChange = { height = it }, onWeightChange = { weight = it }, onAgeChange = { age = it }, onGenderChange = { gender = it },
                        onActivityLevelChange = { activityLevel = it }, onMedicalHistoryChange = { medicalHistory = it }
                    )
                }
                
                item {
                    DietaryPreferencesSection(selectedDiet = selectedDiet, allergyText = allergy, onDietChange = { selectedDiet = it }, onAllergyChange = { allergy = it })
                }
            }
        }
    }
}

// Re-using sub-composables from original file but keeping them simple for brevity in this refactor
@Composable
fun ProfileHeaderSection(userData: User?) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).background(Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))), CircleShape), contentAlignment = Alignment.Center) {
                Text(text = "👤", fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = userData?.fullName ?: "User", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = userData?.email ?: "user@halalytics.com", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun HealthProfileSection(height: String, weight: String, age: String, gender: String, activityLevel: String, medicalHistory: String, onHeightChange: (String) -> Unit, onWeightChange: (String) -> Unit, onAgeChange: (String) -> Unit, onGenderChange: (String) -> Unit, onActivityLevelChange: (String) -> Unit, onMedicalHistoryChange: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "🏃‍♂️ Health Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = height, onValueChange = onHeightChange, label = { Text("H (cm)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = weight, onValueChange = onWeightChange, label = { Text("W (kg)") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = age, onValueChange = onAgeChange, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text("Gender")
            Row {
                listOf("Male", "Female").forEach { opt ->
                    FilterChip(selected = gender == opt, onClick = { onGenderChange(opt) }, label = { Text(opt) }, modifier = Modifier.padding(end = 4.dp))
                }
            }
        }
    }
}

@Composable
fun DietaryPreferencesSection(selectedDiet: String, allergyText: String, onDietChange: (String) -> Unit, onAllergyChange: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "🥗 Dietary", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = allergyText, onValueChange = onAllergyChange, label = { Text("Allergies") }, modifier = Modifier.fillMaxWidth())
        }
    }
}
