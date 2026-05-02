package com.example.halalyticscompose.ui.screens.donor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun DonorEventsScreen(navController: NavController, viewModel: DonorViewModel) {
    val events by viewModel.bloodEvents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donor Events", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(events) { event ->
                ListItem(
                    headlineContent = { Text(event.title) },
                    supportingContent = { Text(event.location) },
                    trailingContent = { Text(event.eventDate) },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
