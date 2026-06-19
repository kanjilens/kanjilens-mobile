package com.example.kanjilens.kanji.data.remote

import com.example.kanjilens.kanji.model.KanjiComment
import com.example.kanjilens.kanji.model.KanjiEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KanjiFirestoreRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    fun hasAuthenticatedUser(): Boolean = auth.currentUser != null

    fun currentUserId(): String? = auth.currentUser?.uid

    fun observeCatalogCount(
        onUpdate: (Int) -> Unit,
        onError: (String) -> Unit,
    ): ListenerRegistration {
        return firestore.collection("kanji")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message ?: "Erro ao carregar catalogo de kanjis")
                    return@addSnapshotListener
                }

                onUpdate(snapshot?.size() ?: 0)
            }
    }

    fun observeUserCollection(
        onUpdate: (List<KanjiEntry>) -> Unit,
        onError: (String) -> Unit,
    ): ListenerRegistration? {
        val uid = currentUserId() ?: return null

        return firestore.collection("usuarios")
            .document(uid)
            .collection("colecao")
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message ?: "Erro ao carregar a colecao do usuario")
                    return@addSnapshotListener
                }

                val items = snapshot?.documents
                    ?.mapNotNull { document -> document.toKanjiEntry() }
                    .orEmpty()

                onUpdate(items)
            }
    }

    fun fetchCatalogKanjiBySymbol(
        symbol: String,
        onSuccess: (KanjiEntry?) -> Unit,
        onError: (String) -> Unit,
    ) {
        firestore.collection("kanji")
            .whereEqualTo("simbolo", symbol)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot.documents.firstOrNull()?.toCatalogKanjiEntry())
            }
            .addOnFailureListener {
                onError(it.message ?: "Erro ao consultar o kanji no Firestore")
            }
    }

    fun toggleViewed(
        item: KanjiEntry,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val uid = currentUserId() ?: run {
            onError("Usuario nao autenticado")
            return
        }

        firestore.collection("usuarios")
            .document(uid)
            .collection("colecao")
            .document(item.id)
            .update("viewed", !item.viewed)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.message ?: "Erro ao atualizar o estado do kanji")
            }
    }

    fun deleteKanji(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val uid = currentUserId() ?: run {
            onError("Usuario nao autenticado")
            return
        }

        firestore.collection("usuarios")
            .document(uid)
            .collection("colecao")
            .document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.message ?: "Erro ao excluir o kanji")
            }
    }

    fun addComment(
        id: String,
        text: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val uid = currentUserId() ?: run {
            onError("Usuario nao autenticado")
            return
        }
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            onSuccess()
            return
        }

        val commentData = mapOf(
            "text" to trimmed,
            "date" to currentDateLabel(),
        )

        firestore.collection("usuarios")
            .document(uid)
            .collection("colecao")
            .document(id)
            .update("comments", FieldValue.arrayUnion(commentData))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.message ?: "Erro ao adicionar comentario")
            }
    }

    fun saveScannedKanji(
        entry: KanjiEntry,
        comment: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val uid = currentUserId() ?: run {
            onError("Usuario nao autenticado")
            return
        }

        val trimmedComment = comment.trim()
        val payload = hashMapOf<String, Any>(
            "id" to entry.id,
            "kanji" to entry.kanji,
            "meaning" to entry.meaning,
            "reading" to entry.reading,
            "viewed" to entry.viewed,
            "strokeCount" to entry.strokeCount,
            "addedDate" to currentDateLabel(),
            "addedAt" to FieldValue.serverTimestamp(),
            "jlpt" to entry.jlpt,
            "grade" to entry.grade,
            "onReadings" to entry.onReadings,
            "kunReadings" to entry.kunReadings,
            "nameReadings" to entry.nameReadings,
            "heisig" to entry.heisig,
        )

        if (trimmedComment.isNotEmpty()) {
            payload["comments"] = listOf(
                mapOf(
                    "text" to trimmedComment,
                    "date" to currentDateLabel(),
                )
            )
        }

        firestore.collection("usuarios")
            .document(uid)
            .collection("colecao")
            .document(entry.id)
            .set(payload, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.message ?: "Erro ao salvar o kanji na colecao")
            }
    }

    private fun DocumentSnapshot.toCatalogKanjiEntry(): KanjiEntry? {
        val symbol = getString("simbolo") ?: getString("kanji") ?: return null
        val meanings = getStringList("meanings")
        val onReadings = getStringList("on_readings")
        val kunReadings = getStringList("kun_readings")
        val nameReadings = getStringList("name_readings")

        return KanjiEntry(
            id = id,
            kanji = symbol,
            meaning = getString("meaning") ?: meanings.joinToString(", "),
            reading = (kunReadings + onReadings).filter { it.isNotBlank() }.joinToString(", "),
            strokeCount = getLong("stroke_count")?.toInt() ?: getLong("strokeCount")?.toInt() ?: 0,
            jlpt = getLong("jlpt")?.toInt()?.let { "JLPT N$it" } ?: (getString("jlpt") ?: "JLPT -"),
            grade = getLong("grade")?.toInt()?.let { "Grade $it" } ?: (getString("grade") ?: "Grade -"),
            onReadings = onReadings,
            kunReadings = kunReadings,
            nameReadings = nameReadings,
            heisig = getString("heisig_en") ?: getString("heisig") ?: "",
        )
    }

    private fun DocumentSnapshot.toKanjiEntry(): KanjiEntry? {
        val kanji = getString("kanji") ?: getString("simbolo") ?: return null
        val meaning = getString("meaning") ?: getStringList("meanings").joinToString(", ")
        val onReadings = getStringList("onReadings").ifEmpty { getStringList("on_readings") }
        val kunReadings = getStringList("kunReadings").ifEmpty { getStringList("kun_readings") }
        val nameReadings = getStringList("nameReadings").ifEmpty { getStringList("name_readings") }
        val comments = (get("comments") as? List<*>)
            ?.mapNotNull { raw ->
                val map = raw as? Map<*, *> ?: return@mapNotNull null
                val text = map["text"] as? String ?: return@mapNotNull null
                val date = map["date"] as? String ?: currentDateLabel()
                KanjiComment(text = text, date = date)
            }
            .orEmpty()

        val addedDate = getString("addedDate")
            ?: getTimestamp("addedAt")?.toDate()?.formatDateLabel()
            ?: currentDateLabel()

        return KanjiEntry(
            id = id,
            kanji = kanji,
            meaning = meaning,
            reading = getString("reading")
                ?: (kunReadings + onReadings).filter { it.isNotBlank() }.joinToString(", "),
            viewed = getBoolean("viewed") ?: false,
            strokeCount = getLong("strokeCount")?.toInt() ?: getLong("stroke_count")?.toInt() ?: 0,
            addedDate = addedDate,
            jlpt = getString("jlpt") ?: getLong("jlpt")?.toInt()?.let { "JLPT N$it" } ?: "JLPT -",
            grade = getString("grade") ?: getLong("grade")?.toInt()?.let { "Grade $it" } ?: "Grade -",
            onReadings = onReadings,
            kunReadings = kunReadings,
            nameReadings = nameReadings,
            comments = comments,
            heisig = getString("heisig") ?: getString("heisig_en") ?: "",
        )
    }

    private fun DocumentSnapshot.getStringList(field: String): List<String> {
        val value = get(field) ?: return emptyList()
        return when (value) {
            is String -> listOf(value)
            is List<*> -> value.mapNotNull { item -> item?.toString()?.takeIf { it.isNotBlank() } }
            else -> emptyList()
        }
    }

    private fun currentDateLabel(): String {
        return Date().formatDateLabel()
    }

    private fun Date.formatDateLabel(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this)
    }
}
