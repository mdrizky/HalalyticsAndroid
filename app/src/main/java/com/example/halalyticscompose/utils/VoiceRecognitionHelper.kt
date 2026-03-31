package com.example.halalyticscompose.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

/**
 * Helper class wrapping Android SpeechRecognizer for Speech-to-Text.
 * Supports Indonesian language.
 */
class VoiceRecognitionHelper(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onPartial: ((String) -> Unit)? = null,
        onListeningStarted: (() -> Unit)? = null,
        onListeningEnded: (() -> Unit)? = null
    ) {
        if (isListening) return
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition tidak tersedia di perangkat ini.")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                onListeningStarted?.invoke()
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
                onListeningEnded?.invoke()
            }

            override fun onError(error: Int) {
                isListening = false
                onListeningEnded?.invoke()
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Suara tidak terdeteksi, coba lagi."
                    SpeechRecognizer.ERROR_NETWORK -> "Tidak ada koneksi internet."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Koneksi timeout."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tidak ada suara terdeteksi."
                    else -> "Error tidak diketahui ($error)"
                }
                Log.e("VoiceRecognition", "Error: $message")
                onError(message)
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                onListeningEnded?.invoke()
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = data?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    onResult(text)
                } else {
                    onError("Suara tidak terdeteksi, coba lagi.")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val data = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                data?.firstOrNull()?.let { onPartial?.invoke(it) }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        isListening = false
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        isListening = false
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
