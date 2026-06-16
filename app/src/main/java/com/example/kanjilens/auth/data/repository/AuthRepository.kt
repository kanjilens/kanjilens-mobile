package com.example.kanjilens.auth.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class AuthRepository {
    private val auth = Firebase.auth

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signIn(email: String, password: String,
               onSuccess: () -> Unit,
               onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro ao entrar") }
    }

    fun createAccount(email: String, password: String,
                      onSuccess: () -> Unit,
                      onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro ao criar conta") }
    }

    fun sendVerificationEmail(onDone: () -> Unit) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { onDone() }
    }

    fun signout(){
         try {
            Firebase.auth.signOut()
            Log.d("AuthRepository", "Logout ok")

        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao deslogar: ${e.message}")
        }
    }
}