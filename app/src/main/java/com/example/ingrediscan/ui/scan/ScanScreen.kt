package com.example.ingrediscan.ui.scan

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ingrediscan.BackEnd.TextRecognition.TextRecognitionHelper
import com.example.ingrediscan.R
import com.example.ingrediscan.ui.theme.lightGreen
import com.example.ingrediscan.ui.previous_results.WavyCircleExample
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreviewWithBarcodeScanner(
    captureRequest: Boolean,
    onImageCaptured: (Bitmap, Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Camera Preview use case
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // ImageAnalysis use case to intercept camera frames
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            if (captureRequest) {
                                val rotation = imageProxy.imageInfo.rotationDegrees
                                val bitmap = imageProxy.toBitmap() // Convert YUV to Bitmap
                                onImageCaptured(bitmap, rotation)
                            }
                            imageProxy.close() // Always close frame
                        }
                    }

                // Bind all use cases to the lifecycle
                val selector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageAnalysis)
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalGetImage::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = viewModel(),
    onNavigateHome: () -> Unit
) {
    // State to control whether result sheet is shown
    val showResult = remember { mutableStateOf(false) }

    // Flag to request one-time image capture
    val captureRequest = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Observing scanned item from ViewModel
    val scannedItem by viewModel.scannedItem.observeAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        // Sets up the CameraX preview and handles scanning when captureRequest is true
        CameraPreviewWithBarcodeScanner(
            captureRequest = captureRequest.value,
            onImageCaptured = { bitmap, rotation ->
                // Launch image processing in a coroutine
                coroutineScope.launch(Dispatchers.Default) {
                    val (ingredients, barcodes) = TextRecognitionHelper.extractProductInfo(bitmap, rotation)
                    Log.d("Scan", "Barcode(s): $barcodes")
                    if (barcodes.isNotEmpty()) {
                        // Trigger ViewModel update with barcode
                        viewModel.fetchItemDetails(barcodes.first())
                        showResult.value = true
                    }
                    // Reset request flag after capture
                    captureRequest.value = false
                }
            }
        )

        // Camera UI overlay including cutout, flash, and shutter button
        BoxWithCutout(
            viewModel = viewModel,
            onNavigateHome = onNavigateHome,
            onShutterClick = {
                captureRequest.value = true // Set capture flag to true on shutter
//                // TEMPORARY — emulator testing (OREOS FROM API)
//                viewModel.fetchItemDetails("044000033248")
//                showResult.value = true

            }
        )

        // Show scan result sheet when item is available
        if (showResult.value && scannedItem != null) {
            ModalBottomSheet(
                onDismissRequest = { showResult.value = false },
                modifier = Modifier.fillMaxHeight(1.0f)
            ) {
                ScanResultSheetContent(
                    itemName = scannedItem!!.name,
                    company = scannedItem!!.brand,
                    facts = listOf(
                        "Calories: ${scannedItem!!.calories}kcal",
                        "Protein: ${scannedItem!!.protein}g",
                        "Carbs: ${scannedItem!!.carbs}g",
                        "Fat: ${scannedItem!!.fat}g"
                    ),
                    description = scannedItem!!.description,
                    grade = scannedItem!!.grade.toString(),
                    onScanMore = {
                        showResult.value = false
                    }
                )
            }
        }
    }
}



@Composable
fun ScanResultSheetContent(
    itemName: String,
    company: String,
    facts: List<String>,
    description: String,
    grade: String,
    onScanMore: () -> Unit
) {
    // Root box to layer the scrollable content and fixed button
    Box(modifier = Modifier.fillMaxSize()) {

        // Column for scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Enable vertical scrolling
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 100.dp), // Leave room at bottom for button
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product name
            Text(
                text = itemName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                lineHeight = 52.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Brand/company name
            Text(
                text = company,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            )

            // Nutrition facts
            facts.forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )
            }

            // Wavy grade badge
            Box(
                modifier = Modifier
                    .size(250.dp),
                contentAlignment = Alignment.Center
            ) {
                WavyCircleExample(text = grade, fontSize = 80.sp)
            }

            // Ingredients description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 30.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp, top = 8.dp)
            )
        }

        // Fixed bottom scan button
        Button(
            onClick = onScanMore,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF),
                contentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter) // Pin to bottom of the sheet
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Scan More", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
    }
}


// This is the composable to make the cutout for the camera UI
@Composable
fun BoxWithCutout(
    viewModel: ScanViewModel,
    onNavigateHome: () -> Unit,
    onShutterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    val rectWidth = 350.dp.toPx()
                    val rectHeight = 600.dp.toPx()
                    val cornerRadius = 20.dp.toPx()
                    drawRoundRect(
                        color = Color.Black,
                        topLeft = Offset(
                            (size.width - rectWidth) / 2,
                            (size.height - rectHeight) / 2 - 50.dp.toPx()
                        ),
                        size = Size(rectWidth, rectHeight),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                        blendMode = BlendMode.Clear
                    )
                }
            }
            .background(Color.Black.copy(alpha = 0.80f))
    ) {
        // ⚡ Flash, nav, settings
        val isFlashOn by viewModel.isFlashOn.observeAsState(false)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp, 20.dp, 15.dp, 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Arrow Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        viewModel.onArrowClick(onNavigateHome)
                    },
                tint = Color.White
            )
            Image(
                painter = painterResource(id = R.drawable.boltauto),
                contentDescription = "Change flash on/off",
                modifier = Modifier
                    .size(35.dp)
                    .clickable { viewModel.toggleFlash() },
                colorFilter = ColorFilter.tint(Color.White)
            )
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.onSettingsClick() },
                tint = Color.White
            )
        }

        // Bottom shutter & icons
        ButtonRow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            onImageButtonClick = { onShutterClick() }
        )
    }
}


// This is the for the bottom row of the scan screen (the shutter and the icons)
@Composable
fun ButtonRow(modifier: Modifier = Modifier, onImageButtonClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 30.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = lightGreen,
                contentColor = Color.White
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.applepng),
                contentDescription = "Produce Icon",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ImageButton(
            onClick = onImageButtonClick,
            imageRes = R.drawable.aperture,
            contentDescription = "Image Button",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = lightGreen,
                contentColor = Color.White
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.barpng),
                contentDescription = "Barcode Icon",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@Composable
fun ImageButton(
    onClick: () -> Unit,
    imageRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

/**
 * Extension function to convert ImageProxy to Bitmap.
 * Needed to run ML Kit Barcode scanning on captured frames.
 */
fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = android.graphics.YuvImage(
        nv21,
        android.graphics.ImageFormat.NV21,
        width,
        height,
        null
    )

    val out = java.io.ByteArrayOutputStream()
    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
