package com.example.kanjilens.auth.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signIn(email: String, password: String,
               onSuccess: () -> Unit,
               onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro ao entrar") }
    }

    fun createAccount(name: String, email: String, password: String,
                      onSuccess: () -> Unit,
                      onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user ?: run {
                    onError("Nao foi possivel criar a conta")
                    return@addOnSuccessListener
                }

                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()

                user.updateProfile(profile)
                    .addOnCompleteListener {
                        firestore.collection("usuarios")
                            .document(user.uid)
                            .set(
                                mapOf(
                                    "uid" to user.uid,
                                    "name" to name.trim(),
                                    "email" to email.trim(),
                                    "createdAt" to FieldValue.serverTimestamp(),
                                )
                            )
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener {
                                Log.w("AuthRepository", "Falha ao salvar usuario no Firestore: ${it.message}")
                                onSuccess()
                            }
                    }
            }
            .addOnFailureListener { onError(it.message ?: "Erro ao criar conta") }
    }

    fun sendVerificationEmail(onDone: () -> Unit) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { onDone() }
            ?: onDone()
    }

    fun signout() {
        try {
            auth.signOut()
            Log.d("AuthRepository", "Logout ok")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao deslogar: ${e.message}")
        }
    }
}
