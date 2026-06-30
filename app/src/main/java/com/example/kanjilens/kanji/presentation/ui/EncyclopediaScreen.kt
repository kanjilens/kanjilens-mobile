package com.example.kanjilens.kanji.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kanjilens.R
import com.example.kanjilens.kanji.model.EncyclopediaKanjiDetail
import com.example.kanjilens.kanji.model.JLPTLevel
import com.example.kanjilens.kanji.model.KanjiDetail
import com.example.kanjilens.kanji.presentation.ui.viewmodel.EncyclopediaViewModel
import com.example.kanjilens.ui.navigation.AppBottomBar
import com.example.kanjilens.ui.navigation.AppTab
import com.example.kanjilens.ui.theme.AppPrimary
import com.example.kanjilens.ui.theme.AppSecondary
import com.example.kanjilens.ui.theme.AppTextMuted
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncyclopediaScreen(
    onOpenHome: () -> Unit,
    onOpenDiscovery: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    viewModel: EncyclopediaViewModel = viewModel()
) {
    val filteredKanjis by viewModel.filteredKanjis.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedKanjiId by remember { mutableStateOf<String?>(null) }
    val selectedKanji = filteredKanjis.firstOrNull { it.id == selectedKanjiId }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                selectedTab = AppTab.ENCYCLOPEDIA, // ou DISCOVERY, depende do seu enum
                onHome = onOpenHome,
                onDiscovery = onOpenDiscovery,
                onEncyclopedia = {},
                onCamera = onOpenCamera,
                onSettings = onOpenSettings
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header (igual ao da Discovery)
            EncyclopediaHeader(onLogout = onLogout)

            // Título e subtítulo
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.encyclopedia_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.encyclopedia_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppTextMuted
                )
            }
            // Barra de pesquisa + seletor de nível "Todos"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo de pesquisa (ocupa o máximo de espaço)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.discovery_search_hint),
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary // Cor da lupa
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        // Bordas transparentes (sem borda visível)
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        // Fundo adaptável ao tema (claro/escuro)
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        // Cursor
                        cursorColor = MaterialTheme.colorScheme.primary,
                        // Cores do texto
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        // Ícone de pesquisa
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        // Placeholder (texto de exemplo)
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),

                    shape = RoundedCornerShape(12.dp), // Borda bem arredondada
                    singleLine = true
                )


                // Dropdown "Nível: Todos"


            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Texto "Nível:"
                Text(
                    text = "Nível:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Botão "Todos"
                val isAllSelected = selectedLevel == null
                // Botões N5..N1
                JLPTLevel.values().forEach { level ->
                    LevelFilterButton(
                        label = level.label,
                        isSelected = selectedLevel == level,
                        onClick = { viewModel.setLevel(level) },
                        modifier = Modifier
                    )
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                filteredKanjis.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.encyclopedia_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppTextMuted
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredKanjis) { kanji ->
                            EncyclopediaKanjiCard(
                                kanji = kanji,
                                onClick = { selectedKanjiId = kanji.id }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de detalhes (modal) - estilo da segunda imagem
    selectedKanji?.let { kanji ->
        KanjiDetailDialog(
            kanji = kanji,
            onDismiss = { selectedKanjiId = null }
        )
    }
}

// ---------- Componentes ----------

@Composable
private fun LevelFilterButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(36.dp),
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) AppPrimary else MaterialTheme.colorScheme.surface,

    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}
@Composable
private fun EncyclopediaHeader(onLogout: () -> Unit) {
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
                        color = MaterialTheme.colorScheme.onSecondary,
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

            // Botão de logout
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    }
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Logout, contentDescription = null, tint = AppTextMuted)
            }
        }
    }
}

@Composable
private fun EncyclopediaKanjiCard(
    kanji: EncyclopediaKanjiDetail,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kanji grande
            Text(
                text = kanji.kanji,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(80.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Significado
                Text(
                    text = kanji.meaning,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Leituras (on/kun)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = kanji.onReadings.joinToString("、"),
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTextMuted
                    )
                    Text(
                        text = "·",
                        color = AppTextMuted
                    )
                    Text(
                        text = kanji.kunReadings.joinToString("、"),
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTextMuted
                    )
                }

                // Traços e nível
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📅 ${kanji.strokes} traços",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTextMuted
                    )
                    if (kanji.jlptLevel != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AppSecondary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = kanji.jlptLevel.label,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = AppPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}