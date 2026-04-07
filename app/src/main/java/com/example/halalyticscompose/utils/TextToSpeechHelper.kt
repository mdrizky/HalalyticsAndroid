package com.example.halalyticscompose.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

/**
 * Helper class wrapping Android TextToSpeech engine.
 * Reads AI responses aloud using Google TTS ("Mbak Google").
 *
 * ✅ Supports Indonesian with English fallback
 * ✅ Callback hooks for UI state (speaking started/done)
 * ✅ Proper lifecycle management
 */
class TextToSpeechHelper(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false

    var onSpeakingStarted: (() -> Unit)? = null
    var onSpeakingDone: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try Indonesian first
                val idResult = tts?.setLanguage(Locale("id", "ID"))
                isReady = if (idResult == TextToSpeech.LANG_MISSING_DATA ||
                    idResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to English
                    Log.w("TTS", "Indonesian not supported, falling back to English")
                    val enResult = tts?.setLanguage(Locale.US)
                    enResult != TextToSpeech.LANG_MISSING_DATA &&
                        enResult != TextToSpeech.LANG_NOT_SUPPORTED
                } else {
                    true
                }

                // Slow down speech slightly for clarity
                tts?.setSpeechRate(0.85f)
                tts?.setPitch(1.0f)

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        onSpeakingStarted?.invoke()
                    }
                    override fun onDone(utteranceId: String?) {
                        onSpeakingDone?.invoke()
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        onSpeakingDone?.invoke()
                    }
                })

                if (!isReady) {
                    Log.e("TTS", "No supported language found on this device")
                } else {
                    Log.d("TTS", "TTS initialized successfully")
                }
            } else {
                Log.e("TTS", "TTS initialization failed with status: $status")
            }
        }
    }

    fun speak(text: String) {
        if (isReady && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ai_response_${System.currentTimeMillis()}")
        }
    }

    /**
     * Build and speak a comprehensive diagnosis summary from SymptomsAnalysis.
     * This creates a natural-sounding medical report in Indonesian.
     */
    fun speakDiagnosisReport(
        condition: String,
        severity: String,
        causes: List<String>,
        recommendation: String?,
        medicines: List<String>,
        shouldSeeDoctor: Boolean
    ) {
        val sb = StringBuilder()
        sb.append("Hasil analisis kesehatan. ")
        sb.append("Kemungkinan kondisi: $condition. ")

        // Severity
        val severityText = when (severity.lowercase()) {
            "mild" -> "ringan"
            "moderate" -> "sedang"
            "severe" -> "berat"
            "emergency" -> "darurat, segera ke rumah sakit"
            else -> severity
        }
        sb.append("Tingkat keparahan: $severityText. ")

        // Causes
        if (causes.isNotEmpty()) {
            sb.append("Kemungkinan penyebab: ")
            causes.take(3).forEachIndexed { i, cause ->
                sb.append("${i + 1}, $cause. ")
            }
        }

        // Recommendation
        recommendation?.takeIf { it.isNotBlank() }?.let {
            sb.append("Saran: $it. ")
        }

        // Medicines
        if (medicines.isNotEmpty()) {
            sb.append("Obat yang disarankan: ")
            medicines.take(3).forEach { med ->
                sb.append("$med. ")
            }
        }

        // Doctor warning
        if (shouldSeeDoctor) {
            sb.append("PENTING: Segera konsultasikan kondisi Anda ke dokter profesional. ")
        }

        speak(sb.toString())
    }

    fun stop() {
        tts?.stop()
        // Trigger callback so UI updates immediately
        onSpeakingDone?.invoke()
    }

    fun isSpeaking(): Boolean = tts?.isSpeaking == true

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
