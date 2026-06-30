package com.example.kanjilens.kanji.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kanjilens.kanji.model.EncyclopediaKanjiDetail
import com.example.kanjilens.ui.theme.AppPrimary
import com.example.kanjilens.ui.theme.AppSecondary
import com.example.kanjilens.ui.theme.AppTextMuted

@Composable
fun KanjiDetailDialog(
    kanji:  EncyclopediaKanjiDetail,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Kanji grande centralizado
                Text(
                    text = kanji.kanji,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Traços e nível
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 ${kanji.strokes} traços",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTextMuted
                    )
                    Spacer(Modifier.width(16.dp))
                    if (kanji.jlptLevel != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AppSecondary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = kanji.jlptLevel.label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = AppPrimary
                            )
                        }
                    }
                }

                // Leituras ON
                DetailRow(
                    label = "LEITURAS ON (音読み)",
                    value = kanji.onReadings.joinToString("、")
                )

                // Leituras KUN
                DetailRow(
                    label = "LEITURAS KUN (訓読み)",
                    value = kanji.kunReadings.joinToString("、")
                )

                // Leituras de nomes (se houver)
                if (kanji.nameReadings.isNotEmpty()) {
                    DetailRow(
                        label = "LEITURAS DE NOMES",
                        value = kanji.nameReadings.joinToString("、")
                    )
                }

                // Significados
                DetailRow(
                    label = "SIGNIFICADOS",
                    value = kanji.meaning
                )

                // Nota (se houver)
                if (kanji.note.isNotBlank()) {
                    DetailRow(
                        label = "NOTA",
                        value = kanji.note
                    )
                }

                // Exemplos de uso
                if (kanji.examples.isNotEmpty()) {
                    Text(
                        text = "EXEMPLOS DE USO",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    kanji.examples.forEach { example ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = example.kanji,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = example.reading,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppTextMuted
                                )
                                Text(
                                    text = example.translation,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AppTextMuted,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}