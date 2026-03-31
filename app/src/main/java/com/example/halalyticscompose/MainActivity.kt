package com.example.halalyticscompose

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.halalyticscompose.ui.screens.*
import com.example.halalyticscompose.ui.components.MainLayout
import com.example.halalyticscompose.ui.theme.*
import com.example.halalyticscompose.healthcare.screens.*
import com.example.halalyticscompose.healthcare.viewmodel.HealthScannerViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.halalyticscompose.ui.viewmodel.MainViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.halalyticscompose.ui.viewmodel.NotificationViewModel

import com.example.halalyticscompose.utils.LanguageManager
import com.example.halalyticscompose.utils.BiometricAuthHelper
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun HalalyticsComposeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun MedicalRouteGuard(
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    if (!enabled) {
        content()
        return
    }

    val context = LocalContext.current
    val activity = context as? FragmentActivity
    var isUnlocked by rememberSaveable { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    if (isUnlocked) {
        content()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Akses Medis Terkunci",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Gunakan biometrik untuk membuka halaman medis sensitif.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                authError?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val act = activity
                        if (act == null) {
                            authError = "Biometrik tidak tersedia di perangkat ini."
                            return@Button
                        }
                        if (!BiometricAuthHelper.canAuthenticate(act)) {
                            authError = "Biometrik belum aktif. Buka pengaturan perangkat untuk mengaktifkan."
                            return@Button
                        }
                        BiometricAuthHelper.authenticate(
                            activity = act,
                            executor = ContextCompat.getMainExecutor(act),
                            onSuccess = {
                                authError = null
                                isUnlocked = true
                            },
                            onError = { message ->
                                authError = message
                            }
                        )
                    }
                ) {
                    Text("Buka dengan Biometrik")
                }
            }
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            // Database and DAO are now injected via Hilt
            val isDarkMode by mainViewModel.isDarkMode.collectAsState()
            val appLanguage by mainViewModel.appLanguage.collectAsState()
            val privacyModeEnabled by mainViewModel.privacyModeEnabled.collectAsState()
            val biometricLockEnabled by mainViewModel.biometricLockEnabled.collectAsState()
            val autoLogoutEnabled by mainViewModel.autoLogoutEnabled.collectAsState()
            val autoLogoutMinutes by mainViewModel.autoLogoutMinutes.collectAsState()
            val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
            val isAdmin by mainViewModel.isAdmin.collectAsState()

            HalalyticsComposeTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

                LaunchedEffect(appLanguage) {
                    LanguageManager.applyLanguageIfNeeded(this@MainActivity, appLanguage)
                }

                LaunchedEffect(privacyModeEnabled) {
                    val shouldUseSecureWindow = privacyModeEnabled && !BuildConfig.DEBUG
                    if (shouldUseSecureWindow) {
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_SECURE,
                            WindowManager.LayoutParams.FLAG_SECURE
                        )
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
                
                // Initialize SessionManager
                val sessionManager = remember {
                    com.example.halalyticscompose.utils.SessionManager.getInstance(context)
                }

                DisposableEffect(lifecycleOwner, autoLogoutEnabled, autoLogoutMinutes, isLoggedIn) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (!autoLogoutEnabled) return@LifecycleEventObserver
                        when (event) {
                            Lifecycle.Event.ON_STOP -> {
                                if (isLoggedIn) {
                                    sessionManager.setLastBackgroundTimestamp(System.currentTimeMillis())
                                }
                            }
                            Lifecycle.Event.ON_START -> {
                                if (isLoggedIn) {
                                    val lastTs = sessionManager.getLastBackgroundTimestamp()
                                    if (lastTs > 0L) {
                                        val timeoutMs = autoLogoutMinutes * 60_000L
                                        val idleMs = System.currentTimeMillis() - lastTs
                                        if (idleMs >= timeoutMs) {
                                            mainViewModel.logout(navController)
                                        }
                                    }
                                }
                            }
                            else -> Unit
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                
                // Health Scanner Feature ViewModel
                val healthViewModel: HealthScannerViewModel = hiltViewModel()
                
                // Notifications
                val notificationViewModel: NotificationViewModel = hiltViewModel()



                // Comparison Feature
                val compareViewModel: com.example.halalyticscompose.ui.viewmodel.CompareViewModel = hiltViewModel()

                // Initialize PreferenceManager
                val preferenceManager = remember {
                    com.example.halalyticscompose.utils.PreferenceManager(context)
                }
                
                // Initialize ViewModel with SessionManager
                LaunchedEffect(Unit) {
                    // MainViewModel dependencies are now injected by Hilt
                    

                    
                     val token = sessionManager.getAuthToken() ?: ""
                     val userId = sessionManager.getUserId()
                     // Start notification listener on app start if logged in
                     if (token.isNotEmpty()) {
                         notificationViewModel.loadNotifications(token, userId)
                         // Sync with MySQL if Firebase user is present
                         com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.let { firebaseUser ->
                             mainViewModel.syncWithMySQL(firebaseUser, token)
                         }
                     }
                }
                
                // ⚠️ DYNAMIC START DESTINATION - Optimized for quick redirect
                val startDestination = remember {
                    when {
                        !sessionManager.isLoggedIn() -> "splash"
                        sessionManager.getRole()?.equals("admin", ignoreCase = true) == true -> "home"
                        else -> "home" // Authenticated users go to home
                    }
                }
                
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    // Splash Screen
                    composable("splash") {
                        SplashScreen(
                            navController = navController,
                            isLoggedIn = sessionManager.isLoggedIn(),
                            onSplashComplete = {
                                // Additional logic if needed after splash
                            }
                        )
                    }

                    // Login Screen
                    composable("login") {
                        MainLayout(navController = navController) { paddingValues ->
                            LoginScreen(
                                navController = navController
                            )
                        }
                    }
                    
                    // Register Screen
                    composable("register") {
                        MainLayout(navController = navController) { paddingValues ->
                            SimpleRegisterScreen(
                                navController = navController,
                                viewModel = mainViewModel
                            )
                        }
                    }

                    composable("basic_profile") {
                        MainLayout(navController = navController) { paddingValues ->
                            BasicProfileScreen(
                                navController = navController,
                                viewModel = mainViewModel
                            )
                        }
                    }
                    
                    // Home Screen
                    composable("home") {
                        MainLayout(navController = navController, showBottomNav = true) { paddingValues ->
                            HomeScreen(
                                navController = navController,
                                viewModel = mainViewModel
                            )
                        }
                    }

                    // Search Hub Screen
                    composable("search_hub") {
                        MainLayout(navController = navController, showBottomNav = true) { paddingValues ->
                            SearchHubScreen(
                                navController = navController
                            )
                        }
                    }

                    composable("cosmetic_detail") {
                        MainLayout(navController = navController) {
                            CosmeticDetailScreen(navController = navController)
                        }
                    }

                    composable("cosmetic_detail/{productId}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId")
                        MainLayout(navController = navController) {
                            CosmeticDetailScreen(
                                navController = navController,
                                productId = productId
                            )
                        }
                    }

                    composable("health_articles") {
                        MainLayout(navController = navController) {
                            HealthArticleListScreen(navController = navController)
                        }
                    }

                    composable("health_article_detail/{articleId}") { backStackEntry ->
                        val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
                        MainLayout(navController = navController) {
                            HealthArticleDetailScreen(
                                navController = navController,
                                articleId = articleId
                            )
                        }
                    }

                    // Scan Hub Screen
                    composable("scan_hub") {
                        MainLayout(navController = navController, showBottomNav = true) { paddingValues ->
                            ScanHubScreen(
                                navController = navController
                            )
                        }
                    }
                    
                    // Scan Screen
                    composable("scan") {
                        MainLayout(navController = navController) { paddingValues ->
                            ScanScreen(
                                navController = navController,
                                viewModel = mainViewModel,
                                paddingValues = paddingValues
                            )
                        }
                    }
                    
                    // Settings Screen
                    composable("settings") {
                        MainLayout(navController = navController) { paddingValues ->
                            SettingsScreen(navController = navController, viewModel = mainViewModel)
                        }
                    }

                    // Profile Screen (BottomNav route)
                    composable("profile") {
                        MainLayout(navController = navController, showBottomNav = true) { paddingValues ->
                            ProfileScreen(navController = navController, viewModel = mainViewModel)
                        }
                    }

                    composable("profile_status") {
                        MainLayout(navController = navController) { paddingValues ->
                            ProfileStatusScreen(
                                navController = navController,
                                viewModel = mainViewModel
                            )
                        }
                    }
                    
                    // Account Management Screen
                    composable("account_management") {
                        MainLayout(navController = navController) { paddingValues ->
                            AccountManagementScreen(navController = navController, viewModel = mainViewModel)
                        }
                    }
                    
                    // Privacy Policy Screen
                    composable("privacy_policy") {
                        MainLayout(navController = navController) { paddingValues ->
                            PrivacyPolicyScreen(navController = navController)
                        }
                    }

                    // Enhanced OCR Screen
                    composable("enhanced_ocr") {
                        MainLayout(navController = navController) { paddingValues ->
                            EnhancedOCRScreen(
                                navController = navController
                            )
                        }
                    }
                    
                    // OCR Analysis Screen
                    composable("ocr_analysis") {
                        MainLayout(navController = navController) { paddingValues ->
                            EnhancedOCRScreen(
                                navController = navController
                            )
                        }
                    }
                    
                    // AI Analysis Screen
                    composable("ai_analysis") { backStackEntry ->
                        MainLayout(navController = navController) { paddingValues ->
                            AiAnalysisScreen(
                                navController = navController,
                                mainViewModel = mainViewModel
                            )
                        }
                    }

                    composable("ai_report") {
                        MainLayout(navController = navController) { paddingValues ->
                            AiReportScreen(navController = navController)
                        }
                    }
                    
                    // Manual Input Screen
                    composable("manual_input") {
                        MainLayout(navController = navController) { paddingValues ->
                            ManualInputScreen(
                                navController = navController
                            )
                        }
                    }
                    
                    // History Screen (New Realtime)
                    composable("history") {
                        MainLayout(navController = navController, showBottomNav = true) { paddingValues ->
                            ScanHistoryScreen(
                                navController = navController
                            )
                        }
                    }

                    // Backward-compatible alias for old route usage
                    composable("scan_history") {
                        MainLayout(navController = navController, showBottomNav = true) { paddingValues ->
                            ScanHistoryScreen(
                                navController = navController
                            )
                        }
                    }

                    // Scan history detail
                    composable("scan_history_detail/{id}") { backStackEntry ->
                        MainLayout(navController = navController) { paddingValues ->
                            val historyId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                            ScanHistoryDetailScreen(
                                navController = navController,
                                historyId = historyId
                            )
                        }
                    }

                    // Notification Screen (New)
                    composable("notifications") {
                        MainLayout(navController = navController) { paddingValues ->
                            NotificationScreen(
                                navController = navController,
                                viewModel = notificationViewModel
                            )
                        }
                    }

                    composable("admin_notifications_app") {
                        if (isAdmin) {
                            MainLayout(navController = navController) { paddingValues ->
                                AdminNotificationScreen(
                                    navController = navController
                                )
                            }
                        } else {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }

                    // Favorites Screen (New)
                    composable("favorites") {
                        MainLayout(navController = navController) { paddingValues ->
                            FavoritesScreen(
                                navController = navController
                            )
                        }
                    }
                    
                    // Edit Profile Screen
                    composable("edit_profile") {
                        MainLayout(navController = navController) { paddingValues ->
                            EditProfileScreen(
                                navController = navController
                            )
                        }
                    }

                    // Family Box Screen
                    composable("family_box") {
                        MainLayout(navController = navController) { paddingValues ->
                            FamilyBoxScreen(
                                navController = navController,
                                viewModel = mainViewModel
                            )
                        }
                    }
                    
                                        
                    // Product Detail Screen
                    composable("product_detail/{barcode}") { backStackEntry ->
                        MainLayout(navController = navController) { paddingValues ->
                            val barcode = backStackEntry.arguments?.getString("barcode")
                            ProductDetailScreen(
                                navController = navController,
                                barcode = barcode ?: "",
                                mainViewModel = mainViewModel
                            )
                        }
                    }
                    
                    // Search External Screen
                    composable("search_external") {
                        MainLayout(navController = navController) { paddingValues ->
                            SearchExternalScreen(
                                navController = navController
                            )
                        }
                    }
                    
                    // Product External Detail Screen
                    composable("product_external_detail/{barcode}") { backStackEntry ->
                        MainLayout(navController = navController) { paddingValues ->
                            val barcode = backStackEntry.arguments?.getString("barcode")
                            ProductExternalDetailScreen(
                                navController = navController,
                                barcode = barcode ?: ""
                            )
                        }
                    }
                    
                    // Ingredient Detail Screen
                    composable("ingredient_detail/{ingredientId}") { backStackEntry ->
                        MainLayout(navController = navController) { paddingValues ->
                            val ingredientId = backStackEntry.arguments?.getString("ingredientId")?.toIntOrNull() ?: 0
                            IngredientDetailScreen(
                                navController = navController,
                                ingredientId = ingredientId
                            )
                        }
                    }
                    
                    // Forgot Password Screen
                    composable("forgot_password") {
                        MainLayout(navController = navController) { paddingValues ->
                            ForgotPasswordScreen(
                                navController = navController
                            )
                        }
                    }

                    // Health Scanner Feature Routes
                    composable(
                        "health_assistant?symptom={symptom}",
                        arguments = listOf(
                            navArgument("symptom") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val symptom = backStackEntry.arguments?.getString("symptom")
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                HealthAssistantScreen(
                                    navController = navController,
                                    initialSymptom = symptom
                                )
                            }
                        }
                    }
                    composable("medicine_reminders") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicineRemindersScreen(
                                navController = navController
                            )
                        }
                    }
                    
                    composable("health_scanner") {
                        MainLayout(navController = navController) { paddingValues ->
                            HealthScannerScreen(
                                navController = navController,
                                viewModel = healthViewModel
                            )
                        }
                    }
                    
                    composable("health_analysis") {
                        MainLayout(navController = navController) { paddingValues ->
                            AnalysisResultScreen(
                                navController = navController,
                                viewModel = healthViewModel
                            )
                        }
                    }
                    
                    // Food Scan & Recognition Screen
                    // Food Scan & Recognition Screen (AI Meal Scanner) - PHASE 6 UPGRADE
                    composable("food_scan") {
                        MainLayout(navController = navController) { paddingValues ->
                            FoodCameraScreen(
                                navController = navController
                            )
                        }
                    }
                    
                    // Food Analysis Result Screen - PHASE 6 UPGRADE
                    composable(
                        route = "food_result/{imagePath}",
                        arguments = listOf(androidx.navigation.navArgument("imagePath") { type = androidx.navigation.NavType.StringType })
                    ) { backStackEntry ->
                        val imagePath = backStackEntry.arguments?.getString("imagePath") ?: ""
                        MainLayout(navController = navController) { paddingValues ->
                            FoodAnalysisResultScreen(
                                navController = navController,
                                imagePath = imagePath,
                                viewModel = mainViewModel
                            )
                        }
                    }



                    // ⚠️ ADDED: AI Weekly Report Screen
                    composable("weekly_report") {
                        MainLayout(navController = navController) { paddingValues ->
                            AiReportScreen(
                                navController = navController
                            )
                        }
                    }

                    // ⚠️ ADDED: Encyclopedia Screen
                    composable("encyclopedia") {
                        MainLayout(navController = navController) { paddingValues ->
                            EncyclopediaScreen(
                                navController = navController,
                                paddingValues = paddingValues
                            )
                        }
                    }

                    // ⚠️ ADDED: Encyclopedia Detail Screen
                    composable("encyclopedia_detail/{id}/{title}") { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                        val viewModel: com.example.halalyticscompose.ui.viewmodel.HealthEncyclopediaViewModel = hiltViewModel()
                        val selectedItem by viewModel.selectedItem.collectAsState()
                        val isLoading by viewModel.isLoading.collectAsState()

                        LaunchedEffect(itemId) {
                            viewModel.fetchById(itemId)
                        }

                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        } else if (selectedItem != null) {
                            EncyclopediaDetailScreen(
                                navController = navController,
                                item = selectedItem!!
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Item tidak ditemukan", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    // Certificate Verification Route
                    composable("certificate_result/{qrData}") { backStackEntry ->
                        val qrData = backStackEntry.arguments?.getString("qrData") ?: ""
                        CertificateVerificationWrapper(
                            navController = navController,
                            viewModel = mainViewModel,
                            qrData = qrData
                        )
                    }

                    // Contribution Screen
                    composable(
                        route = "contribution?barcode={barcode}&name={name}",
                        arguments = listOf(
                            androidx.navigation.navArgument("barcode") {
                                type = androidx.navigation.NavType.StringType
                                nullable = true
                                defaultValue = ""
                            },
                            androidx.navigation.navArgument("name") {
                                type = androidx.navigation.NavType.StringType
                                nullable = true
                                defaultValue = ""
                            }
                        )
                    ) { backStackEntry ->
                        val initialBarcode = backStackEntry.arguments?.getString("barcode")
                        val initialName = backStackEntry.arguments?.getString("name")
                        MainLayout(navController = navController) { paddingValues ->
                            ContributionScreen(
                                navController = navController,
                                initialBarcode = initialBarcode,
                                initialProductName = initialName
                            )
                        }
                    }

                    // Emergency QR Screen
                    composable("emergency_qr") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                EmergencyQRScreen(
                                    navController = navController
                                )
                            }
                        }
                    }

                    // Health Profile Screen
                    composable("health_profile") {
                        MainLayout(navController = navController) { paddingValues ->
                            HealthProfileScreen(
                                navController = navController,
                                viewModel = mainViewModel
                            )
                        }
                    }

                    // Body Monitor Screen
                    composable("health_monitor") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                HealthMonitorScreen(
                                    navController = navController,
                                    viewModel = mainViewModel
                                )
                            }
                        }
                    }

                    composable("health_diary") {
                        MainLayout(navController = navController) { paddingValues ->
                            HealthDiaryScreen(navController = navController)
                        }
                    }

                    composable("medical_resume") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                MedicalResumeScreen(navController = navController)
                            }
                        }
                    }

                    composable("health_pass") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                HealthPassScreen(navController = navController)
                            }
                        }
                    }

                    // International Medicine Search Screen
                    composable("international_medicine") {
                        MainLayout(navController = navController) { paddingValues ->
                            InternationalMedicineScreen(
                                navController = navController
                            )
                        }
                    }

                    // Medicine Detail Screen
                    composable("medicine_detail/{medicineId}") { backStackEntry ->
                        val medicineId = backStackEntry.arguments?.getString("medicineId")?.toIntOrNull() ?: 0
                        MainLayout(navController = navController) { paddingValues ->
                            MedicineDetailScreen(
                                navController = navController,
                                medicineId = medicineId
                            )
                        }
                    }

                    // ==================== ADVANCED AI HEALTH SUITE ====================
                    composable("health_suite_hub") {
                        MainLayout(navController = navController) { paddingValues ->
                            HealthSuiteHubScreen(navController = navController)
                        }
                    }

                    composable("drug_interaction") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                DrugInteractionScreen(navController = navController)
                            }
                        }
                    }

                    composable("pill_scanner") {
                        MainLayout(navController = navController) { paddingValues ->
                            PillScannerScreen(navController = navController)
                        }
                    }

                    composable("medication_reminder_advanced") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicationReminderScreen(navController = navController)
                        }
                    }

                    composable("lab_analysis") {
                        MainLayout(navController = navController) { paddingValues ->
                            LabAnalysisScreen(navController = navController)
                        }
                    }

                    composable("health_journey") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                HealthJourneyScreen(navController = navController)
                            }
                        }
                    }

                    composable("nutrition_scanner") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                NutritionScannerScreen(navController = navController)
                            }
                        }
                    }

                    composable("meal_scan") {
                        MainLayout(navController = navController) { paddingValues ->
                            MealScanScreen(
                                navController = navController,
                                mainViewModel = mainViewModel
                            )
                        }
                    }

                    composable(
                        route = "food_analysis/{foodId}",
                        arguments = listOf(
                            androidx.navigation.navArgument("foodId") {
                                type = androidx.navigation.NavType.IntType
                            }
                        )
                    ) { backStackEntry ->
                        val foodId = backStackEntry.arguments?.getInt("foodId") ?: 0
                        MainLayout(navController = navController) { paddingValues ->
                            FoodAnalysisScreen(
                                navController = navController,
                                foodId = foodId
                            )
                        }
                    }

                    composable("medical_records") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                MedicalRecordsScreen(navController = navController)
                            }
                        }
                    }

                    composable("watchlist_editor") {
                        MainLayout(navController = navController) { paddingValues ->
                            WatchlistEditorScreen(navController = navController)
                        }
                    }

                    composable("emergency_p3k") {
                        MainLayout(navController = navController) { paddingValues ->
                            EmergencyP3KScreen(navController = navController)
                        }
                    }

                    composable("halal_specialist") {
                        MainLayout(navController = navController) { paddingValues ->
                            MedicalRouteGuard(enabled = biometricLockEnabled) {
                                HalalSpecialistScreen(navController = navController)
                            }
                        }
                    }

                    composable("bpom_scanner") {
                        MainLayout(navController = navController) { paddingValues ->
                            BpomScannerScreen(
                                navController = navController
                            )
                        }
                    }

                    composable("skincare_scanner") {
                        MainLayout(navController = navController) { paddingValues ->
                            SkincareScannerScreen(
                                navController = navController,
                                mainViewModel = mainViewModel
                            )
                        }
                    }

                    // Product Request Screen (Crowdsourcing)
                    composable("product_request/{barcode}") { backStackEntry ->
                        val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
                        MainLayout(navController = navController) { paddingValues ->
                            ProductRequestScreen(
                                navController = navController,
                                barcode = barcode
                            )
                        }
                    }

                    composable("compare_products") {
                        MainLayout(navController = navController) { paddingValues ->
                            CompareScreen(
                                navController = navController,
                                mainViewModel = mainViewModel,
                                viewModel = compareViewModel
                            )
                        }
                    }

                    composable("admin_panel_app") {
                        if (isAdmin) {
                            MainLayout(navController = navController) { paddingValues ->
                                AdminPanelScreen(
                                    navController = navController,
                                    mainViewModel = mainViewModel
                                )
                            }
                        } else {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }

                    composable("comparison_result") {
                        MainLayout(navController = navController) { paddingValues ->
                            ComparisonResultScreen(
                                navController = navController,
                                viewModel = compareViewModel
                            )
                        }
                    }

                    composable("report_issue/{productId}/{productName}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull() ?: 0
                        val productName = backStackEntry.arguments?.getString("productName") ?: ""
                        MainLayout(navController = navController) { paddingValues ->
                            ReportIssueScreen(
                                navController = navController,
                                productId = productId,
                                productName = productName
                            )
                        }
                        }

                    // ═══════════════════════════════════════
                    // 🆕 EXPANSION SCREENS
                    // ═══════════════════════════════════════

                    composable("onboarding") {
                        OnboardingScreen(
                            navController = navController,
                            onFinish = {
                                navController.navigate("login") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("points_rewards") {
                        MainLayout(navController = navController) { paddingValues ->
                            PointsRewardsScreen(navController = navController)
                        }
                    }

                    composable("leaderboard") {
                        MainLayout(navController = navController) { paddingValues ->
                            LeaderboardScreen(navController = navController)
                        }
                    }

                    composable("notification_settings") {
                        MainLayout(navController = navController) { paddingValues ->
                            NotificationSettingsScreen(navController = navController)
                        }
                    }

                    composable("halal_cert_verify") {
                        MainLayout(navController = navController) { paddingValues ->
                            HalalCertVerifyScreen(navController = navController)
                        }
                    }

                    composable("barcode_gallery") {
                        MainLayout(navController = navController) { paddingValues ->
                            BarcodeGalleryScreen(navController = navController)
                        }
                    }

                    // ═══ HEALTH EXPANSION ROUTES ═══

                    composable("medical_info") {
                        MedicalInfoScreen(navController = navController)
                    }

                    composable("bmi_calculator") {
                        BMICalculatorScreen(navController = navController)
                    }

                    composable("medicine_search") {
                        MedicineSearchScreen(navController = navController)
                    }

                    composable("add_medicine_reminder") {
                        AddMedicineReminderScreen(navController = navController)
                    }

                    composable("mental_health_hub") {
                        MentalHealthHubScreen(navController = navController)
                    }

                    composable(
                        "mental_health_quiz/{quizType}",
                        arguments = listOf(navArgument("quizType") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val quizType = backStackEntry.arguments?.getString("quizType") ?: "gad7"
                        MentalHealthQuizScreen(navController = navController, quizType = quizType)
                    }

                    composable("help_center") {
                        HelpCenterScreen(navController = navController)
                    }
                    }
                }
                } // End of Surface
            }
        }
    }


@Composable
fun CertificateVerificationWrapper(
    navController: androidx.navigation.NavController,
    viewModel: com.example.halalyticscompose.ui.viewmodel.MainViewModel,
    qrData: String
) {
    var info by remember {
        mutableStateOf<com.example.halalyticscompose.Data.Model.CertificateInfo?>(
            null
        )
    }
    var error by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(qrData) {
        viewModel.verifyCertificate(
            qrData = qrData,
            onSuccess = { info = it },
            onError = { error = it }
        )
    }

    if (info != null) {
        CertificateResultScreen(navController = navController, info = info!!)
    } else if (error != null) {
        // Show error and pop back
        LaunchedEffect(error) {
            android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    } else {
        // Loading state
        Box(
            modifier = Modifier.fillMaxSize().background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = HalalGreen)
        }
    }
}
