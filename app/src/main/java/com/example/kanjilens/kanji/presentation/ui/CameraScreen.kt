@file:kotlin.OptIn(androidx.camera.core.ExperimentalGetImage::class)

package com.example.kanjilens.kanji.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.kanjilens.kanji.data.remote.KanjiApi
import com.example.kanjilens.kanji.data.remote.KanjiResponse
import com.example.kanjilens.ui.theme.AppSecondary
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random

private val OverlayColor = Color(0xC80A1020)
private val AccentColor = AppSecondary
private val CardColor = Color(0xAA151C2B)
private val HanRegex = Regex("\\p{IsHan}")
private val HintTextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp)
private val SampleKanji = listOf("愛", "日", "水", "山", "空", "火", "月", "木")

@Composable
fun CameraScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val recognizer = remember {
        TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
    }
    val isAnalyzing = remember { AtomicBoolean(false) }
    val inFlightKanji = remember { AtomicReference<String?>(null) }
    val lastFetchedKanji = remember { AtomicReference<String?>(null) }
    val lastFetchedAt = remember { AtomicLong(0L) }
    var detectedKanji by remember { mutableStateOf<String?>(null) }
    val sampleKanji = remember { SampleKanji.random(Random(System.currentTimeMillis())) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, "Permita o uso da camera para ler o kanji.", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(recognizer, cameraExecutor) {
        onDispose {
            recognizer.close()
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (!hasCameraPermission) {
            androidx.compose.runtime.LaunchedEffect(Unit) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { androidContext ->
                    val previewView = PreviewView(androidContext).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(androidContext)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().apply {
                            surfaceProvider = previewView.surfaceProvider
                        }
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage == null || !isAnalyzing.compareAndSet(false, true)) {
                                imageProxy.close()
                                return@setAnalyzer
                            }
                            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            recognizer.process(inputImage)
                                .addOnSuccessListener { result ->
                                    detectedKanji = extractFirstKanji(result.text)?.toString()
                                }
                                .addOnCompleteListener {
                                    isAnalyzing.set(false)
                                    imageProxy.close()
                                }
                        }
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        } catch (_: Exception) {
                            Toast.makeText(context, "Nao foi possivel iniciar a camera.", Toast.LENGTH_SHORT).show()
                        }
                    }, ContextCompat.getMainExecutor(androidContext))
                    previewView
                }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF08101F)),
                contentAlignment = Alignment.Center
            ) {
                ActionCircleButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) })
            }
        }

        CameraOverlay(
            detectedKanji = detectedKanji,
            sampleKanji = sampleKanji,
            onClose = onClose,
            onCapture = {
                val kanjiText = detectedKanji
                if (kanjiText == null) {
                    Toast.makeText(context, "Nenhum kanji detectado ainda.", Toast.LENGTH_SHORT).show()
                } else {
                    val now = System.currentTimeMillis()
                    val requestIsInFlight = inFlightKanji.get() == kanjiText
                    val requestWasRecent =
                        lastFetchedKanji.get() == kanjiText && now - lastFetchedAt.get() < 3_000L
                    if (requestIsInFlight || requestWasRecent) {
                        Toast.makeText(context, "Esse kanji ja foi lido agora ha pouco.", Toast.LENGTH_SHORT).show()
                    } else {
                        inFlightKanji.set(kanjiText)
                        KanjiApi.service.getKanji(kanjiText).enqueue(object : Callback<KanjiResponse> {
                            override fun onResponse(call: Call<KanjiResponse>, response: Response<KanjiResponse>) {
                                inFlightKanji.compareAndSet(kanjiText, null)
                                if (!response.isSuccessful) {
                                    Toast.makeText(context, "Nao foi possivel buscar o kanji $kanjiText.", Toast.LENGTH_SHORT).show()
                                    return
                                }
                                val body = response.body()
                                val meanings = body?.meanings?.take(2)?.joinToString(", ").orEmpty()
                                val message = if (body == null) {
                                    "Kanji $kanjiText encontrado, mas sem resposta valida."
                                } else {
                                    "$kanjiText: $meanings"
                                }
                                lastFetchedKanji.set(kanjiText)
                                lastFetchedAt.set(System.currentTimeMillis())
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }

                            override fun onFailure(call: Call<KanjiResponse>, t: Throwable) {
                                inFlightKanji.compareAndSet(kanjiText, null)
                                Toast.makeText(context, "Erro ao consultar o kanji $kanjiText.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        )
    }
}

@Composable
private fun CameraOverlay(
    detectedKanji: String?,
    sampleKanji: String,
    onClose: () -> Unit,
    onCapture: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        FocusMask(modifier = Modifier.fillMaxSize())
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(CardColor).padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(38.dp).clip(CircleShape).background(AccentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "漢", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Kanji OCR", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Text(text = "ML Kit . CameraX", color = AccentColor, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0x66313846)).clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Close, contentDescription = "Fechar camera", tint = Color.White)
            }
        }

        CenterKanjiPreview(kanji = detectedKanji ?: sampleKanji, modifier = Modifier.align(Alignment.Center))

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 58.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Text(
                text = detectedKanji?.let { "Kanji detectado: $it" } ?: "Aponte a camera para um kanji",
                color = Color.White.copy(alpha = 0.86f),
                style = HintTextStyle
            )
            ActionCircleButton(onClick = onCapture)
        }
    }
}

@Composable
private fun CenterKanjiPreview(kanji: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(96.dp).border(1.dp, Color(0xFF168CFF)).background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(text = kanji, color = Color.White.copy(alpha = 0.55f), fontSize = 72.sp, fontWeight = FontWeight.Normal)
    }
}

@Composable
private fun FocusMask(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val frameSize = 268.dp
        val sideShadeWidth = 52.dp
        Box(modifier = Modifier.fillMaxWidth().height(220.dp).align(Alignment.TopCenter).background(OverlayColor))
        Box(modifier = Modifier.fillMaxWidth().height(250.dp).align(Alignment.BottomCenter).background(OverlayColor))
        Row(modifier = Modifier.size(frameSize).align(Alignment.Center)) {
            Box(modifier = Modifier.width(sideShadeWidth).fillMaxHeight().background(OverlayColor))
            Box(modifier = Modifier.width(frameSize - (sideShadeWidth * 2)).fillMaxHeight().background(Color(0x332D4B8B)))
            Box(modifier = Modifier.width(sideShadeWidth).fillMaxHeight().background(OverlayColor))
        }
        FocusFrame(modifier = Modifier.size(frameSize).align(Alignment.Center))
    }
}

@Composable
private fun FocusFrame(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        CornerMark(Modifier.align(Alignment.TopStart))
        CornerMark(Modifier.align(Alignment.TopEnd), flipX = true)
        CornerMark(Modifier.align(Alignment.BottomStart), flipY = true)
        CornerMark(Modifier.align(Alignment.BottomEnd), flipX = true, flipY = true)
    }
}

@Composable
private fun CornerMark(modifier: Modifier = Modifier, flipX: Boolean = false, flipY: Boolean = false) {
    Box(modifier = modifier.size(42.dp)) {
        val horizontalAlignment = if (flipX) Alignment.TopEnd else Alignment.TopStart
        val verticalAlignment = if (flipY) Alignment.BottomStart else Alignment.TopStart
        val verticalOffset = if (flipY) (-1).dp else 0.dp
        val horizontalOffset = if (flipX) (-1).dp else 0.dp
        Box(modifier = Modifier.align(horizontalAlignment).width(36.dp).height(3.dp).offset(x = horizontalOffset).background(AccentColor))
        Box(modifier = Modifier.align(verticalAlignment).width(3.dp).height(36.dp).offset(y = verticalOffset).background(AccentColor))
    }
}

@Composable
private fun ActionCircleButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(86.dp).clip(CircleShape).background(Color(0xFFF6F2F1)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoCamera,
            contentDescription = "Capturar kanji",
            tint = AccentColor,
            modifier = Modifier.size(36.dp)
        )
    }
}

private fun extractFirstKanji(text: String): Char? {
    return text.firstOrNull { HanRegex.matches(it.toString()) }
}
