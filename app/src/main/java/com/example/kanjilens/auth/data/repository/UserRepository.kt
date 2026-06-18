package com.example.kanjilens.auth.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class UserRepository {

    private val db = Firebase.firestore

    fun addCurrentUser(
        uid: String,
        name: String,
        email: String
    ) {
        val user = hashMapOf(
            "uid" to uid,
            "name" to name,
            "email" to email
        )

        db.collection("usuarios")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(
                    "UserRepository",
                    "Usuário salvo com sucesso"
                )
            }
            .addOnFailureListener { e ->
                Log.e(
                    "UserRepository",
                    "Erro ao salvar usuário",
                    e
                )
            }
    }
}