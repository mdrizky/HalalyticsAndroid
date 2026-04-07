package com.example.halalyticscompose.data.ocr

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class OcrProcessor @Inject constructor() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    suspend fun processImage(imageProxy: ImageProxy): String = suspendCancellableCoroutine { continuation ->
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            continuation.resume("")
            return@suspendCancellableCoroutine
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        recognizer.process(inputImage)
            .addOnSuccessListener { result ->
                if (continuation.isActive) {
                    continuation.resume(result.text)
                }
            }
            .addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resume("")
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
