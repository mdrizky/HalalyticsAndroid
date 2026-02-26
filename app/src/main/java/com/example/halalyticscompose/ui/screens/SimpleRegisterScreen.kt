package com.example.halalyticscompose.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleRegisterScreen(
    navController: NavController,
    viewModel: com.example.halalyticscompose.ui.viewmodel.MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var allergy by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Full Name",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Username") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Username",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible }
                            ) {
                                Icon(
                                    if (isPasswordVisible) Icons.Default.Visibility 
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None 
                        else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (isDarkMode) TextGray.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.3f),
                            focusedTextColor = if (isDarkMode) TextWhite else Color.Black,
                            unfocusedTextColor = if (isDarkMode) TextWhite else Color.Black,
                            focusedLabelColor = if (isDarkMode) HalalGreen else Color(0xFF667EEA),
                            unfocusedLabelColor = if (isDarkMode) TextGray else Color.Gray
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm Password") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Confirm Password",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                            ) {
                                Icon(
                                    if (isConfirmPasswordVisible) Icons.Default.Visibility 
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None 
                        else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (isDarkMode) TextGray.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.3f),
                            focusedTextColor = if (isDarkMode) TextWhite else Color.Black,
                            unfocusedTextColor = if (isDarkMode) TextWhite else Color.Black,
                            focusedLabelColor = if (isDarkMode) HalalGreen else Color(0xFF667EEA),
                            unfocusedLabelColor = if (isDarkMode) TextGray else Color.Gray
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Phone Number
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Phone Number") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = "Phone Number",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Blood Type
                    var bloodTypeExpanded by remember { mutableStateOf(false) }
                    val bloodTypes = listOf("A", "B", "AB", "O")
                    
                    ExposedDropdownMenuBox(
                        expanded = bloodTypeExpanded,
                        onExpandedChange = { bloodTypeExpanded = !bloodTypeExpanded }
                    ) {
                        OutlinedTextField(
                            value = bloodType,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                            label = { Text("Blood Type") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocalHospital,
                                    contentDescription = "Blood Type",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                        
                        DropdownMenu(
                            expanded = bloodTypeExpanded,
                            onDismissRequest = { bloodTypeExpanded = false }
                        ) {
                            bloodTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        bloodType = type
                                        bloodTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Allergies
                    var allergyExpanded by remember { mutableStateOf(false) }
                    val allergies = listOf(
                        "None",
                        "Sakit gula",
                        "Seafood",
                        "Peanuts",
                        "Dairy",
                        "Eggs",
                        "Soy",
                        "Wheat",
                        "Other"
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = allergyExpanded,
                        onExpandedChange = { allergyExpanded = !allergyExpanded }
                    ) {
                        OutlinedTextField(
                            value = allergy,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                            label = { Text("Allergies") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Allergies",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                        
                        DropdownMenu(
                            expanded = allergyExpanded,
                            onDismissRequest = { allergyExpanded = false }
                        ) {
                            allergies.forEach { allergyType ->
                                DropdownMenuItem(
                                    text = { Text(allergyType) },
                                    onClick = {
                                        allergy = allergyType
                                        allergyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Medical History
                    OutlinedTextField(
                        value = medicalHistory,
                        onValueChange = { medicalHistory = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        label = { Text("Medical History") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.MedicalServices,
                                contentDescription = "Medical History",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        placeholder = { Text("Enter any medical conditions or history...") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        maxLines = 4
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Error message
                    if (errorMessage.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFF5252).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                modifier = Modifier.padding(12.dp),
                                color = Color(0xFFFF5252),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Register button
                    Button(
                        onClick = {
                            // Clear previous error
                            errorMessage = ""
                            
                            // Validate all required fields
                            if (fullName.isEmpty()) {
                                errorMessage = "Please enter your full name"
                                return@Button
                            }
                            
                            if (username.isEmpty()) {
                                errorMessage = "Please enter a username"
                                return@Button
                            }
                            
                            if (email.isEmpty()) {
                                errorMessage = "Please enter your email"
                                return@Button
                            }
                            
                            if (!email.contains("@")) {
                                errorMessage = "Please enter a valid email address"
                                return@Button
                            }
                            
                            if (password.isEmpty()) {
                                errorMessage = "Please enter a password"
                                return@Button
                            }
                            
                            if (password.length < 6) {
                                errorMessage = "Password must be at least 6 characters"
                                return@Button
                            }
                            
                            if (confirmPassword.isEmpty()) {
                                errorMessage = "Please confirm your password"
                                return@Button
                            }
                            
                            if (password != confirmPassword) {
                                errorMessage = "Passwords do not match"
                                return@Button
                            }
                            
                            if (phone.isEmpty()) {
                                errorMessage = "Please enter your phone number"
                                return@Button
                            }
                            
                            if (bloodType.isEmpty()) {
                                errorMessage = "Please select your blood type"
                                return@Button
                            }
                            
                            if (allergy.isEmpty()) {
                                errorMessage = "Please select your allergy status"
                                return@Button
                            }
                            
                            if (medicalHistory.isEmpty()) {
                                errorMessage = "Please enter your medical history"
                                return@Button
                            }
                            
                            isLoading = true
                            errorMessage = ""
                            
                            viewModel.register(
                                fullName = fullName,
                                username = username,
                                email = email,
                                password = password,
                                phone = phone,
                                bloodType = bloodType,
                                allergy = allergy,
                                medicalHistory = medicalHistory,
                                onSuccess = {
                                    isLoading = false
                                    navController.navigate("home") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onError = { error ->
                                    isLoading = false
                                    errorMessage = error
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667EEA)
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = "Creating Account...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                text = "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Back to login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        
                        Text(
                            text = "Sign In",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("login")
                                },
                            color = Color(0xFF667EEA),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
