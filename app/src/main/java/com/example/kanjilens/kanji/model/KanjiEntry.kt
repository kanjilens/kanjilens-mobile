package com.example.kanjilens.kanji.model

data class KanjiComment(
    val text: String,
    val date: String,
)

data class KanjiEntry(
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
    val nameReadings: List<String> = emptyList(),
    val comments: List<KanjiComment> = emptyList(),
    val heisig: String = "",
)
