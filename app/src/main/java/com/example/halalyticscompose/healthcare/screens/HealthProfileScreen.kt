package com.example.halalyticscompose.healthcare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.halalyticscompose.healthcare.model.HealthProfile
import com.example.halalyticscompose.healthcare.viewmodel.HealthScannerViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthProfileScreen(
    navController: NavController,
    viewModel: HealthScannerViewModel
) {
    var conditions by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var dietaryGoals by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0EA5E9), Color(0xFF2563EB))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Health Profile",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Help us personalize your food analysis",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileField(
                label = "Health Conditions",
                placeholder = "e.g. Diabetes, Hypertension",
                value = conditions,
                onValueChange = { conditions = it },
                icon = Icons.Default.MedicalServices
            )

            ProfileField(
                label = "Allergies",
                placeholder = "e.g. Peanuts, Gluten, Dairy",
                value = allergies,
                onValueChange = { allergies = it },
                icon = Icons.Default.Warning
            )

            ProfileField(
                label = "Dietary Goals",
                placeholder = "e.g. Low Sodium, Weight Loss",
                value = dietaryGoals,
                onValueChange = { dietaryGoals = it },
                icon = Icons.AutoMirrored.Filled.TrendingDown
            )

            ProfileField(
                label = "Additional Notes",
                placeholder = "Any other information...",
                value = additionalNotes,
                onValueChange = { additionalNotes = it },
                icon = Icons.AutoMirrored.Filled.Notes,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    val profile = HealthProfile(
                        id = UUID.randomUUID().toString(),
                        userId = "user123", // Get from actual session
                        conditions = conditions.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        allergies = allergies.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        dietaryGoals = dietaryGoals.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        additionalNotes = additionalNotes
                    )
                    viewModel.updateHealthProfile(profile)
                    navController.navigate("health_scanner")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Save and Continue", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    singleLine: Boolean = true
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.White) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 3
        )
    }
}
