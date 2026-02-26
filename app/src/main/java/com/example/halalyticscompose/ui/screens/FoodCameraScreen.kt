package com.example.halalyticscompose.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

@Composable
fun FoodCameraScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (exc: Exception) {
                        exc.printStackTrace()
                    }
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Guide
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Center Food Here",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleMedium
            )
        }


        // Capture Button
        Button(
            onClick = {
                if (!isCapturing) {
                    isCapturing = true
                    captureImage(context, imageCapture!!) { base64Image ->
                        // Navigate to Result Screen with Base64 as argument (or shared state)
                        // For simplicity, we might store it in a ViewModel or pass it. 
                        // Since Base64 is large, passing via NavArgument is risky. 
                        // Better practice: Save file path, pass path.
                        
                        // For this implementation, let's assume we pass the file path.
                        // But wait, the API expects Base64. We can convert in the next screen too.
                        
                        // Let's pass the file path to avoid TransactionTooLargeException
                        val savedFile = saveBase64ToFile(context, base64Image)
                        // Use Uri.encode to handle special characters in path
                        val encodedPath = android.net.Uri.encode(savedFile.absolutePath)
                        navController.navigate("food_result/$encodedPath")
                        isCapturing = false
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(80.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            enabled = !isCapturing
        ) {
            // Icon or simple circle
        }
        
        if (isCapturing) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (String) -> Unit
) {
    val photoFile = File(
        context.externalCacheDir,
        "food_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                exc.printStackTrace()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // Compress and Convert to Base64
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true) // Resize for AI
                val stream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                val byteArray = stream.toByteArray()
                val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                
                onImageCaptured(base64String)
            }
        }
    )
}

// Helper to save Base64 (or just pass the file path directly if we didn't resize)
// Since we resized, let's strictly save the resized version to a new temp file
private fun saveBase64ToFile(context: Context, base64: String): File {
    val bytes = Base64.decode(base64, Base64.DEFAULT)
    val file = File(context.cacheDir, "temp_food_analysis.jpg")
    val fos = FileOutputStream(file)
    fos.write(bytes)
    fos.close()
    return file
}
