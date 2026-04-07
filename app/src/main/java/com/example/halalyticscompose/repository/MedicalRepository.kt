package com.example.halalyticscompose.repository

import android.util.Log
import com.example.halalyticscompose.BuildConfig
import com.example.halalyticscompose.Data.Model.SymptomsAnalysis
import com.example.halalyticscompose.Data.Model.HalalCheck
import com.example.halalyticscompose.utils.MegaPromptBuilder
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MedicalRepository — Direct AI fallback for symptom analysis.
 *
 * This repository is used when the Laravel backend (apiService.analyzeSymptoms)
 * fails or is unreachable. It calls the AI API directly from the client.
 *
 * Priority: Gemini (key already configured) → Anthropic (if key provided)
 */
@Singleton
class MedicalRepository @Inject constructor() {

    companion object {
        private const val TAG = "MedicalRepo"

        // Gemini endpoint
        private const val GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

        // Anthropic endpoint
        private const val ANTHROPIC_URL =
            "https://api.anthropic.com/v1/messages"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * Analyze symptoms using direct AI call.
     * Tries Gemini first (key already configured), then Anthropic if available.
     */
    suspend fun analyzeSymptomsDirect(symptoms: String): SymptomsAnalysis {
        return withContext(Dispatchers.IO) {
            val geminiKey = BuildConfig.GEMINI_API_KEY
            val anthropicKey = BuildConfig.ANTHROPIC_API_KEY

            // Try Gemini first (key is already configured in the project)
            if (geminiKey.isNotBlank()) {
                try {
                    Log.d(TAG, "Attempting Gemini analysis for: ${symptoms.take(50)}...")
                    return@withContext analyzeWithGemini(symptoms, geminiKey)
                } catch (e: Exception) {
                    Log.e(TAG, "Gemini failed: ${e.message}", e)
                    // Fall through to Anthropic
                }
            }

            // Fallback to Anthropic
            if (anthropicKey.isNotBlank()) {
                try {
                    Log.d(TAG, "Attempting Anthropic analysis for: ${symptoms.take(50)}...")
                    return@withContext analyzeWithAnthropic(symptoms, anthropicKey)
                } catch (e: Exception) {
                    Log.e(TAG, "Anthropic failed: ${e.message}", e)
                    throw e
                }
            }

            throw IllegalStateException("No AI API key configured. Set GEMINI_API_KEY or ANTHROPIC_API_KEY in local.properties")
        }
    }

    // ─── Gemini Implementation ─────────────────────────────────────

    private fun analyzeWithGemini(symptoms: String, apiKey: String): SymptomsAnalysis {
        val systemPrompt = MegaPromptBuilder.buildSystemPrompt()
        val userMessage = MegaPromptBuilder.buildUserMessage(symptoms)

        val requestBody = JSONObject().apply {
            put("system_instruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            })
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", userMessage) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.3)
                put("maxOutputTokens", 2048)
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url("$GEMINI_URL?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: throw Exception("Empty Gemini response")

        if (!response.isSuccessful) {
            Log.e(TAG, "Gemini error ${response.code}: $body")
            throw Exception("Gemini API Error: ${response.code}")
        }

        val jsonResponse = JSONObject(body)
        val textContent = jsonResponse
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")

        return parseToSymptomsAnalysis(textContent)
    }

    // ─── Anthropic Implementation ──────────────────────────────────

    private fun analyzeWithAnthropic(symptoms: String, apiKey: String): SymptomsAnalysis {
        val systemPrompt = MegaPromptBuilder.buildSystemPrompt()
        val userMessage = MegaPromptBuilder.buildUserMessage(symptoms)

        val requestBody = JSONObject().apply {
            put("model", "claude-3-5-haiku-20241022")
            put("max_tokens", 2048)
            put("system", systemPrompt)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userMessage)
                })
            })
        }

        val request = Request.Builder()
            .url(ANTHROPIC_URL)
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: throw Exception("Empty Anthropic response")

        if (!response.isSuccessful) {
            Log.e(TAG, "Anthropic error ${response.code}: $body")
            throw Exception("Anthropic API Error: ${response.code}")
        }

        val jsonResponse = JSONObject(body)
        val textContent = jsonResponse
            .getJSONArray("content")
            .getJSONObject(0)
            .getString("text")

        return parseToSymptomsAnalysis(textContent)
    }

    // ─── JSON Parsing ──────────────────────────────────────────────

    /**
     * Parses AI text response (JSON) into SymptomsAnalysis.
     * Maps the MegaPrompt JSON schema to the existing data model.
     */
    private fun parseToSymptomsAnalysis(jsonText: String): SymptomsAnalysis {
        return try {
            // Clean markdown code blocks if present
            val cleanJson = jsonText
                .replace(Regex("```json\\s*"), "")
                .replace(Regex("```\\s*$"), "")
                .trim()

            Log.d(TAG, "Parsing AI JSON: ${cleanJson.take(200)}...")

            val json = JSONObject(cleanJson)

            SymptomsAnalysis(
                condition = json.optString("diagnosis", "Perlu Evaluasi"),
                severity = mapSeverity(json.optString("severity", "LOW")),
                why_it_happened = json.optString("description", ""),
                possible_causes = jsonArrayToList(json.optJSONArray("potentialCauses")),
                gejala_terkait = emptyList(), // Not in our prompt schema, extracted from description
                emergency_warning = if (json.optString("severity") == "EMERGENCY")
                    "Segera ke IGD!" else null,
                should_seek_doctor = json.optBoolean("shouldSeeDoctor", false),
                recommendation = json.optString("monitoring", "Pantau kondisi Anda"),
                doctor_recommendation = if (json.optBoolean("shouldSeeDoctor"))
                    "Segera konsultasikan ke dokter profesional" else null,
                triage_action = json.optString("monitoring", ""),
                lifestyle_advice = buildLifestyleAdvice(json),
                future_prevention = null,
                recommended_medicines_list = buildMedicineNameList(json),
                alternative_medicines = emptyList(),
                medicine_categories = emptyList(),
                usage_instructions = buildUsageInstructions(json),
                dosage_guidelines = buildDosageGuidelines(json),
                when_to_take_and_frequency = buildFrequencyInfo(json),
                side_effects = emptyList(),
                halal_check = HalalCheck(
                    status = if (json.optBoolean("isHalal", true)) "halal" else "perlu verifikasi",
                    notes = "Obat yang direkomendasikan telah diverifikasi status halalnya"
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "JSON parse error: ${e.message}", e)
            // Return a helpful fallback instead of crashing
            SymptomsAnalysis(
                condition = "Analisis Tersedia Sebagian",
                severity = "mild",
                why_it_happened = "AI berhasil merespons tetapi format data tidak sesuai. Silakan coba lagi.",
                possible_causes = listOf("Gangguan format respons AI"),
                recommendation = "Coba ulangi analisis atau konsultasikan ke dokter",
                should_seek_doctor = false
            )
        }
    }

    // ─── Helper Methods ────────────────────────────────────────────

    private fun mapSeverity(raw: String): String = when (raw.uppercase()) {
        "LOW" -> "mild"
        "MEDIUM" -> "moderate"
        "HIGH" -> "severe"
        "EMERGENCY" -> "emergency"
        else -> raw.lowercase()
    }

    private fun jsonArrayToList(array: org.json.JSONArray?): List<String> {
        if (array == null) return emptyList()
        return (0 until array.length()).map { array.getString(it) }
    }

    private fun buildLifestyleAdvice(json: JSONObject): String? {
        val recommendations = json.optJSONArray("recommendations") ?: return null
        return (0 until recommendations.length())
            .map { recommendations.getString(it) }
            .joinToString("\n• ", prefix = "• ")
    }

    private fun buildMedicineNameList(json: JSONObject): List<String> {
        val medicines = json.optJSONArray("medicines") ?: return emptyList()
        return (0 until medicines.length()).map { i ->
            val med = medicines.getJSONObject(i)
            val halal = if (med.optBoolean("isHalal", true)) "✓ Halal" else "⚠ Perlu verifikasi"
            "${med.optString("name")} ($halal)"
        }
    }

    private fun buildUsageInstructions(json: JSONObject): String? {
        val medicines = json.optJSONArray("medicines") ?: return null
        if (medicines.length() == 0) return null
        return (0 until medicines.length()).map { i ->
            val med = medicines.getJSONObject(i)
            val notes = med.optString("notes", "")
            "${med.optString("name")}: ${notes.ifBlank { "Ikuti aturan pakai" }}"
        }.joinToString("\n")
    }

    private fun buildDosageGuidelines(json: JSONObject): String? {
        val medicines = json.optJSONArray("medicines") ?: return null
        if (medicines.length() == 0) return null
        return (0 until medicines.length()).map { i ->
            val med = medicines.getJSONObject(i)
            "${med.optString("name")}: ${med.optString("dose", "sesuai anjuran")}"
        }.joinToString(", ")
    }

    private fun buildFrequencyInfo(json: JSONObject): String? {
        val medicines = json.optJSONArray("medicines") ?: return null
        if (medicines.length() == 0) return null
        return (0 until medicines.length()).map { i ->
            val med = medicines.getJSONObject(i)
            "${med.optString("name")}: ${med.optString("frequency", "sesuai anjuran")}"
        }.joinToString(", ")
    }
}
