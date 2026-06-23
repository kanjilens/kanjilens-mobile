package com.example.kanjilens.kanji.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kanjilens.kanji.data.remote.KanjiFirestoreRepository
import com.example.kanjilens.kanji.model.KanjiEntry
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    private val repository: KanjiFirestoreRepository = KanjiFirestoreRepository()
) : ViewModel() {

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
                _kanjis.value = newList
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
}