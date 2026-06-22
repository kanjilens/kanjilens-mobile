@file:kotlin.OptIn(androidx.camera.core.ExperimentalGetImage::class)

package com.example.kanjilens.kanji.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.kanjilens.kanji.data.remote.KanjiApi
import com.example.kanjilens.kanji.data.remote.KanjiFirestoreRepository
import com.example.kanjilens.kanji.data.remote.KanjiResponse
import com.example.kanjilens.kanji.model.KanjiEntry
import com.example.kanjilens.ui.theme.AppPrimary
import com.example.kanjilens.ui.theme.AppPrimaryLight
import com.example.kanjilens.ui.theme.AppSecondary
import com.example.kanjilens.ui.theme.AppTextMuted
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

private val OverlayColor = Color(0xC80A1020)
private val CameraCardColor = Color(0xAA151C2B)
private val HanRegex = Regex("\\p{IsHan}")

private data class OcrCandidate(
    val kanji: String,
    val sourceText: String,
)

@Composable
fun CameraScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val repository = remember { KanjiFirestoreRepository() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val recognizer = remember {
        TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
    }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedEntry by remember { mutableStateOf<KanjiEntry?>(null) }
    var recognizedCandidates by remember { mutableStateOf<List<OcrCandidate>>(emptyList()) }
    var isSelectingCandidate by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
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
            Toast.makeText(context, "Permita o uso da camera para tirar a foto do kanji.", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(recognizer, cameraExecutor) {
        onDispose {
            recognizer.close()
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1020))
    ) {
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
                        val captureUseCase = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = captureUseCase

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                captureUseCase
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
                CaptureActionButton(
                    enabled = true,
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                )
            }
        }

        CameraOverlay(
            isProcessing = isProcessing,
            onClose = onClose,
            onCapture = {
                val capture = imageCapture
                if (capture == null || isProcessing) return@CameraOverlay

                isProcessing = true
                capture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        @androidx.annotation.OptIn(ExperimentalGetImage::class)
                        override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                            val mediaImage = image.image
                            if (mediaImage == null) {
                                image.close()
                                isProcessing = false
                                Toast.makeText(context, "Nao foi possivel capturar a foto.", Toast.LENGTH_SHORT).show()
                                return
                            }

                            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                            recognizer.process(inputImage)
                                .addOnSuccessListener { result ->
                                    val candidates = extractKanjiCandidates(result.text)
                                    if (candidates.isEmpty()) {
                                        isProcessing = false
                                        Toast.makeText(context, "Nenhum kanji foi identificado na foto.", Toast.LENGTH_SHORT).show()
                                        return@addOnSuccessListener
                                    }

                                    recognizedCandidates = candidates
                                    isSelectingCandidate = true
                                    isProcessing = false
                                }
                                .addOnFailureListener {
                                    isProcessing = false
                                    Toast.makeText(context, "Falha ao processar a foto do kanji.", Toast.LENGTH_SHORT).show()
                                }
                                .addOnCompleteListener {
                                    image.close()
                                }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            isProcessing = false
                            Toast.makeText(context, "Erro ao tirar a foto.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        )

        if (isSelectingCandidate) {
            OcrCandidateSelectionOverlay(
                candidates = recognizedCandidates,
                onDismiss = {
                    isSelectingCandidate = false
                    recognizedCandidates = emptyList()
                },
                onSelect = { selectedKanji ->
                    isSelectingCandidate = false
                    recognizedCandidates = emptyList()
                    isProcessing = true
                    resolveKanjiEntry(
                        repository = repository,
                        kanji = selectedKanji,
                        onResolved = { entry ->
                            isProcessing = false
                            if (entry == null) {
                                Toast.makeText(context, "Nao foi possivel consultar o kanji $selectedKanji.", Toast.LENGTH_SHORT).show()
                            } else {
                                capturedEntry = entry
                            }
                        },
                        onError = { message ->
                            isProcessing = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }

        capturedEntry?.let { entry ->
            CameraResultOverlay(
                entry = entry,
                onDismiss = { capturedEntry = null },
                onScanAnother = { capturedEntry = null },
                onAddToCollection = { comment ->
                    repository.saveScannedKanji(
                        entry = entry,
                        comment = comment,
                        onSuccess = {
                            Toast.makeText(context, "Kanji adicionado a colecao.", Toast.LENGTH_SHORT).show()
                            onClose()
                        },
                        onError = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onClose = onClose
            )
        }
    }
}

@Composable
private fun CameraOverlay(
    isProcessing: Boolean,
    onClose: () -> Unit,
    onCapture: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        FocusMask(modifier = Modifier.fillMaxSize())

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CameraCardColor)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AppSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "漢", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Kanji OCR", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Text(text = "ML Kit . CameraX", color = AppPrimaryLight, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0x66313846))
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Close, contentDescription = "Fechar camera", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = if (isProcessing) "Processando foto do kanji..." else "Aponte e tire a foto do kanji",
                color = Color.White.copy(alpha = 0.88f),
                style = MaterialTheme.typography.bodyMedium
            )
            CaptureActionButton(enabled = !isProcessing, onClick = onCapture)
        }
    }
}

@Composable
private fun OcrCandidateSelectionOverlay(
    candidates: List<OcrCandidate>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC08101F))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(18.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Qual kanji foi lido?", style = MaterialTheme.typography.titleLarge, color = Color(0xFF24324A))
                        Text(text = "Escolha o caractere correto antes de salvar.", style = MaterialTheme.typography.bodyMedium, color = AppTextMuted)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Outlined.Close, contentDescription = null)
                    }
                }

                Text(
                    text = "Candidatos detectados",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTextMuted
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    candidates.forEach { candidate ->
                        CandidateChip(
                            candidate = candidate,
                            onClick = { onSelect(candidate.kanji) }
                        )
                    }
                }

                OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Ler outra foto")
                }
            }
        }
    }
}

@Composable
private fun CandidateChip(
    candidate: OcrCandidate,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppPrimaryLight.copy(alpha = 0.88f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = candidate.kanji, style = MaterialTheme.typography.headlineMedium, color = Color(0xFF24324A))
            Text(text = candidate.sourceText, style = MaterialTheme.typography.labelSmall, color = AppTextMuted)
        }
    }
}

@Composable
private fun CaptureActionButton(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(CircleShape)
            .background(if (enabled) Color(0xFFF6F2F1) else Color(0xFFCAD2D8))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoCamera,
            contentDescription = "Capturar kanji",
            tint = AppSecondary,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
private fun FocusMask(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val frameSize = 286.dp
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

        Box(
            modifier = Modifier
                .align(horizontalAlignment)
                .width(36.dp)
                .height(3.dp)
                .offset(x = horizontalOffset)
                .background(AppSecondary)
        )
        Box(
            modifier = Modifier
                .align(verticalAlignment)
                .width(3.dp)
                .height(36.dp)
                .offset(y = verticalOffset)
                .background(AppSecondary)
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun CameraResultOverlay(
    entry: KanjiEntry,
    onDismiss: () -> Unit,
    onScanAnother: () -> Unit,
    onAddToCollection: (String) -> Unit,
    onClose: () -> Unit,
) {
    var commentExpanded by remember { mutableStateOf(true) }
    var commentText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66040A16))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.78f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppSecondary)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = "Fechar modal", tint = Color(0xFF143135))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, end = 42.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = entry.kanji,
                                style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp),
                                color = Color(0xFF24324A)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PhotoInfoChip(entry.jlpt)
                                PhotoInfoChip(entry.grade)
                                PhotoInfoChip("${entry.strokeCount} tracos")
                            }
                            Text(
                                text = entry.meaning,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.58f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoBanner()
                    ReadingGroup(label = "LEITURAS ON (音読み)", readings = entry.onReadings)
                    ReadingGroup(label = "LEITURAS KUN (訓読み)", readings = entry.kunReadings)
                    if (entry.nameReadings.isNotEmpty()) {
                        ReadingGroup(label = "LEITURAS DE NOMES", readings = entry.nameReadings)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "HEISIG", style = MaterialTheme.typography.labelSmall, color = AppTextMuted)
                        Text(
                            text = entry.heisig.ifBlank { entry.meaning.substringBefore(',') },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF24324A)
                        )
                    }

                    CommentComposer(
                        expanded = commentExpanded,
                        commentText = commentText,
                        onToggle = { commentExpanded = !commentExpanded },
                        onTextChange = { commentText = it }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onAddToCollection(commentText) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AppSecondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar a minha colecao")
                    }

                    BoxWithConstraints {
                        val actionWidth = (maxWidth - 10.dp) / 2
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = onScanAnother,
                                modifier = Modifier.width(actionWidth),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Escanear outro")
                            }
                            OutlinedButton(
                                onClick = onClose,
                                modifier = Modifier.width(actionWidth),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.AutoStories, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Fechar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoInfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.24f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun InfoBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE9FBF7))
            .border(1.dp, Color(0xFF86EFE0), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = AppSecondary, modifier = Modifier.size(18.dp))
        Text(
            text = "Kanji identificado com ML Kit OCR",
            style = MaterialTheme.typography.bodyMedium,
            color = AppPrimary
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ReadingGroup(label: String, readings: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = AppTextMuted)
        if (readings.isEmpty()) {
            Text(text = "Nenhuma leitura encontrada.", style = MaterialTheme.typography.bodyMedium, color = AppTextMuted)
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                readings.forEach { reading ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppPrimaryLight.copy(alpha = 0.88f))
                            .border(1.dp, Color(0xFF86EFE0), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = reading, style = MaterialTheme.typography.bodyMedium, color = AppPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentComposer(
    expanded: Boolean,
    commentText: String,
    onToggle: () -> Unit,
    onTextChange: (String) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E5EC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = AppSecondary)
                    Text(
                        text = if (expanded) "Comentario adicionado" else "Adicionar comentario",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF425063)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Outlined.Close else Icons.Outlined.Add,
                    contentDescription = null,
                    tint = AppTextMuted
                )
            }

            if (expanded) {
                Text(
                    text = "Escreva uma nota sobre este kanji - contexto em que apareceu, dificuldade, associacoes...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTextMuted
                )
                OutlinedTextField(
                    value = commentText,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    placeholder = { Text("Ex.: Vi esse kanji no titulo do manga...") },
                    shape = RoundedCornerShape(12.dp)
                )
                Text(
                    text = "O comentario sera salvo junto com o kanji na colecao.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTextMuted
                )
            }
        }
    }
}

private fun extractKanjiCandidates(text: String): List<OcrCandidate> {
    val seen = linkedSetOf<String>()
    val candidates = mutableListOf<OcrCandidate>()

    text.lineSequence()
        .flatMap { line ->
            line.asSequence()
                .filter { HanRegex.matches(it.toString()) }
                .map { it.toString() }
        }
        .forEach { kanji ->
            if (seen.add(kanji)) {
                candidates += OcrCandidate(kanji = kanji, sourceText = kanji)
            }
        }

    return candidates
}

private fun resolveKanjiEntry(
    repository: KanjiFirestoreRepository,
    kanji: String,
    onResolved: (KanjiEntry?) -> Unit,
    onError: (String) -> Unit,
) {
    repository.fetchCatalogKanjiBySymbol(
        symbol = kanji,
        onSuccess = { firestoreEntry ->
            if (firestoreEntry != null) {
                onResolved(firestoreEntry)
            } else {
                KanjiApi.service.getKanji(kanji).enqueue(object : Callback<KanjiResponse> {
                    override fun onResponse(call: Call<KanjiResponse>, response: Response<KanjiResponse>) {
                        if (!response.isSuccessful || response.body() == null) {
                            onError("Nao foi possivel consultar o kanji $kanji.")
                            return
                        }

                        onResolved(response.body()?.toKanjiEntry())
                    }

                    override fun onFailure(call: Call<KanjiResponse>, t: Throwable) {
                        onError("Erro ao consultar o kanji $kanji.")
                    }
                })
            }
        },
        onError = { message ->
            KanjiApi.service.getKanji(kanji).enqueue(object : Callback<KanjiResponse> {
                override fun onResponse(call: Call<KanjiResponse>, response: Response<KanjiResponse>) {
                    if (!response.isSuccessful || response.body() == null) {
                        onError(message)
                        return
                    }

                    onResolved(response.body()?.toKanjiEntry())
                }

                override fun onFailure(call: Call<KanjiResponse>, t: Throwable) {
                    onError(message)
                }
            })
        }
    )
}

private fun KanjiResponse.toKanjiEntry(): KanjiEntry {
    return KanjiEntry(
        id = kanji,
        kanji = kanji,
        meaning = meanings.joinToString(", "),
        reading = (kun_readings + on_readings).joinToString(", "),
        strokeCount = stroke_count ?: 0,
        jlpt = jlpt?.let { "JLPT N$it" } ?: "JLPT -",
        grade = grade?.let { "Grade $it" } ?: "Grade -",
        onReadings = on_readings,
        kunReadings = kun_readings,
        nameReadings = name_readings,
        heisig = heisig_en.orEmpty()
    )
}
