package com.example.ingrediscan.ui.scan

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
import com.example.ingrediscan.R
import com.example.ingrediscan.ui.theme.lightGreen
import com.example.ingrediscan.ui.previous_results.WavyCircleExample


@Composable
fun CameraPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = viewModel(),
    onNavigateHome: () -> Unit
) {
    val showResult = remember { mutableStateOf(false) }
    BoxWithCutout(
        viewModel = viewModel,
        onNavigateHome = onNavigateHome,
        onShutterClick = { showResult.value = true }
    )

    if (showResult.value) {
        ModalBottomSheet(
            onDismissRequest = { showResult.value = false },
            modifier = Modifier
                .fillMaxWidth()
                .height(1000.dp)
        ) {
            ScanResultSheetContent( // Example content
                itemName = "Wild Blueberry Granola Bars",
                company = "Sister Fruit Company",
                facts = listOf("Calories: 146kcal", "Protein: 8g", "Carbs: 25g", "Fat: 10g"),
                description = "The All-Natural Granola Bar — LOADED with Blueberries. " +
                        "A premium blend of fresh Wild Blueberries, whole grains and almonds " +
                        "are carefully baked in small batches for a soft chewy snack that is deliciously fruity!",
                grade = "B+",
                onScanMore = { showResult.value = false }
            )
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
    Box(modifier = Modifier.fillMaxSize()) {
        // Scrollable content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 100.dp), // leave space for the button
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Text(
                text = company,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            )

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

            Box(
                modifier = Modifier
                    .size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                WavyCircleExample(text = grade)
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp, top = 8.dp)
            )

        }

        // Fixed bottom button
        Button(
            onClick = onScanMore,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF),
                contentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Scan More", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
    }
}


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
    ) {
        CameraPreview()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.80f))
        ) {}

        ButtonRow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            onImageButtonClick = { onShutterClick() }
        )

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
    }
}

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
