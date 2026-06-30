package com.example.kanjilens.kanji.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.kanjilens.kanji.model.KanjiEntry
import com.example.kanjilens.kanji.presentation.viewmodel.DiscoveryViewModel
import com.example.kanjilens.ui.navigation.AppBottomBar
import com.example.kanjilens.ui.navigation.AppTab
import com.example.kanjilens.ui.theme.AppPrimary
import com.example.kanjilens.ui.theme.AppSecondary
import com.example.kanjilens.ui.theme.AppTextMuted
import com.google.firebase.auth.FirebaseAuth
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    onOpenHome: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenEncyclopedia:() -> Unit,
    onLogout: () -> Unit,
    viewModel: DiscoveryViewModel = viewModel()
) {
    val filteredKanjis by viewModel.filteredKanjis.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedKanjiId by remember { mutableStateOf<String?>(null) }
    val selectedKanji = filteredKanjis.firstOrNull { it.id == selectedKanjiId }
    var expanded by remember { mutableStateOf(false) }

    val selectedLevel by viewModel.selectedJlpt.collectAsState()

    val selectedText = when (selectedLevel) {
        null -> stringResource(R.string.all)
        5 -> "N5"
        4 -> "N4"
        3 -> "N3"
        2 -> "N2"
        1 -> "N1"
        else -> stringResource(R.string.all)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                selectedTab = AppTab.DISCOVERY,
                onHome = onOpenHome,
                onDiscovery = { /* já estamos aqui */ },
                onEncyclopedia = onOpenEncyclopedia,
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
            // Header (igual ao da Home, mas com botão de adicionar)
            DiscoveryHeader(onLogout= onLogout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ){
                    Text(
                    text = stringResource(R.string.discovery_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
                    Text(
                        text = stringResource(R.string.discovery_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTextMuted,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Botão +
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onOpenCamera)
                        .background(AppSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.discovery_add),
                        tint = Color.White
                    )
                }
            }
            // Barra de pesquisa + filtro "Todos" na mesma linha
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
                            color = if (isSystemInDarkTheme()) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                Color(0xFF6A7282)
                            }
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
                        focusedPlaceholderColor = if (isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            Color(0xFF6A7282)  // #6A7282
                        },
                        unfocusedPlaceholderColor = if (isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            Color(0xFF6A7282)  // #6A7282
                        }
                    ),

                    shape = RoundedCornerShape(12.dp), // Borda bem arredondada
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .menuAnchor()
                            .height(55.dp)
                            .clickable { expanded = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedText,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(Modifier.width(4.dp))

                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.all)) },
                            onClick = {
                                viewModel.setJlptFilter(null)
                                expanded = false
                            }
                        )

                        listOf(5, 4, 3, 2, 1).forEach { level ->

                            DropdownMenuItem(
                                text = {
                                    Text("N$level")
                                },
                                onClick = {
                                    viewModel.setJlptFilter(level)
                                    expanded = false
                                }
                            )

                        }
                    }
                }
            }

            // Lista ou vazio
            if (filteredKanjis.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.discovery_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppTextMuted
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Divide a lista em grupos de 2
                    filteredKanjis.chunked(2).forEach { rowItems ->
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowItems.forEach { kanji ->
                                    // Cada card ocupa metade da largura (com peso)
                                    DiscoveryKanjiCard(
                                        kanji = kanji,
                                        onClick = { selectedKanjiId = kanji.id },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Se a linha tiver apenas 1 item, preenche o espaço vazio
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de detalhes (reutilizado)
    selectedKanji?.let { kanji ->
        KanjiDetailsDialog(
            item = kanji,
            onDismiss = { selectedKanjiId = null },
            onToggleViewed = { viewModel.toggleViewed(kanji) },
            onDelete = {
                viewModel.deleteKanji(kanji.kanji )
                selectedKanjiId = null
            },
            onAddComment = { comment -> viewModel.addComment(kanji.kanji , comment) }
        )
    }
}

@Composable
private fun DiscoveryHeader( onLogout:() -> Unit) {
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

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
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
private fun DiscoveryKanjiCard(
    kanji: KanjiEntry,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = kanji.kanji,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.discovery_meaning_label),
                style = MaterialTheme.typography.labelSmall,
                color = AppTextMuted
            )
            Text(
                text = kanji.meaning,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.discovery_reading_label),
                style = MaterialTheme.typography.labelSmall,
                color = AppTextMuted
            )
            Text(
                text = kanji.reading,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = kanji.addedDate,
                style = MaterialTheme.typography.bodySmall,
                color = AppTextMuted
            )
        }
    }
}