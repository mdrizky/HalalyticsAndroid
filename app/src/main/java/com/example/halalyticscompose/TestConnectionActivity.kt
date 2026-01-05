package com.example.halalyticscompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.halalyticscompose.Data.Network.ApiConfig
import kotlinx.coroutines.launch

class TestConnectionActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                TestConnectionScreen()
            }
        }
    }
    
    @Composable
    fun TestConnectionScreen() {
        var testResult by remember { mutableStateOf("Press button to test connection") }
        var isLoading by remember { mutableStateOf(false) }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "API Connection Test",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    testConnection { result ->
                        testResult = result
                    }
                },
                enabled = !isLoading
            ) {
                Text("Test Connection")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                CircularProgressIndicator()
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = testResult,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    
    private fun testConnection(onResult: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                Log.d("TEST", "🔍 Starting API test...")
                
                val response = ApiConfig.getExternalApiService()
                    .searchProducts(query = "coca cola", pageSize = 5, page = 1)
                
                Log.d("TEST", "📡 Response code: ${response.code()}")
                Log.d("TEST", "📡 Response success: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("TEST", "📦 Response body: $body")
                    
                    val result = buildString {
                        appendLine("✅ CONNECTION SUCCESS!")
                        appendLine("Response Code: ${body?.responseCode}")
                        appendLine("Message: ${body?.message}")
                        appendLine("Products Found: ${body?.content?.products?.size ?: 0}")
                        
                        body?.content?.products?.take(3)?.forEach { product ->
                            appendLine("\n• ${product.getDisplayName()}")
                            appendLine("  Brand: ${product.brands ?: "Unknown"}")
                        }
                    }
                    
                    onResult(result)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("TEST", "❌ Error body: $errorBody")
                    
                    onResult("❌ ERROR: ${response.code()}\n$errorBody")
                }
            } catch (e: Exception) {
                Log.e("TEST", "❌ Exception: ${e.message}", e)
                onResult("❌ EXCEPTION: ${e.localizedMessage}\n\n${e.stackTraceToString()}")
            }
        }
    }
}
