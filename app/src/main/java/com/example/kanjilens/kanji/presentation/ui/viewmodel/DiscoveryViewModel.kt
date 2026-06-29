package com.example.kanjilens.kanji.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kanjilens.kanji.data.cache.KanjiDetailCache
import com.example.kanjilens.kanji.data.remote.KanjiApi
import com.example.kanjilens.kanji.data.remote.KanjiFirestoreRepository
import com.example.kanjilens.kanji.data.remote.KanjiResponse
import com.example.kanjilens.kanji.model.KanjiDetail
import com.example.kanjilens.kanji.model.KanjiEntry
import com.example.kanjilens.kanji.model.UserKanji
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscoveryViewModel(
    private val repository: KanjiFirestoreRepository = KanjiFirestoreRepository()
) : ViewModel() {

    private val _userKanjis = MutableStateFlow<List<UserKanji>>(emptyList())
    private val _kanjis = MutableStateFlow<List<KanjiEntry>>(emptyList())
    val kanjis: StateFlow<List<KanjiEntry>> = _kanjis.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredKanjis: StateFlow<List<KanjiEntry>> = combine(
        _kanjis,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter { entry ->
            entry.kanji.contains(query, ignoreCase = true) ||
                    entry.meaning.contains(query, ignoreCase = true) ||
                    entry.reading.contains(query, ignoreCase = true) ||
                    entry.heisig.contains(query, ignoreCase = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    private var collectionListener: ListenerRegistration? = null

    init {
        collectionListener = repository.observeUserCollection(
            onUpdate = { newList ->
                _userKanjis.value = newList
                _kanjis.value = newList.map { KanjiEntry(it, KanjiDetailCache.get(it.kanji)) }
                newList.forEach { uk ->
                    if (!KanjiDetailCache.has(uk.kanji)) {
                        KanjiApi.service.getKanji(uk.kanji).enqueue(object :
                            Callback<KanjiResponse> {
                            override fun onResponse(call: Call<KanjiResponse>, response: Response<KanjiResponse>) {
                                response.body()?.toKanjiDetail()?.let { detail ->
                                    KanjiDetailCache.put(uk.kanji, detail)
                                    _kanjis.value = _userKanjis.value.map { u ->
                                        KanjiEntry(u, KanjiDetailCache.get(u.kanji))
                                    }
                                }
                            }
                            override fun onFailure(call: Call<KanjiResponse>, t: Throwable) {}
                        })
                    }
                }
            },
            onError = { error ->
                // tratar erro (log, toast etc.)
            }
        )
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleViewed(kanji: KanjiEntry, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            repository.toggleViewed(kanji, onSuccess, onError)
        }
    }

    fun deleteKanji(kanjiId: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteKanji(kanjiId, onSuccess, onError)
        }
    }

    fun addComment(kanjiId: String, comment: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            repository.addComment(kanjiId, comment, onSuccess, onError)
        }
    }

    override fun onCleared() {
        super.onCleared()
        collectionListener?.remove()
    }
    private fun KanjiResponse.toKanjiDetail(): KanjiDetail = KanjiDetail(
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