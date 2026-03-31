package com.example.halalyticscompose.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

/**
 * Helper class wrapping Android TextToSpeech engine.
 * Reads AI responses aloud using Google TTS ("Mbak Google").
 */
class TextToSpeechHelper(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false

    var onSpeakingStarted: (() -> Unit)? = null
    var onSpeakingDone: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("id", "ID"))
                isReady = result != TextToSpeech.LANG_MISSING_DATA &&
                          result != TextToSpeech.LANG_NOT_SUPPORTED

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
                    Log.w("TTS", "Indonesian language not supported on this device")
                }
            } else {
                Log.e("TTS", "TTS initialization failed with status: $status")
            }
        }
    }

    fun speak(text: String) {
        if (isReady && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ai_response")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun isSpeaking(): Boolean = tts?.isSpeaking == true

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
