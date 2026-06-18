package com.example.kanjilens.auth.data.repository

import android.util.Log
import com.example.kanjilens.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

class AuthRepository {
    private val auth by lazy {
        if (BuildConfig.FIREBASE_ENABLED) Firebase.auth else null
    }

    fun getCurrentUser(): FirebaseUser? = auth?.currentUser

    fun signIn(email: String, password: String,
               onSuccess: () -> Unit,
               onError: (String) -> Unit) {
        val currentAuth = auth ?: run {
            onSuccess()
            return
        }

        currentAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro ao entrar") }
    }

    fun createAccount(  name: String,email: String, password: String,
                      onSuccess: () -> Unit,
                      onError: (String) -> Unit) {
        val currentAuth = auth ?: run {
            onSuccess()
            return
        }

        currentAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                currentAuth.currentUser?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener { onSuccess() }
                    ?.addOnFailureListener { onError(it.message ?: "Erro ao salvar nome") }
            }
            .addOnFailureListener { onError(it.message ?: "Erro ao criar conta") }
    }

    fun sendVerificationEmail(onDone: () -> Unit) {
        auth?.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { onDone() }
            ?: onDone()
    }

    fun signOut() {
        try {
            auth?.signOut()
            Log.d("AuthRepository", "Logout ok")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao deslogar: ${e.message}")
        }
    }
}
