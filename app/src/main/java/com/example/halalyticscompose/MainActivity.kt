package com.example.halalyticscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.halalyticscompose.ui.components.BottomNavigationBar
import com.example.halalyticscompose.ui.screens.*
import com.example.halalyticscompose.ui.theme.HalalyticsComposeTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            HalalyticsComposeTheme {
                val navController = rememberNavController()
                var currentRoute by remember { mutableStateOf("home") }
                
                // List of routes that should show bottom navigation
                val bottomNavRoutes = listOf("home", "history", "scan", "result", "profile")
                
                Scaffold(
                    bottomBar = {
                        if (currentRoute in bottomNavRoutes) {
                            BottomNavigationBar(
                                selectedRoute = currentRoute,
                                onItemSelected = { route ->
                                    if (route != currentRoute) {
                                        navController.navigate(route) {
                                            popUpTo("home") { 
                                                saveState = true 
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        // Home Screen
                        composable("home") {
                            currentRoute = "home"
                            HomeScreen(
                                user = null,
                                onScanClick = { 
                                    navController.navigate("scan")
                                },
                                onHistoryClick = { 
                                    navController.navigate("history")
                                },
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                onSearchProductsClick = {
                                    navController.navigate("search")
                                }
                            )
                        }
                        
                        // Scan Screen
                        composable("scan") {
                            currentRoute = "scan"
                            ScanScreen(
                                navController = navController,
                                onBack = { navController.popBackStack() },
                                onResult = { barcode ->
                                    // Handle barcode result - navigate to result or show product
                                    navController.navigate("result")
                                },
                                onManualInputClick = {
                                    navController.navigate("manual_input")
                                }
                            )
                        }
                        
                        // History Screen
                        composable("history") {
                            currentRoute = "history"
                            HistoryScreen(
                                onBackClick = { navController.popBackStack() },
                                onItemClick = { itemId ->
                                    // Navigate to item detail
                                }
                            )
                        }
                        
                        // Result Screen (placeholder for now)
                        composable("result") {
                            currentRoute = "result"
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Result Screen - Coming Soon")
                            }
                        }
                        
                        // Profile Screen
                        composable("profile") {
                            currentRoute = "profile"
                            ProfileScreen(
                                user = null,
                                onHistoryClick = { 
                                    navController.navigate("history")
                                },
                                onSettingsClick = { 
                                    // Navigate to settings
                                },
                                onHelpClick = { 
                                    // Navigate to help
                                },
                                onLogoutClick = {
                                    // Handle logout
                                }
                            )
                        }
                        
                        // Manual Input Screen
                        composable("manual_input") {
                            currentRoute = "manual_input"
                            ManualInputScreen(
                                onBackClick = { navController.popBackStack() },
                                onSubmit = { barcode ->
                                    // Handle manual barcode input
                                    navController.navigate("result")
                                }
                            )
                        }
                        
                        // Search Screen
                        composable("search") {
                            currentRoute = "search"
                            ProductExternalSearchScreen(
                                onBackClick = { navController.popBackStack() },
                                onProductClick = { productCode ->
                                    navController.navigate("product_detail/$productCode")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
