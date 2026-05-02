package com.example.halalyticscompose.ui.screens.donor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.halalyticscompose.ui.viewmodel.DonorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfScreeningScreen(navController: NavController, viewModel: DonorViewModel, token: String, eventId: Int) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Self Screening", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Please complete the questionnaire below.", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { 
                viewModel.registerForEvent(token, eventId) {
                    navController.navigate("donor_history") {
                        popUpTo("donor_home")
                    }
                }
            }) {
                Text("Submit Screening")
            }
        }
    }
}
