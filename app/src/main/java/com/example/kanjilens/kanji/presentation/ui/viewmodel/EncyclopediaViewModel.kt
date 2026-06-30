package com.example.kanjilens.kanji.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kanjilens.kanji.data.cache.KanjiDetailCache
import com.example.kanjilens.kanji.data.local.JlptKanjiList
import com.example.kanjilens.kanji.data.remote.KanjiApi
import com.example.kanjilens.kanji.data.remote.KanjiResponse
import com.example.kanjilens.kanji.model.EncyclopediaKanjiDetail
import com.example.kanjilens.kanji.model.JLPTLevel
import com.example.kanjilens.kanji.model.KanjiDetail
import com.example.kanjilens.kanji.presentation.ui.toKanjiDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EncyclopediaViewModel : ViewModel() {

    private val _allKanjis = MutableStateFlow<List<EncyclopediaKanjiDetail>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedLevel = MutableStateFlow(JLPTLevel.N5)
    val selectedLevel: StateFlow<JLPTLevel?> = _selectedLevel

    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> = _isLoading

    private val _filteredKanjis = MutableStateFlow<List<EncyclopediaKanjiDetail>>(emptyList())
    val filteredKanjis: StateFlow<List<EncyclopediaKanjiDetail>> = _filteredKanjis

    private val loadedLevels = mutableSetOf<JLPTLevel>()

    init {
        // Carrega N5 automaticamente ao abrir a tela
        loadLevel(JLPTLevel.N5)

        viewModelScope.launch {
            combine(_allKanjis, _searchQuery, _selectedLevel) { list, query, level ->
                list.filter { kanji ->
                    val matchesQuery =
                        query.isEmpty() ||
                                kanji.kanji.contains(query) ||
                                kanji.meaning.contains(query, ignoreCase = true) ||
                                kanji.onReadings.any { it.contains(query) } ||
                                kanji.kunReadings.any { it.contains(query) }

                    val matchesLevel = kanji.jlptLevel == level

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

    fun setLevel(level: JLPTLevel) {
        _selectedLevel.value = level
        loadLevel(level)
    }

    private fun loadLevel(level: JLPTLevel) {
        if (loadedLevels.contains(level)) return
        loadedLevels.add(level)

        val symbols = JlptKanjiList.forLevel(level)
        _isLoading.value = true
        var pending = symbols.size

        if (pending == 0) {
            _isLoading.value = false
            return
        }

        symbols.forEach { symbol ->
            val cached = KanjiDetailCache.get(symbol)
            if (cached != null) {
                addToAllKanjis(symbol, cached, level)
                pending--
                if (pending == 0) _isLoading.value = false
            } else {
                KanjiApi.service.getKanji(symbol).enqueue(object : Callback<KanjiResponse> {
                    override fun onResponse(call: Call<KanjiResponse>, response: Response<KanjiResponse>) {
                        response.body()?.toKanjiDetail()?.let { detail ->
                            KanjiDetailCache.put(symbol, detail)
                            addToAllKanjis(symbol, detail, level)
                        }
                        pending--
                        if (pending == 0) _isLoading.value = false
                    }

                    override fun onFailure(call: Call<KanjiResponse>, t: Throwable) {
                        pending--
                        if (pending == 0) _isLoading.value = false
                    }
                })
            }
        }
    }

    private fun addToAllKanjis(symbol: String, detail: KanjiDetail, level: JLPTLevel) {
        val entry = EncyclopediaKanjiDetail(
            id = symbol,
            kanji = symbol,
            meaning = detail.meaning,
            onReadings = detail.onReadings,
            kunReadings = detail.kunReadings,
            nameReadings = detail.nameReadings,
            strokes = detail.strokeCount,
            jlptLevel = level,
            note = "",
            examples = emptyList()
        )
        val current = _allKanjis.value
        if (current.none { it.id == entry.id }) {
            _allKanjis.value = current + entry
        }
    }

    private fun KanjiResponse.toKanjiApiDetail(): KanjiDetail = KanjiDetail(
        meaning = meanings.joinToString(", "),
        reading = (kun_readings + on_readings).joinToString(", "),
        strokeCount = stroke_count ?: 0,
        jlpt = jlpt?.let { "JLPT N$it" } ?: "JLPT -",
        grade = grade?.let { "Grade $it" } ?: "Grade -",
        onReadings = on_readings,
        kunReadings = kun_readings,
        nameReadings = name_readings,
        heisig = heisig_en.orEmpty()
    )
}