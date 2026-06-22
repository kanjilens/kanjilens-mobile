package com.example.kanjilens.kanji.presentation.ui

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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Today
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kanjilens.R
import com.example.kanjilens.kanji.data.remote.KanjiFirestoreRepository
import com.example.kanjilens.kanji.model.KanjiComment
import com.example.kanjilens.kanji.model.KanjiEntry
import com.example.kanjilens.ui.navigation.AppBottomBar
import com.example.kanjilens.ui.navigation.AppTab
import com.example.kanjilens.ui.theme.AppPrimary
import com.example.kanjilens.ui.theme.AppPrimaryLight
import com.example.kanjilens.ui.theme.AppSecondary
import com.example.kanjilens.ui.theme.AppTextMuted

private data class DashboardStat(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: @Composable () -> Unit,
)

@Composable
fun HomeScreen(
    onOpenCamera: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    val repository = remember { KanjiFirestoreRepository() }
    var dailyKanjis by remember { mutableStateOf<List<KanjiEntry>>(emptyList()) }
    var totalCatalogKanjis by remember { mutableStateOf(0) }
    var selectedKanjiId by remember { mutableStateOf<String?>(null) }
    val selectedKanji = dailyKanjis.firstOrNull { it.id == selectedKanjiId }

    DisposableEffect(repository) {
        val catalogListener = repository.observeCatalogCount(
            onUpdate = { totalCatalogKanjis = it },
            onError = {}
        )
        val collectionListener = repository.observeUserCollection(
            onUpdate = { dailyKanjis = it },
            onError = {}
        )

        onDispose {
            catalogListener.remove()
            collectionListener?.remove()
        }
    }

    val stats = listOf(
        DashboardStat(
            title = stringResource(R.string.stat_total_kanjis),
            value = totalCatalogKanjis.toString(),
            subtitle = stringResource(R.string.stat_total_kanjis_sub),
            icon = { Icon(Icons.Outlined.AutoStories, contentDescription = null, tint = AppSecondary) }
        ),
        DashboardStat(
            title = stringResource(R.string.stat_viewed_kanjis),
            value = dailyKanjis.count { it.viewed }.toString(),
            subtitle = stringResource(R.string.stat_viewed_kanjis_sub),
            icon = { Icon(Icons.Outlined.RemoveRedEye, contentDescription = null, tint = AppSecondary) }
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                selectedTab = AppTab.HOME,
                onHome = {},
                onCamera = onOpenCamera,
                onSettings = onOpenSettings
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderCard(onLogout = onLogout) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.home_dashboard_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.home_dashboard_subtitle),
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
            item { WeeklyCard(kanjiCount = dailyKanjis.size) }
            item {
                TodaySection(
                    dailyKanjis = dailyKanjis,
                    onKanjiClick = { selectedKanjiId = it.id }
                )
            }
        }
    }

    selectedKanji?.let { item ->
        KanjiDetailsDialog(
            item = item,
            onDismiss = { selectedKanjiId = null },
            onToggleViewed = {
                repository.toggleViewed(item, onSuccess = {}, onError = {})
            },
            onDelete = {
                repository.deleteKanji(item.id, onSuccess = {}, onError = {})
                selectedKanjiId = null
            },
            onAddComment = { comment ->
                repository.addComment(item.id, comment, onSuccess = {}, onError = {})
            }
        )
    }
}

@Composable
private fun HeaderCard(onLogout: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .background(MaterialTheme.colorScheme.surfaceVariant),
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
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
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
private fun WeeklyCard(kanjiCount: Int) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    text = stringResource(R.string.weekly_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF536173)
                )
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = AppSecondary)
            }
            Text(
                text = kanjiCount.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = AppPrimary
            )
            Text(
                text = stringResource(R.string.weekly_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = AppTextMuted
            )
        }
    }
}

@Composable
private fun TodaySection(
    dailyKanjis: List<KanjiEntry>,
    onKanjiClick: (KanjiEntry) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    text = stringResource(R.string.discovered_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = AppPrimary
                )
            }
            Text(
                text = stringResource(R.string.discovered_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = AppTextMuted
            )

            if (dailyKanjis.isEmpty()) {
                EmptyCollectionCard()
            } else {
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
}

@Composable
private fun EmptyCollectionCard() {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.empty_collection_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.empty_collection_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = AppTextMuted
            )
        }
    }
}

@Composable
private fun DailyKanjiCard(
    item: KanjiEntry,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                text = stringResource(R.string.badge_viewed),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.label_meaning),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTextMuted
                )
                Text(
                    text = item.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.label_reading),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTextMuted
                )
                Text(
                    text = item.reading,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
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
private fun KanjiDetailsDialog(
    item: KanjiEntry,
    onDismiss: () -> Unit,
    onToggleViewed: () -> Unit,
    onDelete: () -> Unit,
    onAddComment: (String) -> Unit,
) {
    var commentText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC08101F))
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppPrimary)
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = item.kanji, style = MaterialTheme.typography.displayLarge, color = Color.White)
                            Text(text = item.meaning, style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.9f))
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = null, tint = Color.White)
                        }
                    }

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        DetailChipRow(item = item)

                        InfoBlock(title = stringResource(R.string.dialog_reading), value = item.reading)
                        InfoBlock(title = stringResource(R.string.dialog_heisig), value = item.heisig.ifBlank { stringResource(R.string.dialog_no_reference) })

                        if (item.comments.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = AppSecondary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.dialog_comments_title), style = MaterialTheme.typography.titleLarge, color = AppPrimary)
                                }
                                item.comments.forEach { comment ->
                                    CommentCard(comment)
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(stringResource(R.string.dialog_add_comment_title), style = MaterialTheme.typography.titleLarge, color = AppPrimary)
                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text(stringResource(R.string.dialog_comment_placeholder)) }
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(onClick = {
                                    onAddComment(commentText)
                                    commentText = ""
                                }) {
                                    Icon(Icons.Outlined.Send, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.dialog_btn_save))
                                }
                                OutlinedButton(onClick = onToggleViewed) {
                                    Text(if (item.viewed) stringResource(R.string.dialog_btn_mark_not_viewed) else stringResource(R.string.dialog_btn_mark_viewed))
                                }
                            }
                        }

                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD95C5C))
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.dialog_btn_delete))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailChipRow(item: KanjiEntry) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(item.jlpt, item.grade, "${item.strokeCount} traços").forEach { label ->
            Box(
                modifier = Modifier
                    .border(1.dp, Color(0xFFE0E6ED), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(text = label, style = MaterialTheme.typography.labelMedium, color = AppTextMuted)
            }
        }
    }
}

@Composable
private fun InfoBlock(title: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = AppPrimary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        HorizontalDivider(color = Color(0xFFE6ECF1))
    }
}

@Composable
private fun CommentCard(comment: KanjiComment) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = comment.text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = comment.date, style = MaterialTheme.typography.bodySmall, color = AppTextMuted)
        }
    }
}