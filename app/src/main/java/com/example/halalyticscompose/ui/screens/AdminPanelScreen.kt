package com.example.halalyticscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.AdminViewModel
import com.example.halalyticscompose.Data.Model.AdminProduct
import com.example.halalyticscompose.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    navController: androidx.navigation.NavController,
    viewModel: AdminViewModel = hiltViewModel(),
    mainViewModel: MainViewModel
) {
    val dashboardStats by viewModel.dashboardStats.collectAsState()
    val pendingProducts by viewModel.pendingProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val actionResult by viewModel.actionResult.collectAsState()
    
    // Check Role Access
    val currentUserRole = mainViewModel.currentUser.collectAsState().value // We might need to expose Role specifically
    // Ideally MainViewModel or SessionManager exposes role directly. 
    // For now assuming we are here means we are authorized or we check it.
    
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
        viewModel.loadPendingProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = { 
                        viewModel.loadDashboardData()
                        viewModel.loadPendingProducts() 
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats Section
                item {
                    Text("Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Pending",
                            value = dashboardStats?.pendingApproval?.toString() ?: "0",
                            color = Color(0xFFE57373),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Users",
                            value = dashboardStats?.totalUsers?.toString() ?: "0",
                            color = Color(0xFF64B5F6),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Approved",
                            value = dashboardStats?.totalProducts?.toString() ?: "0",
                            color = Color(0xFF81C784),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Scans Today",
                            value = dashboardStats?.totalScansToday?.toString() ?: "0",
                            color = Color(0xFFFFD54F),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Divider
                item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

                // Pending List Section
                item {
                    Text("Pending Approvals", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                if (pendingProducts.isEmpty()) {
                    item {
                        Text("No pending products.", color = Color.Gray, modifier = Modifier.padding(8.dp))
                    }
                } else {
                    items(pendingProducts) { product ->
                        ProductApprovalCard(
                            product = product,
                            onApprove = { viewModel.approveProduct(product.idProduct) },
                            onReject = { viewModel.rejectProduct(product.idProduct, "Rejected by Admin") }
                        )
                    }
                }
            }
            
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            
            actionResult?.let { message ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearMessage() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(message)
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            Text(title, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProductApprovalCard(
    product: AdminProduct,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Barcode: ${product.barcode}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Health Stats Preview
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Sugar", fontSize = 12.sp, color = Color.Gray)
                    Text("${product.sugarG}g", fontWeight = FontWeight.Medium)
                }
                Column {
                    Text("Caffeine", fontSize = 12.sp, color = Color.Gray)
                    Text("${product.caffeineMg}mg", fontWeight = FontWeight.Medium)
                }
                 Column {
                    Text("Certificate", fontSize = 12.sp, color = Color.Gray)
                    Text(product.halalCertificate ?: "-", fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve")
                }
            }
        }
    }
}
