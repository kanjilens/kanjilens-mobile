package com.example.kanjilens.kanji.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val kanji: String,
    val meaning: String,
    val reading: String,
    val viewed: Boolean = false,
)

@Composable
fun HomeScreen(onOpenCamera: () -> Unit) {
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
    val dailyKanjis = listOf(
        DailyKanji("緑", "affinity, border", "えにし, ふち, ふちどる", true),
        DailyKanji("雪", "snow", "ゆき, セツ"),
        DailyKanji("星", "star, spot", "ほし, -ぼし, セイ", true),
        DailyKanji("風", "wind, air", "かぜ, かざ-, フウ"),
    )

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
                HeaderCard()
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
                TodaySection(dailyKanjis = dailyKanjis)
            }
        }
    }
}

@Composable
private fun HeaderCard() {
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
                    .clickable(onClick = {})
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
private fun TodaySection(dailyKanjis: List<DailyKanji>) {
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
                dailyKanjis.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        rowItems.forEach { item ->
                            DailyKanjiCard(item = item, modifier = Modifier.width(cardWidth))
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

@Composable
private fun DailyKanjiCard(item: DailyKanji, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
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
                    style = MaterialTheme.typography.displayMedium,
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
                    text = "15/06/2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTextMuted
                )
            }
        }
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
