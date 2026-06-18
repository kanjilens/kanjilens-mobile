package com.example.kanjilens.kanji.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjilens.auth.presentation.viewmodel.AuthViewModel
import com.example.kanjilens.ui.theme.AppBackground
import com.example.kanjilens.ui.theme.AppPrimary
import com.example.kanjilens.ui.theme.AppPrimaryLight
import com.example.kanjilens.ui.theme.AppSecondary
import com.example.kanjilens.ui.theme.AppSurface
import com.example.kanjilens.ui.theme.AppTextMuted

private data class DashboardStat(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: @Composable () -> Unit,
)

private data class DailyKanji(
    val id: String,
    val kanji: String,
    val meaning: String,
    val reading: String,
    val viewed: Boolean = false,
    val strokeCount: Int = 0,
    val addedDate: String = "15/06/2026",
    val jlpt: String = "JLPT N4",
    val grade: String = "Grade 1",
    val onReadings: List<String> = emptyList(),
    val kunReadings: List<String> = emptyList(),
    val comments: List<KanjiComment> = emptyList(),
)

private data class KanjiComment(
    val text: String,
    val date: String,
)

@Composable
fun HomeScreen(viewModel: AuthViewModel = viewModel(), onLogout: () -> Unit, onOpenCamera: () -> Unit) {
    LaunchedEffect(viewModel.isLoggedIn) {
        if (!viewModel.isLoggedIn) onLogout()
    }
    val stats = listOf(
        DashboardStat(
            title = "Total de Kanjis",
            value = "12",
            subtitle = "kanjis cadastrados",
            icon = {
                Icon(Icons.Outlined.AutoStories, contentDescription = null, tint = AppSecondary)
            }
        ),
        DashboardStat(
            title = "Kanjis Vistos",
            value = "7",
            subtitle = "marcados como vistos",
            icon = {
                Icon(Icons.Outlined.RemoveRedEye, contentDescription = null, tint = AppSecondary)
            }
        ),
    )
    var dailyKanjis by remember {
        mutableStateOf(
            listOf(
                DailyKanji(
                    id = "midori",
                    kanji = "緑",
                    meaning = "affinity, border, brink, connection, edge, relation, verge",
                    reading = "えにし, ふち, ふちどる, へり, ゆかり, よすが, -ネン, エン",
                    viewed = true,
                    strokeCount = 15,
                    jlpt = "JLPT N1",
                    grade = "Grade 8",
                    onReadings = listOf("-ネン", "エン"),
                    kunReadings = listOf("えにし", "ふち", "ふちどる", "へり", "ゆかり", "よすが"),
                    comments = listOf(
                        KanjiComment("Apareceu no contexto de 緑起 (engi) falando sobre superstições e sorte.", "12/06/2026"),
                        KanjiComment("Lembrar: ligado à ideia de 'conexão entre pessoas'. Muito usado em 緑結び.", "12/06/2026"),
                    )
                ),
                DailyKanji(
                    id = "yuki",
                    kanji = "雪",
                    meaning = "snow",
                    reading = "ゆき, セツ",
                    strokeCount = 11,
                    onReadings = listOf("セツ"),
                    kunReadings = listOf("ゆき")
                ),
                DailyKanji(
                    id = "hoshi",
                    kanji = "星",
                    meaning = "star, spot",
                    reading = "ほし, -ぼし, セイ",
                    viewed = true,
                    strokeCount = 9,
                    onReadings = listOf("セイ"),
                    kunReadings = listOf("ほし", "-ぼし")
                ),
                DailyKanji(
                    id = "kaze",
                    kanji = "風",
                    meaning = "wind, air",
                    reading = "かぜ, かざ-, フウ",
                    strokeCount = 9,
                    onReadings = listOf("フウ"),
                    kunReadings = listOf("かぜ", "かざ-")
                ),
            )
        )
    }
    var selectedKanji by remember { mutableStateOf<DailyKanji?>(null) }

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            HomeBottomBar(onOpenCamera = onOpenCamera)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderCard(onLogout = { viewModel.signOut() })
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF24324A)
                    )
                    Text(
                        text = "Acompanhe seu progresso no aprendizado de kanjis",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTextMuted
                    )
                }
            }
            item {
                BoxWithConstraints {
                    val cardWidth = (maxWidth - 14.dp) / 2
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        stats.forEach { stat ->
                            StatCard(stat = stat, modifier = Modifier.width(cardWidth))
                        }
                    }
                }
            }
            item {
                WeeklyCard()
            }
            item {
                TodaySection(
                    dailyKanjis = dailyKanjis,
                    onKanjiClick = { selectedKanji = it }
                )
            }
        }
    }

    selectedKanji?.let { item ->
        KanjiDetailsDialog(
            item = item,
            onDismiss = { selectedKanji = null },
            onToggleViewed = {
                val updatedItem = item.copy(viewed = !item.viewed)
                dailyKanjis = dailyKanjis.map { kanji ->
                    if (kanji.id == item.id) updatedItem else kanji
                }
                selectedKanji = updatedItem
            },
            onDelete = {
                dailyKanjis = dailyKanjis.filterNot { kanji -> kanji.id == item.id }
                selectedKanji = null
            }
        )
    }
}

@Composable
private fun HeaderCard(onLogout: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(AppSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "漢",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "漢字レンズ",
                    style = MaterialTheme.typography.titleLarge,
                    color = AppPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onLogout)
                    .background(Color(0xFFF5FAF9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Logout, contentDescription = null, tint = AppTextMuted)
            }
        }
    }
}

@Composable
private fun StatCard(stat: DashboardStat, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = stat.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF536173)
                )
                stat.icon()
            }
            Text(
                text = stat.value,
                style = MaterialTheme.typography.displaySmall,
                color = AppPrimary
            )
            Text(
                text = stat.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppTextMuted
            )
        }
    }
}

@Composable
private fun WeeklyCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Esta Semana",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF536173)
                )
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = AppSecondary)
            }
            Text(
                text = "1",
                style = MaterialTheme.typography.displaySmall,
                color = AppPrimary
            )
            Text(
                text = "kanjis adicionados",
                style = MaterialTheme.typography.bodySmall,
                color = AppTextMuted
            )
        }
    }
}

@Composable
private fun TodaySection(
    dailyKanjis: List<DailyKanji>,
    onKanjiClick: (DailyKanji) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Today, contentDescription = null, tint = AppSecondary)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Kanjis de Hoje",
                    style = MaterialTheme.typography.titleLarge,
                    color = AppPrimary
                )
            }
            Text(
                text = "segunda-feira, 15 de junho",
                style = MaterialTheme.typography.bodyLarge,
                color = AppTextMuted
            )
            BoxWithConstraints {
                val cardWidth = (maxWidth - 14.dp) / 2
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    dailyKanjis.chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            rowItems.forEach { item ->
                                DailyKanjiCard(
                                    item = item,
                                    modifier = Modifier.width(cardWidth),
                                    onClick = { onKanjiClick(item) }
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.width(cardWidth))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyKanjiCard(
    item: DailyKanji,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(AppPrimaryLight.copy(alpha = 0.9f))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (item.viewed) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppSecondary)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Visto",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
                Text(
                    text = item.kanji,
                    modifier = Modifier.offset(y = (-8).dp),
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 56.sp),
                    color = Color(0xFF24324A)
                )
                Text(
                    text = "SIGNIFICADO",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTextMuted
                )
                Text(
                    text = item.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF24324A)
                )
                Text(
                    text = "LEITURA",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTextMuted
                )
                Text(
                    text = item.reading,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF24324A)
                )
                Text(
                    text = item.addedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTextMuted
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun KanjiDetailsDialog(
    item: DailyKanji,
    onDismiss: () -> Unit,
    onToggleViewed: () -> Unit,
    onDelete: () -> Unit,
) {
    val modalShape = RoundedCornerShape(10.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.91f)
                    .border(2.dp, Color(0xFFE5E7EB), modalShape),
                shape = modalShape,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppSecondary)
                            .padding(16.dp)
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = "Fechar", tint = Color(0xFF143135))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, end = 44.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.kanji,
                                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 52.sp),
                                    color = Color(0xFF24324A)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    DetailChip(text = item.jlpt, background = Color.White, textColor = AppPrimary)
                                    DetailChip(text = item.grade, background = AppPrimaryLight.copy(alpha = 0.55f), textColor = Color.White)
                                    if (item.viewed) {
                                        DetailChip(text = "Visto", background = Color.White.copy(alpha = 0.22f), textColor = Color.White)
                                    }
                                }
                                Text(
                                    text = item.meaning,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )
                                Text(
                                    text = "Heisig: ${item.meaning.substringBefore(',')}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.82f)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailMetric(label = "TRAÇOS", value = item.strokeCount.toString())
                            DetailMetric(label = "DATA ADICIONADO", value = item.addedDate, showCalendar = true)
                        }

                        HorizontalDivider(color = Color(0xFFE5E9EF))

                        ReadingChipsSection(label = "LEITURAS ON (音読み)", readings = item.onReadings)
                        ReadingChipsSection(label = "LEITURAS KUN (訓読み)", readings = item.kunReadings)

                        HorizontalDivider(color = Color(0xFFE5E9EF))

                        DetailSection(label = "SIGNIFICADOS", value = item.meaning)

                        HorizontalDivider(color = Color(0xFFE5E9EF))
                        CommentsSection(comments = item.comments)

                        HorizontalDivider(color = Color(0xFFE5E9EF))

                        OutlinedButton(
                            onClick = onToggleViewed,
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, AppPrimaryLight),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppPrimary)
                        ) {
                            Icon(Icons.Outlined.RemoveRedEye, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (item.viewed) "Marcar como não visto" else "Marcar como visto")
                        }

                        Button(
                            onClick = onDelete,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Excluir")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailChip(text: String, background: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = textColor)
    }
}

@Composable
private fun ReadingChipsSection(label: String, readings: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = AppTextMuted)
        if (readings.isEmpty()) {
            Text(text = "Nenhuma leitura cadastrada.", style = MaterialTheme.typography.bodyMedium, color = AppTextMuted)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                readings.chunked(4).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowItems.forEach { reading ->
                            DetailChip(
                                text = reading,
                                background = AppPrimaryLight.copy(alpha = 0.85f),
                                textColor = AppPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentsSection(comments: List<KanjiComment>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "COMENTÁRIOS", style = MaterialTheme.typography.labelSmall, color = AppTextMuted)
        if (comments.isEmpty()) {
            Text(text = "Nenhum comentário ainda.", style = MaterialTheme.typography.bodyMedium, color = AppTextMuted)
        } else {
            comments.forEach { comment ->
                CommentCard(comment = comment)
            }
        }
        CommentInputPreview()
    }
}

@Composable
private fun CommentCard(comment: KanjiComment) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E5EC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Outlined.ChatBubbleOutline,
                contentDescription = null,
                tint = AppTextMuted,
                modifier = Modifier.size(18.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = comment.text, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF24324A))
                Text(text = comment.date, style = MaterialTheme.typography.bodySmall, color = AppTextMuted)
            }
        }
    }
}

@Composable
private fun CommentInputPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE0E5EC), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Escreva um comentário sobre este kanji...",
                style = MaterialTheme.typography.bodyLarge,
                color = AppTextMuted.copy(alpha = 0.75f)
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF7CCBC5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Send, contentDescription = "Enviar comentário", tint = Color.White)
        }
    }
}

@Composable
private fun DetailMetric(label: String, value: String, showCalendar: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = AppTextMuted)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showCalendar) {
                Icon(
                    Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = AppTextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = Color(0xFF24324A))
        }
    }
}

@Composable
private fun DetailSection(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = AppTextMuted)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF24324A))
    }
}

@Composable
private fun HomeBottomBar(onOpenCamera: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(icon = { Icon(Icons.Outlined.Home, null) }, label = "Home", selected = true)
                NavItem(icon = { Icon(Icons.Outlined.AutoStories, null) }, label = "Descobertos")
                Spacer(modifier = Modifier.width(72.dp))
                NavItem(icon = { Icon(Icons.Outlined.Search, null) }, label = "Enciclopedia")
                NavItem(icon = { Icon(Icons.Outlined.Settings, null) }, label = "Config.")
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-26).dp)
                .size(74.dp)
                .clip(CircleShape)
                .background(AppSecondary)
                .clickable(onClick = onOpenCamera),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CenterFocusStrong,
                contentDescription = "Abrir camera",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: @Composable () -> Unit,
    label: String,
    selected: Boolean = false,
) {
    val tint = if (selected) AppSecondary else AppTextMuted
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = {})
    ) {
        Box {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides tint,
                content = icon
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = tint
        )
    }
}
