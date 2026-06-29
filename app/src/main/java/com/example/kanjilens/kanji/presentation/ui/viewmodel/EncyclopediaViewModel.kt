package com.example.kanjilens.kanji.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kanjilens.kanji.model.JLPTLevel
import com.example.kanjilens.kanji.model.KanjiDetail
import com.example.kanjilens.kanji.model.Example
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.example.kanjilens.kanji.data.remote.KanjiResponse
import com.example.kanjilens.kanji.model.EncyclopediaKanjiDetail

class EncyclopediaViewModel : ViewModel() {

    // Dados mockados (substituir por repositório real)
    private val allKanjis = listOf(
        EncyclopediaKanjiDetail(
            id = "1",
            kanji = "日",
            meaning = "Sol, dia",
            onReadings = listOf("ニチ", "ジツ"),
            kunReadings = listOf("ひ", "か"),
            nameReadings = listOf("あき", "い", "く", "さ", "こ", "う", "す", "たち", "に", "にっ"),
            strokes = 4,
            jlptLevel = JLPTLevel.N5,
            note = "Um dos kanjis mais básicos. Representa o sol e também o Japão.",
            examples = listOf(
                Example("日本", "にほん", "Japão"),
                Example("毎日", "まいにち", "todo dia"),
                Example("日曜日", "にちようび", "domingo")
            )
        ),
        EncyclopediaKanjiDetail(
            id = "2",
            kanji = "月",
            meaning = "Lua, mês",
            onReadings = listOf("ゲツ", "ガツ"),
            kunReadings = listOf("つき"),
            strokes = 4,
            jlptLevel = JLPTLevel.N5
        ),
        EncyclopediaKanjiDetail(
            id = "3",
            kanji = "火",
            meaning = "Fogo",
            onReadings = listOf("カ"),
            kunReadings = listOf("ひ", "-び"),
            strokes = 4,
            jlptLevel = JLPTLevel.N5
        ),
        EncyclopediaKanjiDetail(
            id = "4",
            kanji = "水",
            meaning = "Água",
            onReadings = listOf("スイ"),
            kunReadings = listOf("みず", "みな-"),
            strokes = 4,
            jlptLevel = JLPTLevel.N5
        ),
        EncyclopediaKanjiDetail(
            id = "5",
            kanji = "木",
            meaning = "Árvore",
            onReadings = listOf("ボク", "モク"),
            kunReadings = listOf("き"),
            strokes = 4,
            jlptLevel = JLPTLevel.N5
        ),
        EncyclopediaKanjiDetail(
            id = "6",
            kanji = "金",
            meaning = "Ouro, dinheiro",
            onReadings = listOf("キン", "コン"),
            kunReadings = listOf("かね"),
            strokes = 8,
            jlptLevel = JLPTLevel.N5
        ),
        EncyclopediaKanjiDetail(
            id = "7",
            kanji = "人",
            meaning = "Pessoa",
            onReadings = listOf("ジン", "ニン"),
            kunReadings = listOf("ひと"),
            strokes = 2,
            jlptLevel = JLPTLevel.N5
        ),
        EncyclopediaKanjiDetail(
            id = "8",
            kanji = "本",
            meaning = "Livro, origem",
            onReadings = listOf("ホン"),
            kunReadings = listOf("もと"),
            strokes = 5,
            jlptLevel = JLPTLevel.N5
        )
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedLevel = MutableStateFlow<JLPTLevel?>(null)
    val selectedLevel: StateFlow<JLPTLevel?> = _selectedLevel

    private val _filteredKanjis = MutableStateFlow(allKanjis)
    val filteredKanjis: StateFlow<List<EncyclopediaKanjiDetail>> = _filteredKanjis

    init {
        viewModelScope.launch {
            combine(_searchQuery, _selectedLevel) { query, level ->
                allKanjis.filter { kanji ->
                    val matchesQuery = query.isEmpty() ||
                            kanji.kanji.contains(query) ||
                            kanji.meaning.contains(query, ignoreCase = true) ||
                            kanji.onReadings.any { it.contains(query) } ||
                            kanji.kunReadings.any { it.contains(query) }
                    val matchesLevel = level == null || kanji.jlptLevel == level
                    matchesQuery && matchesLevel
                }
            }.collect { filtered ->
                _filteredKanjis.value = filtered
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLevel(level: JLPTLevel?) {
        _selectedLevel.value = level
    }

}