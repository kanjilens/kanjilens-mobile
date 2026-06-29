package com.example.kanjilens.kanji.model
data class KanjiComment(
    val text: String,
    val date: String,
)
// O que vem do Firestore (relação usuário↔kanji)
data class UserKanji(
    val kanji: String = "",
    val addedAt: String = "",
    val status: String = "LEARNING",
    val category: String = "KANJI",
    val notes: List<KanjiComment> = emptyList(),
)

// O que vem da API (nunca salvo no Firestore)
data class KanjiDetail(
    val meaning: String = "",
    val reading: String = "",
    val strokeCount: Int = 0,
    val jlpt: String = "",
    val grade: String = "",
    val onReadings: List<String> = emptyList(),
    val kunReadings: List<String> = emptyList(),
    val nameReadings: List<String> = emptyList(),
    val heisig: String = "",
)

// O que a UI consome (merge dos dois)
data class KanjiEntry(
    val userKanji: UserKanji,
    val detail: KanjiDetail? = null, // null enquanto carrega da API
) {
    val kanji get() = userKanji.kanji
    val addedDate get() = userKanji.addedAt
    val comments get() = userKanji.notes
    val viewed get() = userKanji.status == "SEEN"
    val meaning get() = detail?.meaning ?: ""
    val reading get() = detail?.reading ?: ""
    val strokeCount get() = detail?.strokeCount ?: 0
    val jlpt get() = detail?.jlpt ?: "JLPT -"
    val grade get() = detail?.grade ?: "Grade -"
    val onReadings get() = detail?.onReadings ?: emptyList()
    val kunReadings get() = detail?.kunReadings ?: emptyList()
    val nameReadings get() = detail?.nameReadings ?: emptyList()
    val heisig get() = detail?.heisig ?: ""
    val id get() = userKanji.kanji // kanji é o ID do documento
}