package com.example.kanjilens.kanji.model

enum class JLPTLevel(val label: String) {
    N5("N5"),
    N4("N4"),
    N3("N3"),
    N2("N2"),
    N1("N1")
}
data class EncyclopediaKanjiDetail(
    val id: String,
    val kanji: String,
    val meaning: String,
    val onReadings: List<String>,
    val kunReadings: List<String>,
    val nameReadings: List<String> = emptyList(),
    val strokes: Int,
    val jlptLevel: JLPTLevel? = null,
    val note: String = "",
    val examples: List<Example> = emptyList()
)

data class Example(
    val kanji: String,
    val reading: String,
    val translation: String
)