package com.example.kanjilens.kanji.data.cache

import com.example.kanjilens.kanji.model.KanjiDetail

object KanjiDetailCache {
    private val cache = mutableMapOf<String, KanjiDetail>()

    fun get(kanji: String): KanjiDetail? = cache[kanji]
    fun put(kanji: String, detail: KanjiDetail) { cache[kanji] = detail }
    fun has(kanji: String): Boolean = cache.containsKey(kanji)
}