package com.example.halalyticscompose.ai

import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import android.util.Log

class GeminiAnalyzer {

    // Initialize Vertex AI for Firebase (Gemini 1.5 Flash)
    private val generativeModel = Firebase.vertexAI.generativeModel(
        modelName = "gemini-1.5-flash"
    )

    /**
     * Analyze image to extract product info (OCR)
     */
    suspend fun analyzeImage(bitmap: android.graphics.Bitmap): Flow<AiAnalysisResult> = flow {
        try {
            emit(AiAnalysisResult.Loading)
            
            val prompt = "Identifikasi produk ini. Ambil Nama Produk, Brand, Barcode (jika ada), dan daftar Komposisi. Format dalam JSON: { \"name\": \"...\", \"brand\": \"...\", \"barcode\": \"...\", \"ingredients\": \"...\" }"
            
            val content = com.google.firebase.vertexai.type.content {
                image(bitmap)
                text(prompt)
            }
            
            val response = generativeModel.generateContent(content)
            val resultText = response.text ?: "{}"
            
            emit(AiAnalysisResult.Success(resultText))
        } catch (e: Exception) {
            Log.e("GeminiAnalyzer", "OCR failed", e)
            emit(AiAnalysisResult.Error(e.message ?: "Gagal memproses gambar"))
        }
    }

    /**
     * Analyze ingredients from local source
     */
    suspend fun analyzeIngredients(ingredientsText: String): Flow<AiAnalysisResult> = flow {
        try {
            emit(AiAnalysisResult.Loading)
            
            val prompt = "Analisis apakah bahan-bahan ini halal menurut standar MUI dan berikan penjelasan singkat: $ingredientsText"
            
            val response = generativeModel.generateContent(prompt)
            val resultText = response.text ?: "Tidak ada hasil analisis"
            
            emit(AiAnalysisResult.Success(resultText))
        } catch (e: Exception) {
            Log.e("GeminiAnalyzer", "Analysis failed", e)
            emit(AiAnalysisResult.Error(e.message ?: "Terjadi kesalahan saat analisis"))
        }
    }
}

sealed class AiAnalysisResult {
    object Loading : AiAnalysisResult()
    data class Success(val analysis: String) : AiAnalysisResult()
    data class Error(val message: String) : AiAnalysisResult()
}
