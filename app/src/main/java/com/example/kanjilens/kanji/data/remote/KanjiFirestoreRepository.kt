package com.example.kanjilens.kanji.data.remote

import com.example.kanjilens.kanji.model.KanjiComment
import com.example.kanjilens.kanji.model.KanjiDetail
import com.example.kanjilens.kanji.model.KanjiEntry
import com.example.kanjilens.kanji.model.UserKanji
import com.google.firebase.Timestamp
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
import java.util.UUID

import java.util.Calendar

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
        val uid = currentUserId() ?: return firestore.collection("users")
            .addSnapshotListener { _, _ -> onUpdate(0) }

        return firestore.collection("users")
            .document(uid)
            .collection("kanjis")
            .addSnapshotListener { snapshot, error ->
                onUpdate(snapshot?.size() ?: 0)
            }
    }

    fun observeUserCollection(
        onUpdate: (List<UserKanji>) -> Unit,
        onError: (String) -> Unit,
    ): ListenerRegistration? {
        val uid = currentUserId() ?: return null

        return firestore.collection("users")
            .document(uid)
            .collection("kanjis")
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { onError(error.message ?: "Erro"); return@addSnapshotListener }
                val items = snapshot?.documents?.mapNotNull { it.toUserKanji() }.orEmpty()
                onUpdate(items)
            }
    }

    fun toggleViewed(item: KanjiEntry, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = currentUserId() ?: run { onError("Usuario nao autenticado"); return }
        val newStatus = if (item.viewed) "LEARNING" else "SEEN"

        firestore.collection("users").document(uid)
            .collection("kanjis").document(item.kanji)
            .update("status", newStatus)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro") }
    }

    fun deleteKanji(kanji: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = currentUserId() ?: run { onError("Usuario nao autenticado"); return }

        firestore.collection("users").document(uid)
            .collection("kanjis").document(kanji)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro") }
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

        val noteData = mapOf(
            "id" to UUID.randomUUID().toString(),
            "text" to trimmed,
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        firestore.collection("users").document(uid)
            .collection("kanjis")
            .document(id)
            .update("notes", FieldValue.arrayUnion(noteData))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.message ?: "Erro ao adicionar comentario")
            }
    }

    fun saveScannedKanji(
        kanji: String,          // ← muda de KanjiEntry para String
        comment: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val uid = currentUserId() ?: run { onError("Usuario nao autenticado"); return }

        val notes = if (comment.isBlank()) emptyList<Map<String, Any>>()
        else listOf(mapOf(
            "id" to java.util.UUID.randomUUID().toString(),
            "text" to comment.trim(),
            "createdAt" to com.google.firebase.Timestamp.now()
        ))

        val payload = hashMapOf<String, Any>(
            "kanji" to kanji,
            "addedAt" to FieldValue.serverTimestamp(),
            "status" to "LEARNING",
            "category" to "KANJI",
            "notes" to notes,
        )

        firestore.collection("users")
            .document(uid)
            .collection("kanjis")
            .document(kanji)           // ← kanji é o ID do documento
            .set(payload, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro ao salvar") }
    }


   private fun DocumentSnapshot.toUserKanji(): UserKanji? {
    val kanji = getString("kanji") ?: return null
    val notes = (get("notes") as? List<*>)?.mapNotNull { raw ->
        val map = raw as? Map<*, *> ?: return@mapNotNull null
        val text = map["text"] as? String ?: return@mapNotNull null
        val date = (map["createdAt"] as? com.google.firebase.Timestamp)
            ?.toDate()?.formatDateLabel() ?: currentDateLabel()
        KanjiComment(text = text, date = date)
    }.orEmpty()

    return UserKanji(
        kanji = kanji,
        addedAt = getTimestamp("addedAt")?.toDate()?.formatDateLabel() ?: currentDateLabel(),
        status = getString("status") ?: "LEARNING",
        category = getString("category") ?: "KANJI",
        notes = notes,
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
