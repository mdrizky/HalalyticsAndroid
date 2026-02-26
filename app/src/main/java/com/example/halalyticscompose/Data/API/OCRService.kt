package com.example.halalyticscompose.data.api

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class OCRService(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val BASE_URL = "http://your-laravel-domain.com/api/ocr"
    
    /**
     * Upload OCR image to backend
     */
    suspend fun uploadOCRImage(
        imageUri: Uri,
        step: String,
        userId: Int,
        barcode: String? = null
    ): Result<OCRResponse> = withContext(Dispatchers.IO) {
        try {
            // Convert URI to file
            val file = uriToFile(imageUri)
            val requestPart = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("step", step)
                .addFormDataPart("user_id", userId.toString())
                
            barcode?.let { 
                requestPart.addFormDataPart("barcode", it) 
            }
            
            val requestBody = requestPart
                .addFormDataPart(
                    "image",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .build()
            
            val request = Request.Builder()
                .url("$BASE_URL/upload")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                if (jsonResponse.getBoolean("success")) {
                    val data = jsonResponse.getJSONObject("data")
                    Result.success(
                        OCRResponse(
                            success = true,
                            message = jsonResponse.getString("message"),
                            ocrProductId = data.getInt("ocr_product_id"),
                            extractedText = data.getString("extracted_text"),
                            ingredients = parseIngredients(data.getJSONArray("ingredients")),
                            confidenceScore = data.getDouble("confidence_score").toFloat(),
                            processingStep = data.getString("processing_step"),
                            nextStep = data.getString("next_step")
                        )
                    )
                } else {
                    Result.failure(Exception(jsonResponse.getString("message")))
                }
            } else {
                Result.failure(Exception("Upload failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get OCR statistics
     */
    suspend fun getOCRStatistics(): Result<OCRStatistics> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$BASE_URL/statistics")
                .get()
                .addHeader("Authorization", "Bearer ${getAuthToken()}")
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                if (jsonResponse.getBoolean("success")) {
                    val data = jsonResponse.getJSONObject("data")
                    Result.success(
                        OCRStatistics(
                            totalScans = data.getInt("total_scans"),
                            pendingReview = data.getInt("pending_review"),
                            approvedToday = data.getInt("approved_today"),
                            rejectedToday = data.getInt("rejected_today"),
                            processingAccuracy = data.getDouble("processing_accuracy").toFloat()
                        )
                    )
                } else {
                    Result.failure(Exception(jsonResponse.getString("message")))
                }
            } else {
                Result.failure(Exception("Failed to get statistics: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get user's OCR history
     */
    suspend fun getUserOCRHistory(userId: Int): Result<List<OCRHistoryItem>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$BASE_URL/history/$userId")
                .get()
                .addHeader("Authorization", "Bearer ${getAuthToken()}")
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                if (jsonResponse.getBoolean("success")) {
                    val dataArray = jsonResponse.getJSONArray("data")
                    val historyList = mutableListOf<OCRHistoryItem>()
                    
                    for (i in 0 until dataArray.length()) {
                        val item = dataArray.getJSONObject(i)
                        historyList.add(
                            OCRHistoryItem(
                                id = item.getInt("id"),
                                productName = item.getString("product_name"),
                                barcode = item.getString("barcode"),
                                extractedText = item.getString("extracted_text"),
                                ingredients = parseIngredients(item.getJSONArray("ingredients")),
                                halalStatus = item.getString("halal_status"),
                                status = item.getString("status"),
                                createdAt = item.getString("created_at")
                            )
                        )
                    }
                    
                    Result.success(historyList)
                } else {
                    Result.failure(Exception(jsonResponse.getString("message")))
                }
            } else {
                Result.failure(Exception("Failed to get history: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Convert URI to File
     */
    private fun uriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "ocr_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        return file
    }
    
    /**
     * Parse ingredients from JSON array
     */
    private fun parseIngredients(jsonArray: org.json.JSONArray): List<String> {
        val ingredients = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            ingredients.add(jsonArray.getString(i))
        }
        return ingredients
    }
    
    /**
     * Get auth token (implement based on your auth system)
     */
    private fun getAuthToken(): String {
        // Implement based on your authentication system
        // This could be from SharedPreferences, SessionManager, etc.
        return "your_auth_token_here"
    }
    
    /**
     * Mock OCR response for testing
     */
    suspend fun getMockOCRResponse(step: String): OCRResponse {
        return OCRResponse(
            success = true,
            message = "Image processed successfully",
            ocrProductId = (1..1000).random(),
            extractedText = "Ingredients: Wheat flour, vegetable oil, salt, sugar, yeast extract, natural flavors, spices. Contains wheat and soy.",
            ingredients = listOf(
                "Wheat flour",
                "vegetable oil",
                "salt",
                "sugar",
                "yeast extract",
                "natural flavors",
                "spices",
                "soy"
            ),
            confidenceScore = 0.87f,
            processingStep = step,
            nextStep = when (step) {
                "front" -> "back"
                "back" -> "processing"
                "processing" -> "complete"
                else -> "front"
            }
        )
    }
    
    /**
     * Mock OCR statistics
     */
    suspend fun getMockOCRStatistics(): OCRStatistics {
        return OCRStatistics(
            totalScans = 1250,
            pendingReview = 23,
            approvedToday = 45,
            rejectedToday = 8,
            processingAccuracy = 87.5f
        )
    }
}

// Data classes for API responses
data class OCRResponse(
    val success: Boolean,
    val message: String,
    val ocrProductId: Int,
    val extractedText: String,
    val ingredients: List<String>,
    val confidenceScore: Float,
    val processingStep: String,
    val nextStep: String
)

data class OCRStatistics(
    val totalScans: Int,
    val pendingReview: Int,
    val approvedToday: Int,
    val rejectedToday: Int,
    val processingAccuracy: Float
)

data class OCRHistoryItem(
    val id: Int,
    val productName: String,
    val barcode: String,
    val extractedText: String,
    val ingredients: List<String>,
    val halalStatus: String,
    val status: String,
    val createdAt: String
)
